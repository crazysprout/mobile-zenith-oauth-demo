# Mobile Zenith OAuth Demo

This repository contains a demonstration of the Zenith OAuth SDK implementation for both Android and iOS platforms.

## Project Structure

- `android/`: Android sample application (Kotlin).
- `ios/`: iOS sample application (Swift).

## Prerequisites

- **Android**:
  - Android Studio
  - JDK 17
- **iOS**:
  - Xcode 15+
  - iOS 12.0+ Deployment Target

## SDK Integration

### Android

1. **Add the AAR File**:
   - Create a `libs` directory in your app module (e.g., `app/libs`).
   - Copy `zenith.aar` into this directory.

2. **Add Dependencies**:
   Open your module-level `build.gradle.kts` (or `build.gradle`) and add the following dependencies:

   ```kotlin
   dependencies {
       // Zenith SDK
       implementation(files("libs/zenith.aar"))

       // Required Transitive Dependencies
       implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
       implementation("com.squareup.okhttp3:okhttp:4.12.0")
       implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
       implementation("com.squareup.retrofit2:retrofit:2.9.0")
       implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
       implementation("androidx.browser:browser:1.8.0")
   }
   ```
   > **Note**: Versions shown are those used in this demo. Newer compatible versions may be used.

### iOS

1. **Add Framework**:
   - Drag and drop `Zenith.xcframework` into your Xcode project.
   - Ensure "Copy items if needed" is checked.

2. **Embed & Sign**:
   - Select your App Target > **General** > **Frameworks, Libraries, and Embedded Content**.
   - Verify `Zenith.xcframework` is listed.
   - Set **Embed** to **Embed & Sign**.

## Setup & Configuration

Before running the demo, you must configure your Zenith API Key in both projects.

### Android

1. Open the `android` directory in **Android Studio**.
2. Open `app/src/main/java/com/aztek/zenith/demo/MainActivity.kt`.
3. Locate the initialization code in `onCreate`:
   ```kotlin
   ZenithApp.setup(this, "YOUR_API_KEY") { error ->
       // ...
   }
   ```
4. Replace `"YOUR_API_KEY"` with your actual Zenith API Key.
5. **Redirect URI Configuration**:
   - Open `app/src/main/AndroidManifest.xml`.
   - Locate the `<data android:scheme="YOUR_REDIRECT_SCHEME" />` tag.
   - Replace `"YOUR_REDIRECT_SCHEME"` with your custom scheme (e.g., `com.example.app`).
6. Run the app on an Emulator or Device.

### iOS

1. Open `ios/zenith demo/zenith demo.xcodeproj` in **Xcode**.
2. Open `AppDelegate.swift`.
3. Locate the setup call in the `application(_:didFinishLaunchingWithOptions:)` method:
   ```swift
   ZenithApp.shared.setup(apiKey: "YOUR_API_KEY")
   ```
4. Replace `"YOUR_API_KEY"` with your actual Zenith API Key.
5. Select your target Simulator or Device and run the app.

## Features

The demo highlights the following Zenith SDK capabilities:

- **Authentication**:
  - **OAuth Sign In**: Standard user authentication.
  - **Guest Sign In**: Anonymous/Guest access.
- **Session Management**:
  - **Auto-Login**: Checks for existing sessions on app launch.
  - **Sign Out**: Clears local session data.
- **User Profile**:
  - Fetches and displays user details (ID, Username, Email, Verification status).

## Notes

- The iOS project uses a manually embedded `Zenith.xcframework`. Ensure the framework is correctly linked in "Frameworks, Libraries, and Embedded Content" if you move files around.
- The Android project uses standard Gradle dependency management.

## Troubleshooting

### Clearing OAuth Cache

If you need to switch users or force a fresh sign-in, you may need to clear the browser cache as the authentication session is often held by the system browser (Chrome/Safari).

- **Android**:
  1. Go to **Settings** > **Apps** > **Chrome** (or your default browser).
  2. Select **Storage & cache**.
  3. Tap **Clear storage** > **Clear all data**.

- **iOS**:
  1. Go to **Settings** > **Safari**.
  2. Tap **Clear History and Website Data**.

### Common Build Issues

- **Library not found (Android)**: Ensure the `libs` folder exists and `zenith.aar` is inside it. Check that `Settings.gradle.kts` (or project root build.gradle) doesn't conflict with module-level repositories.
- **Framework not found (iOS)**: Verify that `Zenith.xcframework` is under **Frameworks, Libraries, and Embedded Content** and set to **Embed & Sign**. If moving the project, you may need to re-drag the framework to fix file paths.


