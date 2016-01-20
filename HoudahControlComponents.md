# HoudahControlComponents #

The HoudahControlComponents framework implements a controller layer to work with HoudahViewComponents. The framework’s controllers are designed to assist in creating a CRUD (Create – Read – Update – Delete) application. They implement behavior, page flow and error handling that is appropriate for this use.

The HoudahControlComponents framework is dependant on HoudahViewComponents as well as on lower level Houdah and WebObjects frameworks. It supposes the existence of a business layer managed by EOControl with persistence handled by EOAccess.


## Tasks ##

The controller layer is based upon the concept of tasks. Common tasks are: search, list, detail, edit and quickSearch. A task is applied to an entity and optionally to one or more instances of said entity. A page of the application can thus be defined by a task + entity name pair.

A typical workflow starts with a menu item linking to the search task of a certain entity. When the user submits the search request, she/he is taken to the page representing the list task on the same entity. A click on a line of the list takes her/him to the detail task page. From there, the user may navigate the object graph by following relationships. Such relationships either lead to list or detail tasks. The user may also elect to edit a given object.


## Pages ##

The framework defines 5 types of pages. One for each of the above mentioned task. These pages hold View components implemented by the HoudahViewComponents framework. To these Views, the pages are controllers. The actual responsibilities of a controller are however delegated to separate controller objects. There is a 1-1 relationship between a page and such a controller object. This technical artifact allows for the sharing and reuse of a single WOD amongst several controller classes.


## Controllers ##

The HoudahControlComponents implements controllers for the common tasks. These controllers are designed to handle the typical behavior of a CRUD (Create – Read – Update – Delete) application. They provide sensible behavior for workflow and error management.

The controllers in the HoudahControlComponents framework are declared abstract. In order to use them one needs to create concrete subclasses. Typically such subclasses need to implement a method creating Descriptors appropriate for the use with objects of a given entity. If you were to create an application using HoudahControlComponents you would implement concrete subclasses of all controllers for each entity in your model. Obviously you would much rather use the implementations provided by HoudahAgileComponents. The possibility of creating custom controllers by directly subclassing the controllers however remains. When only minor customization is needed (e.g. addition of an action) you would again rather subclass a HoudahAgileComponent controller. To create a wildly customized component you could subclass AbstractPageController and provide your own controllerComponentName().


## Customization ##

Subclasses of the standard controllers have a lot of flexibility as to the presentation (descriptors) and behavior (actions).

There may nonetheless be situations where this is not enough. For one the standard page for the controller's task may not suit the needs at hand. In this situation the controller may specify an alternate page component to use. The controller retains all the benefits of being a subclass of a common controller.
Further customization may be achieved by defining a new task. The controllers for that new task may extend one of the common controllers or not. They may rely on existing pages or not.