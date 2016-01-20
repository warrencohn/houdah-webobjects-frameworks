The [HoudahEOControl](HoudahEOControl.md) frameworks provides EOGenericRecord subclasses which provide the following functionalities:

> •	Audit trail: timestamp modified objects and link them to the object representing the responsible user

> •	Object history: archive former versions of objects

The idea here is to set the timestamp and create the archived data object as soon as an object needing such services is modified. This approach has several advantages over implementations that do these operations shortly before saving the editing context or within the database. Most notably, only objects requiring these service suffer from the overhead caused these operations. Furthermore the archived data is immediately available for the user to interact with.