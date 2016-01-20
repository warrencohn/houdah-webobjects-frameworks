# HoudahViewComponents #

The Houdah frameworks are designed with strict Model-View-Controller principles in mind. In the presentation layers, WebObjects already takes care of separating view from controller by using several distinct files to declare a single component. The .wod bundle specializes in the view aspects. The corresponding Java class contains mostly controller code. The Houdah frameworks take this a step further by recognizing that some strictly view code can pollute the component’s Java class. We add another layer of separation by distinguishing between components which act as views and components which act as controllers.

The HoudahViewComponents framework implements a completely generic and reusable view layer. It provides high-level view components for thin client three tier web applications. The framework is based upon the presentation layer of the WebObjects application server. As such it has dependencies only on the Foundation, EOControl and AppServer level frameworks. Its dependency on EOControl has been kept minimal so that it may be used with control layers that don’t rely on EOF for persistence.

The framework defines the following notions:

  * Views: High-level presentation components
  * Cells: Lightweight presentation components. Building bricks for views
  * Values: proxy objects linking cells to the model layer. In charge of lazy conversion between business value and presentation value.
  * Descriptors: immutable objects describing views or cells and the way they present values. May be shared or cached.
  * Controllers: a controller is defined as the owner of a view. Provides behavior.

It is important to note that while HoudahViewComponents defines the notion of controller, it does not implement controllers. The framework does not implement or suppose any particular behavior.


## Views ##

Views are reusable WebObjects components. They generate pure HTML. All matters of design are left to an external CSS stylesheet.
  * HVCList: This list component displays a single object as a list of label + value pairs.
  * HVCDisplayGroupList: A wrapper around HVCList to display an object embedded in a display group.
  * HVCSimpleList: A simplified list component. It has a single value column
  * HVCTable: A table component. Displays a series of objects in a table: one row per object.
  * HVCDisplayGroupTable: A wrapper around HVCTable to display objects from a batched display group.
  * HVCFieldset: A field set is a partial form. It is made of groups of form elements: text fields, checkbox groups, and action buttons...
  * HVCFieldTable: This component is a cross between the table and field set components. It allows for editing several objects at a time.
  * HVCDisplayGroupFieldTable: A wrapper around HVCFieldTable to edit objects from a batched display group.


## Cells ##

Cells are lightweight building blocks used to create Views. They are implemented as stateless WebObjects components.

You should hardly ever have to manipulate Cells. For most uses it is sufficient instantiate Views from a Controller component and configure them using Descriptors.


## Values ##

Views and Cells that allow for user input use Value objects. They are in charge of the lazy bi-directional conversion between the displayed value and the corresponding business value.

Controllers and delegates to the Value may participate in the initializing of a Value as well as in the conversion operations.


## Descriptors ##

Descriptors are immutable objects used to describe Views and Cells. They are usually instantiated by Controllers and then passed on to a View. Further Views and Cells may be instantiated as demanded by the Descriptors.

E.g.: A table is described as a list of columns. Each column has a header and a prototype row element. A row element may be described by a keyPath and formatter couple. These allow it to compute a value.
Being immutable, Descriptors may be put in cache and shared. They may also be persisted to a file or generated on the fly using a rule system. In order to stay immutable, Descriptors may not have setter methods and may not hold onto mutable objects.


## Controllers ##

To a View, a Controller is the first parent that is not a View. The Controller is expected to implement actions, initialize values etc. Provide behavior.


## Describers and Identifiers ##

The Describer and Identifier of an entity are formatters that create respectively parse a canonical description of an enterprise object. E.g. a User object may be described by the user’s full name. It may be identified by the user’s login name.