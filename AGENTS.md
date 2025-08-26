# Repository Guidelines

Concise guide for contributing to TestDewIt (Android, Kotlin, Gradle Kotlin DSL).

## Project Structure & Modules

- `app/`: Main Android application module.
- `app/src/main/java/com/dsronne/dewit/`: Kotlin source.
- `app/src/main/res/`: XML layouts, drawables, strings, themes.
- `app/src/test/`: JVM unit tests (Kotest + JUnit 5 + MockK).
- `app/src/androidTest/`: Instrumented tests (Espresso) for device/emulator.
- Top-level build with `build.gradle.kts`, module config in `app/build.gradle.kts`.

## Build, Test, and Development Commands

- Build debug APK: `./gradlew :app:assembleDebug`
- Install on device: `./gradlew :app:installDebug` (device/emulator required)
- Run unit tests: `./gradlew :app:testDebugUnitTest`
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest` (device/emulator)
- Lint checks: `./gradlew :app:lintDebug`
- Clean: `./gradlew clean`

Use Android Studio for iterative development; Gradle wrapper commands above should match IDE actions.

## Coding Style & Naming Conventions

- Language: Kotlin, JVM target 11, `kotlin.code.style=official`.
- Indentation: 4 spaces; limit lines to ~120 chars where practical.
- Classes/Objects: PascalCase (e.g., `ItemStore`, `CopyWorkflow`).
- Functions/vars: camelCase; constants UPPER_SNAKE_CASE.
- Files should match the primary type name (e.g., `ItemStore.kt`).
- Packages under `com.dsronne.dewit` mirroring feature areas (e.g., `datamodel`, `storage`).

## Testing Guidelines

- Frameworks: JUnit 5 (via `useJUnitPlatform()`), Kotest assertions, MockK.
- Unit tests live in `app/src/test/...`; instrumented UI tests in `app/src/androidTest/...`.
- Naming: `SomethingTest.kt`; test names should be descriptive (e.g., `shouldCopyItemToPath`).
- Example: run all unit tests with `./gradlew test` or per-variant as above.

## Commit & Pull Request Guidelines

- Commits: short, imperative summaries (e.g., "fix list rendering"), group related changes.
- Include context in body if needed; reference issues like `#123` when applicable.
- PRs: clear description, screenshots for UI changes, reproduction/verification steps, and any breaking change notes.
- Ensure build, unit tests, and lint pass before requesting review.

## Security & Configuration Tips

- Do not commit `local.properties` or secrets; SDK paths live locally.
- Min/target SDK: 33/36; verify behavior on a recent emulator before merging.
- Database code lives under `storage/`; keep migrations and data access localized and tested.

