# HoudahEOValidation #

The HoudahEOValidation implements rule-based business object validation. Apple’s EOControl defines a life cycle for enterprise objects (EOs). This life cycle includes a validation phase. The business object may provide various callback methods to do either attribute-based validation or cross check validation. Validation may modify values or prohibit an insertion, update or deletion in the underlying persistence layer.

The EOAccess layer plugs into this system by providing its own class description: EOEntityClassDescription. Some of the validation work is delegated to the class description. EOEntityClassDescription uses the opportunity to validate against a couple of simple rules that may be set in the entity model. E.g. required attributes, mandatory relationships, value widths, … .

The HoudahEOValidation hooks into this framework and extends it. It is important to note that all the original mechanisms remain intact. They may still be used were found to be more appropriate than rule based validation.

For an EO to benefit from the enhanced validation system, it needs to extend the ValidatingRecord class. For this to activate, the EO must be managed by an instance of ValidatingEditingContext. The HoudahEOValidation – transparently to the caller – replaces the EOEntityClassDescription by an enhanced subclass: ValidatingEntityClassDescription. This class is dependant on the EOAccess layer. This implies that rule based validation is available only on the server side. The ValidatingRecord itself of course remains independent of the EOAccess layer. It may thus safely be used in environments lacking EOAccess. It then falls back to standard EOF validation.

The HoudahEOValidation comes preconfigured with a set of rules that cover the same ground as EOF’s model based validation. Those rules get called before EOEntityClassDescription gets to validate properties. In effect this implies that the model validation should no longer fire exceptions: all errors are caught before that layer of validation is reached. The benefit of this approach is that we can now provide context-enhanced exceptions that integrate with our message framework to provide localizability.

Validation rules are stored in .valid.d2wmodel rule files. We actually distinguish types of such files. The first one is always named rules.valid.d2wmodel. It contains only rules with the ruleName RHS-key. Such rules should only be found in these files. Additionally we find an arbitrary number of model files specifying arguments to said rules. Typically we would create on such file per entity as well as a defaultArguments.valid.d2wmodel files for arguments applying to all entities. The reason to distinguish these files is readability. The separation does not matter at runtime. At runtime however, rules with RHS-key of “ruleName” show a very particular behavior. Indeed for these rules the priority value is interpreted as a sort order rather than a priority.

When validating an EO, the HoudahEOValidation framework first validates the object’s attributes and then its relationships. The rule model may also define an array of  additionalKeys. These are abstract keys that don’t exist as such on the EO. Their purpose is to implement cross checks. These are performed last and in the order specified by the array. E.g. you could specify an additional key of “nameLogin” in order to implement a validation comparing the name to the login property.

The framework creates a rule context that it feeds with contextual information. For one it notes the task currently performed: insert, update or delete. It also informs the context about the current entity, key, attribute and relationship.

From that context we first derive a set of ruleName values. When queried for ruleName, the context will return a list of rule names in the order in which they need be checked. For each value for priority, it finds the rule best matching the current context.

Example:

`((not (attribute = null)) and (attribute.allowsNull = 0))`

`	=> ruleName = notNull [0]`

`((not (relationship = null)) and (relationship.isToMany = 0) and (relationship.isMandatory = 1))`

`	=> ruleName = mandatoryToOne [0]`

`((not (attribute = null)) and (attribute.width > 0))`

`	=> ruleName = width [10]`

`(key = “name”) `

`	 => ruleName = someRule [50]`

`(entity.name = “User”) and (key = “name”) `

`	 => ruleName = minLength [50]`


The rules to apply for the User.name attribute are in order: notNull, width, minLength.

The mandatoryToOne does not apply, as its LHS-qualifier does not match the current context. At the same sort order, the minLength rule wins over someRule. Indeed it has the more specific LHS.


For each rule the engine needs to know the method to call in order to perform the actual validation. The rule engine knows the method name by the method key. It is specified as the name of a class followed by a hash and the name of a static method on that class. The HoudahEOValidation comes with a set of predefined rules, which should cover 80%-90% of your needs. For these ruleNames the methods are already defined. E.g. the method for the notNull rule is known as "com.houdah.eovalidation.validation.ModelValidation#notNull".

The validation methods must follow the following signature:

`public static Object notNull(`

`	 ValidatingRecord record,`

`	 String key,`

`	 Object value,`

`	 ValidationContext validationContext)`

`throws BEVValidationException`


The method is passed the object being validated, the key being worked on, where possible the suggested value for the key as well as the current validation rule context. While performing simple property checks, the method is passed a current value and may return an alternative value. When performing crosschecks, the key is abstract and no value can be passed nor returned.

The validation methods have thus access to the validation rule context. From this they may derive argument values. E.g. the minLength rule needs a minLength argument. The framework defines a zero default value for this. You may override this default using a higher priority rule.

The utility classes in the HoudahEOValidation framework define a set of validation methods for the base types: Number, String and Date. They also re-implement model-based validation.

When no method name is explicitly specified, a rule makes the framework default to "com.houdah.eovalidation.validation.DefaultValidation#qualifier". This rule takes a single argument: qualifier. This key should resolve to a String representation of an EOQualifier. An EO is valid as long as it matches this qualifier. A validation exception is raised if it fails to match. This mechanism allows you to create most generic checks and crosschecks by writing argument rules rather than code.

When validation fails an exception is thrown. This exception carries an error code: a String that takes the place of the error message. The caller is expected to use the HoudahMessages framework to interpret said code. The exception also carries context information. By default this context contains a subset of the knowledge acquired by the validation rule context. In fact it contains the values for all keys actually referred to in the process of validation. This context may be called upon by the HoudahMessages framework to create rich error messages.

When required you may write your own validation methods and plug them into the system by providing a ruleName and method declaration. A such method must match the expected signature. It should also try its best to provide contextual information to exceptions it throws.

Methods that are generic in the sense that they may apply to many EOs should be factored into a common utility class. Specific methods are best located on the EO class they relate to.