# HoudahFoundation #

The HoudahFoundation framework builds upon and extends Apple’s
WebObjects Foundation framework. As such it also relies on the JDK’s core
libraries.

The WebObjects Foundation framework provides a set collection types
(NSArray, NSDictionary, ...). The HoudahFoundation has utility methods to
perform common operations on these types. There are also utility methods
that apply to core JDK types (String, Integer, ...). Anything you may want to
do to a collection type (transformation, persistence, ...) is very likely to be
already taken care of by one of these utility classes.

The HoudahFoundation framework implements a large collection of
formatter objects. Formatters extend from java.text.Format. They allow the
often bidirectional transformation between objects and their String
representation. These operations are referred to as “parsing” and
“formatting”. We leave it up to the JavaDoc to document each individual
formatter.

An object of special interest in the HoudahFoundation framework is the
KeyFormatter class. Despite what its name might suggest, this is not a
formatter. It rather is a couple made up of a key path (c.f.
NSKeyValueCodingAdditions) and a formatter. Often times when displaying
an object we want to say: “follow that key path and then format the value at
its end”. This operation can be described using a KeyFormatter. The actual
formatter wrapping this operation is called KeyFormatterFormatter. It allows
for the applying of a KeyFormatter or a series thereof to an object. A
KeyFormatterFormatter may be used anywhere a formatter is expected for
formatting a value.

Special attention should be given to the Delegate class. It allows for the
implementation in the Houdah frameworks of the delegate pattern. This
pattern is very common in the WebObjects frameworks. The Delegate
implementation expects the publisher of a delegate API to declare a Java
interface comprising all the methods the delegate could possibly be expected
to implement. This Java interface should be thought of as an “informal
protocol”. A delegate is not required to implement all of the interface’s
methods. Consequently the delegate need not (and should not) be declared as
implementing the interface. It needs only to match the method signature for
the calls it is interested in. The master object will call upon the delegate where
possible, and drop back to default behavior in all other cases.