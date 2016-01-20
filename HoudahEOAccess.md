# HoudahEOAccess #

The HoudahEOAccess framework acts as an extension to Apple’s EOAccess framework. As such it depends on the HoudahFoundation and HoudahEOControl frameworks.

The AccessUtilities class is another sibling to the EOUtilities class. It provides utility methods that rely on both the EOControl and EOAccess layers.

Similarly the ModelUtilities class provides methods to manipulate objects from the EOModel domain.

Of interest to the performance conscious is the SpecialityFetches utility class. It provides ways to perform object counts and like operations within the database. This is often preferable to fetching large batches of objects in memory for the sole purpose of counting them or summing up one of their attributes.

The FetchSpecBatchIterator targets the same crowd. It allows for computations on very large sets of objects to be performed in a memory stable way. Rather than fetching thousands of objects into memory, the FetchSpecBatchIterator gets only the primary keys. From there it gets objects in batches, leaving processed objects up to garbage collection. An increase in the number of objects to process (e.g. production ramp up) then merely translates into an increase of processing time. Memory consumption will be determined by the chosen batch size.

The bulk of the HoudahEOAccess framework is made of the supporting classes for the qualifiers defined by the HoudahEOControl framework. Indeed a qualifier needs an EOAccess layer support class in order to be evaluated in the database. These support classes are in charge of generating SQL code. Again these classes could serve as starting points for writing your own qualifiers. For casual use of the qualifiers you should not worry about the support classes beyond making sure you have HoudahEOAccess included in your runtime path.