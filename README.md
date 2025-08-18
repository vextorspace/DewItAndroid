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
- **In-memory:** useful for testing and debugging.
- **SQLite:** persisted storage via `SqliteItemRepository` and `ItemDatabaseHelper`.
- **Future:** remote sync support to keep data in the cloud.
## Development
### We dont worry about automated tests for the UI but we do test the internal logic.
### We believe in descriptive names and good organization over large comments
  
## Project Structure

```
. 
├── README.md
├── settings.gradle.kts
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle/
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/dsronne/testdewit/
        │   │   ├── MainActivity.kt
        │   │   ├── viewports/…
        │   │   ├── ui/…
        │   │   ├── datamodel/…
        │   │   ├── storage/…
        │   │   └── storageports/…
        │   └── res/
        │       ├── layout/…
        │       ├── drawable/…
        │       ├── mipmap-*/…
        │       ├── values/…
        │       └── xml/…
        ├── test/java/com/dsronne/testdewit/
        └── androidTest/java/com/dsronne/testdewit/
```

## Key Entry Points

1. **MainActivity**
   - Inflates `activity_main.xml`, sets up edge-to-edge insets.
   - Constructs `SqliteItemRepository` → `ItemStore`, seeds initial hierarchy on first launch if the database is empty.
   - Retrieves top-level children and feeds them to `ItemPagerAdapter`.

2. **ItemPagerAdapter**
   - Presents one `ItemFragment` per root-child item via `ViewPager2`.

3. **ItemFragment**
   - Renders an item’s label and nested children list.
   - Handles add/edit/remove operations on items through `ItemStore`.

## Architecture

```
┌───────────┐     ┌──────────────┐     ┌───────────────┐
│  UI/View  │◀───▶│  ItemStore   │◀───▶│ ItemRepository│
└───────────┘     └──────────────┘     └───────────────┘
    ▲                                     ▲
    │                                     │
 Fragments                           InMemory / DB /
 Adapters                             remote impl
```

## Build & Test

- **Command-line:**
  ```bash
  ./gradlew assembleDebug   # build the debug APK
  ./gradlew :app:testDebugUnitTest   # run local unit tests for the debug build
  ./gradlew connectedAndroidTest  # run instrumentation tests on a connected device/emulator
  ```

## Todo
- workflow of inbox should allow moving to any of the top level items
- workflow of projects should allow copying to todo, waiting, moving to someday
- worklfow of someday should include moving to projects
- items need a todo checkbox when they are added to the todo list or waiting list
- when the todo is checked the item should cross off that list and go to a done list (in the top level)
- when the todo is checked a date at which it was completed should be stored
- items need a body markdown text which can include hyperlinks
- items need to be able to be added to the inbox at a date that is set
- changes should be synced to a remote database
- lists should be sharable between users