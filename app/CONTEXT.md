# DewIt App
This is an android app that primarily manages lists
## Model
### There is a root item that is not displayed
### Items have children that are also items
### Items have unique ids
### Items can be children of multiple items
## View
### Item views are generated on the fly
### Top level is a swiping left and right kind of view where each root child item has its own screen
### The Item's children are then listed under the item and are themselves views so they have the same tools and can have children
## Storage
### We are currently working with an in memory storage system but will change to a data-base one that can be synced remotely
## Development
### We dont worry about automated tests for the UI but we do test the internal logic.
### We believe in descriptive names and good organization over large comments

## Todo
- extract well-named methods from bindChildView
- adopt View Binding (or Kotlin Android extensions) instead of manual findViewById
- use RecyclerView with a tree-capable adapter (4b)
- maybe instead of Recycle View extract custom TreeView/NestedListView component (4c)
- changes should also be stored to a local database
- changes should be synced to a remote database
- items need a body text
- items need a todo checkbox when they are added to the todo list
- when the todo is checked the item should cross off that list and go to a done list (in the top level)
- when the todo is checked a date at which it was completed should be stored
- items need to be able to be added to the inbox at a date that is set
