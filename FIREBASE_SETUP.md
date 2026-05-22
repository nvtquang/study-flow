# Firebase Setup

This project is prepared for Firebase, but it does not include a real Firebase configuration file yet.

## Required Firebase Android app

Create or use a Firebase project, then register an Android app with this package name:

```text
com.example.studyflow
```

## Required Firebase products

Enable these products in the Firebase console:

- Authentication
- Cloud Firestore
- Firebase Storage

For Authentication, enable the sign-in providers you need. Email/password is the default provider expected by the current repository code.

## Add google-services.json

Download the Android config file from Firebase Console and place it here:

```text
app/google-services.json
```

Keep the file name exactly `google-services.json`.

The project includes `app/google-services.json.example` only as a location/template reference. Do not use the example file as a real Firebase config.

## Build behavior

The Google Services Gradle plugin is applied only when `app/google-services.json` exists. This keeps local builds working before a real Firebase project is connected.

After adding the real file, sync Gradle and build:

```powershell
.\gradlew.bat assembleDebug
```
