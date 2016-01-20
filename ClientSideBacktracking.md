The main problems related to backtracking are:
> •	Wrong items being selected from WORepetitions after the user backtracked

> •	A component being asked to perform an action that is inappropriate in the current state. E.g. cancel an edit after committing it.


The first problem is in fact caused by a more general problem: a component always exits with only one state, i.e. the latest one. The user may however backtrack to a cached page that matches a previous state: showing components that should be hidden, displaying an earlier batch of a display group,... .
The second problem is closely related, but differs by the fact that the former state of the component is permanently lost in that the moving to the new state has non reversible effects like deleting from a database.

The [HoudahAppServer](HoudahAppServer.md) frameworks contains BackTrackComponent and related classes which provide a generic solution for detecting and ultimately handling backtracking problems.