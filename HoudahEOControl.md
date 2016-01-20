# HoudahEOControl #

The HoudahEOControl framework builds upon Apple’s EOControl
framework as well as upon the HoudahFoundation framework. It
intentionally does not rely on the EOAccess framework. Indeed the EOAccess
layer is only one of several possible persistence layers EOControl may work
with. Having a business layer dependant on EOAccess would introduce
assumptions as to which persistence layer is used. Most notably such a
business layer could no longer be used in the context of JavaClient
applications where persistence is handled by the EODistribution framework.
Following this logic, the ControlUtilities class appears as the most important
class of the framework. It is in a way a sibling of Apple’s EOUtilities class. It
actually re-implements all of EOUtilities’ methods that could be written
without a dependency on the EOAccess layer. To this it adds a set of its own
utility methods. You will find yourself using this class quite often: you should
prefer it to EOUtilities in your business layer so as to prevent a dependency
on the access layer.

The bulk of the HoudahEOControl code is made of [custom qualifiers](QualifierAdditions.md).
Qualifiers describe criteria used to restrict the selection of objects. The
WebObject frameworks provide a set of frequently used qualifiers:
EOKeyValueQualifier, EOKeyComparisonQualifier, EOAndQualifier,
EOOrQualifier and EONotQualifier. The HoudahEOControl framework adds
quite a few custom qualifiers to this set. These are best documented by their
respective JavaDoc. These qualifiers are meant as actual useful additions to
the frameworks. They however also serve as examples and starting point for
you to implement your own.

The ChangeNotificationCenter and related classes allow for caching derived
values that are computed at display time. The owner of the cache may register
for notifications of changes to values the cache derives from.