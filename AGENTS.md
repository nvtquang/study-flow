# StudyFlow Agent Instructions

## Project

StudyFlow is an Android learning management app for students.

Title:
"Phat trien ung dung quan ly hoc tap thong minh StudyFlow tren nen tang Android, su dung Firebase va kien truc MVVM."

## Tech Stack

- Kotlin
- Android Native
- Jetpack Compose
- MVVM
- Repository Pattern
- Navigation Compose
- Firebase Auth
- Cloud Firestore
- Firebase Storage
- StateFlow
- Coroutines
- Hilt if needed

## Design

Use the provided StudyFlow mockups as the main visual reference.

Design style:

- Modern student productivity app
- Primary blue
- Lavender accent
- Off-white / light purple background
- Large rounded cards
- Subtle shadows
- Clean typography
- Spacious layout
- Friendly bottom navigation
- Consistent UI across the whole app

## Required Screens

Keep these screens represented in the app:

1. Dashboard
2. Login
3. Register
4. Home
5. Notification settings
6. Study schedule
7. Add schedule
8. Study group list
9. Group discussion
10. AI assistant
11. Focus mode
12. Document library
13. Profile
14. Goal management

## Architecture Rules

- Do not put business logic inside Composables.
- UI calls ViewModel only.
- ViewModel calls Repository only.
- Repository handles Firebase.
- Use state classes for loading, success, and error.
- Keep files small and organized.
- Prefer reusable components.
- Prefer existing Jetpack Compose patterns already used in this project.
- Keep edits scoped to the requested feature or phase.

## Theme And UI Rules

- Keep shared colors in `app/src/main/java/com/example/studyflow/ui/theme/Color.kt`.
- Keep typography in `app/src/main/java/com/example/studyflow/ui/theme/Type.kt`.
- Keep app-level Material theme rules in `app/src/main/java/com/example/studyflow/ui/theme/Theme.kt`.
- Avoid enabling dynamic color if it changes the intended StudyFlow brand palette.
- Reuse shared composables for cards, primary buttons, progress bars, chips, form fields, and navigation where practical.

## Build Rules

- After each phase, run Gradle build/check if available.
- Fix compile errors before reporting done.
- Do not introduce unnecessary dependencies.
- Do not refactor unrelated working code.
- Do not remove existing working features.

Recommended verification command:

```powershell
.\gradlew.bat assembleDebug
```

## Phase Workflow

Work one phase at a time.

For every phase:

1. Explain the implementation plan briefly.
2. Implement only the requested phase.
3. Ensure the app builds.
4. List changed files.
5. Explain how to test manually.
6. Stop and wait for the next phase.
