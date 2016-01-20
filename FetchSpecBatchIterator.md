Iterator to lazily instantiate batches of objects matching a given fetch specification. Allows for memory stable operations on very large data sets.

FetchSpecBatchIterator is part of the [HoudahEOControl](HoudahEOControl.md) framework.

This code is dependant on the [InSetQualifier](QualifierAdditions.md). Ultimately this implies a runtime dependancy on the [HoudahEOAccess](HoudahEOAccess.md) framework. For obvious reasons there is no compile-time dependancy of the EOControl layer on the EOAccess layer.