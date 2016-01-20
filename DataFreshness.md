# Introduction #

# Data freshness in Houdah Frameworks #

HoudahControlComponents implements the concepts of "trails", which was originally described in this [mailing list post](http://wodeveloper.com/omniLists/webobjects-dev/2002/June/msg00569.html). While the memory management advantages of the trails concept have mostly been obsoleted by improvements in WebObjects 5.2, the benefits of data freshness remain.

Trails are possible paths a user may take through an application. For a CRUD application, a trail usually starts with the submission of a search query. It then leads to a list page, from there to a detail page where the user possibly follows links to other entities.

The idea is to create a fresh editing context at the trailhead. The editing context is to configured not to accept values cached prior to its own creation. The editing context is passed down the trail and shared by all the pages and components on the trail. Thus, while following a trail. the user will never see data older than the trailhead.

In HoudahControlComponents, the trail's editing context is created in the search controller's searchAction() method. HoudahControlComponents stores the editing context in a userInfo dictionary attached to the current page. When navigating from one page to another, this userInfo dictionary is automagically passed along. By this mechanism, all pages further down the path get access to the trail's editing context.

Edit pages are on trails of their own: when the user reaches an Edit page a new peer editing context is created for the purpose of writing data. Upon saving changes, the Edit page sends the user off on a new trail where data will be fresh enough to witness the changes made on the Edit page.