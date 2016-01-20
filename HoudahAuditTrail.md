# HoudahAuditTrail #

The purpose of an audit trail is to keep track of who applied what modifications to a business object. HoudahAuditTrail framework hooks into the EOControl layer in order to provide this service.

The HoudahAuditTrail framework defines two subclasses of ValidatingRecord. The ChangeableRecord class is to be subclassed by enterprise objects for which we want to keep track of both date and user responsible for the last change. The TraceableRecord adds a versioning mechanism to this. At each change a copy of the old version of the record is kept in a separate entity. It is also possible to attach comments to historic versions. Comments should state the reason why a given version was overridden.

The ChangeableRecord class declares 4 abstract methods that subclasses must implement. The first two are accessor methods for a property named auditDate. The easiest way to fulfill this requirement is to model an auditDate attribute of type NSTimestamp. ChangeableRecord will use this field to store the date of last change.

The second set of methods is made of accessors for the auditUser property. This points to the user who performed the last change on the record. Typically your model should include an entity representing the system’s users. The class for said entity would then implement the AuditUser interface. Consequently the entity mapped to a subclass of ChangeableRecord needs a to-one relationship to said entity. In order to keep the Java compiler happy, you will need to implement the auditUser accessor methods as wrappers to the relationship’s accessor methods. Typically you would include this code in your EOGenerator template.

The ChangeableRecord is now able to record date and user of last change. This requires it to be used within a special editing context: AuditingEditingContext. When used with a plain editing context, the ChangeableRecord will forgo audit trail mechanisms.

Implementing a traceable record requires some additional work. For starters, one more attribute is needed: version. This number field will be used to record sequential version numbers.

In order to store historic versions, a new entity is needed. This entity is mostly identical to the master object. It adds a comment attribute. The presence of this attribute and the choice of name are both crucial: this string attribute masks a transient field declared by TraceableRecord. It also adds a to-one relationship to the master object. Conversely the master class will have a to-many relationship to its historic entity.

Both master and historic objects are implemented by classes extending from TraceableRecord. As such they need to implement a couple of abstract methods defined therein.