# Introduction #

You may have various approaches to the Houdah WebObjects Frameworks. Our favorite one being that you would wholeheartedly adopt the frameworks for your next project. Yet we do understand that some may just want to borrow some juicy bits of code.

The below chapters should give some pointers on where to start. This varies depending on your planned use of the frameworks.


## Adopting the frameworks and their architecture ##

### Quick-start: shortest way to a running application ###

Please check the HoudahMovies project. It is the demo application which ultimately should demo most of the project's features. As of this writing it only demos the creation of a plain HoudahAgileComponents application. This is already a good place to start.

In the future HoudahMovies should demo the creation of custom controllers, views and cells. It should also demo lower level features such as rule based validation, query blessing, message localization,... Please feel free to experiment and contribute!

### Understanding the architecture ###

The Houdah frameworks are layered in the same way as are Apple's WebObjects frameworks. A lower level layer only depends on the matching Apple framework and on the layers below it. The order of dependancies is roughly as follows:

  * [HoudahFoundation](HoudahFoundation.md)
  * [HoudahEOControl](HoudahEOControl.md)
  * [HoudahRuleEngine](HoudahRuleEngine.md)
  * [HoudahEOAccess](HoudahEOAccess.md)
  * [HoudahMessages](HoudahMessages.md)
  * [HoudahEOValidation](HoudahEOValidation.md)
  * [HoudahQueryBlessing](HoudahQueryBlessing.md)
  * [HoudahAuditTrail](HoudahAuditTrail.md)
  * [HoudahAppServer](HoudahAppServer.md) (depends only on Foundation, Control and AppServer layers)
  * [HoudahViewComponents](HoudahViewComponents.md) (does not depend on EOControl, EOAccess, Validation, QueryBlessing, …)
  * [HoudahControlComponents](HoudahControlComponents.md) (does not depend on  EOAccess, Validation, QueryBlessing, …)
  * [HoudahAgileComponents](HoudahAgileComponents.md)

## Borrowing code ##

You may safely use code from any layer provided you link against all layers it depends on. You however do not need to use or even link to higher up layers.

While there is no compile time dependancy of HoudahEOControl on HoudahEOAccess, custom qualifiers can be used for database evaluation only if the support classes from HoudahEOAccess are present and registered at runtime.

Please keep the license terms in mind and give credit!