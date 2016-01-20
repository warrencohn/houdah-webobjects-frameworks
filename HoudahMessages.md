# HoudahMessages #

The object of the HoudahMessages framework is to present rich messages to the user. The HoudahMessages framework is powered by an instance of the rule engine. The use of rules with an LHS-qualifier specifying a language allows for easy localization of messages. The responsibility to publish the desired language to the rule context remains with the controller layer.

Messages are identified by a unique message code. This is a string value that the rule engine may use to retrieve the actual message.

Examples:

`(messageCode = “nameLogin”) `

`=> message = “The name and login may not be equal” [50]`

`(messageCode = “nameLogin”) and (language = “French”) `

`=> message = “Le login doit être différent du nom” [50]`


The RHS-values of such rules are message templates. They may contain variables that call back into the rule engine. Example:

`(messageCode = “notNull”) `

`message = “The property '$label' may not be null” [0]`

The HoudahMessages defines the MessageException interface, which may be implemented by exceptions that wish to provide additional contextual information to the HoudahMessages framework. The BEVValidationException thrown by the HoudahEOValidation framework implements this interface. Indeed the framework keeps tabs on all keys referenced in its rule engine during validation process. When a validation method throws an exception it includes current values for these keys. This contextual information includes values for the “arguments” passed to validation methods.

When processing exceptions, the HoudahMessages framework folds their contextual information into the current rule context and thus makes it available to message templates. With this information it is possible to produce rich and easy to understand error message. Example:

`(messageCode = “minLength”) `

`message = “The property '$label' has a minimum length of $minLength” [0]`

The HoudahMessageFramework’s rule engine loads rule files named .message.d2wmodel where it finds message templates by their code. It also loads the labels.dictionary.d2wmodel files. This allows it to derive localized values for $label variables.