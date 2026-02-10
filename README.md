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

## SDK Integration & Setup

Before running this demo, ensure you have set your **API Key** in the respective platform's initialization code.

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


