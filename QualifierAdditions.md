Houdah Frameworks include a number of custom qualifiers:
> •	InSetQualifier: WHERE a IN (x, y, z)

> •	InSubQueryQualifier: WHERE a IN (SELECT x FROM A ...)

> •	ExistsInRelationshipQualifier: WHERE EXISTS (SELECT 1 FROM A WHERE ...)

> •	BestMatchQualifier: WHERE a IN (SELECT MAX(x) ...)

> •	PeriodQualifier: operates on month precision dates stored as year and month fields

> •	PiggybackQualifier: piggyback data onto qualifiers

> •	TrueQualifier: qualifier to match all objects

> •	FalseQualifier: qualifier to match no objects

In-memory search implementations are found in the [HoudahEOControl](HoudahEOControl.md) framework. Matching support classes for SQL generation are found in the [HoudahEOAccess](HoudahEOAccess.md) framework.