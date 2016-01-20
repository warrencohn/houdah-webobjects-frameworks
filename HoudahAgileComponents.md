# HoudahAgileComponents #

This is where HoudahControlComponents and the HoudahRuleEngine meet. The HoudahAgileComponents framework extends the controller classes by concrete subclasses. The role of these subclasses is to provide descriptor objects by calling into the rule engine.

The core of the framework is the DescriptorFactory. Here descriptor objects are instantiated as specified by the rules found in .agile.d2wmodel files applied to a current context.

The HoudahAgileComponents framework holds a default.agile.d2wmodel file. This provides sensible defaults. Indeed it derives the way entities, attributes and relationships are handled from the way they are modeled. E.g. it knows to show dates in a textfield using a NSTimestampFormatter. It also knows that if a to-many relationship owns its destination objects, new objects need to be created to add to the relationship. If the relationship were not the owner of the target objects, additional entries would need to be selected among existing objects.

Client applications or frameworks should declare their own default.agile.d2wmodel file. It is recommended that rules specific to a given entity should be factored into a separate file named after the entity. An application project would thus have one model file for each entity plus the default.agile.d2wmodel to hold cross-entity rules. Application-level rules should have a priority in the 100-110 range.

## Usage ##

The agile control components first ask the rule engine which cells to create.

The AgileSearchPageController will ask the rule engine for a list of named fieldsets. A default rule will resolve to the single field set named fieldset. For each field set it will ask for a list of fields. This should return an array of arrays: cells grouped by rows.

`((entityName = 'User') and (task = 'search') and (fieldset = 'fieldset'))`

`=> fields = ((name, login), (toUserType), (toChangedBy)) [100]`

For each of the fields, the controller will ask the rule engine for a desciptorClass to use. The HoudahAgileComponents framework provides sensible defaults based upon the fields type and properties. In order to be able to instantiate the desired Descriptor, the DesciptorFactory will ask the rule context for the descriptor’s signature and arguments. Rules to provide these values are already set-up for all Descriptors defined by the framework. Both values are NSArrays. The first describes the constructor’s signature. The second gives the arguments names. For each of the arguments the factory calls back into the rule context with the argument’s name in order to get a value. You may provide values for any of these arguments.

We should note the following special argument types:

  * Descriptor subclasses: These cause a recursive call into the factory.
  * Action subclasses: These also cause a recursive call. Here we ask the rule engine for an actionClass to instantiate. To do so the value provided for argument is passed back into the rule engine as actionType.
  * Class: The argument value is the name of a Class object to look up.
  * Format: The argument value is the name of a formatter declared in the FormatterFactory
  * Describe: The argument should be the name of an entity for which to get the Describer.
  * Identify: The argument should be the name of an entity for which to get the Identifier.

Other controllers behave in a similar manner. The AgileDetailPageController’s initial request will be for an array of properties. The AgileListPageController needs an array of columns. The AgileEditPageController shares the same requirements as the AgileSearchPageController. When setting up rules you may distinguish between these two by task name. The AgileQuickSearchPageController wants to know the fields of a fieldset named quickSearch.


## Special tasks ##

Two special tasks are not meant to map to pages: describe and identify. They serve to define the Describers and Identifiers. For each entity, the framework will ask the rule engine to provide a formatter for both tasks. The formatter needs to be a String reorientation of a KeyFormatterFormatter. Alternatively you may provide a formatterName for callback into the FormatterFactory.