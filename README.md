This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Email-Code Authentication

The mobile app uses the deployed API at `https://getsaltpepper.fr/api` and does not
connect to Supabase directly. Both sign-in and account creation use the same flow:

1. `POST /auth/email/start` with `{"email":"user@example.com"}` sends a code.
2. `POST /auth/email/verify` with the email and received code verifies it.

The API URL is public configuration in the shared client. No Supabase keys or
local secrets file are needed by the mobile app. The verification endpoint's
response returns an access token, its lifetime, the user, and a rotating
`sp_refresh` cookie.

### Persistent Mobile Sessions

The refresh cookie is saved in Android Keystore-encrypted, non-backed-up storage
or iOS Keychain (`AfterFirstUnlockThisDeviceOnly`). Access tokens stay in memory.
On launch, splash calls `POST /auth/refresh` to restore the user and securely save
the rotated cookie before entering Main. Refreshes are serialized, with no
automatic replay of a consumed refresh token.

Main checks the session every 30 seconds while foregrounded. Checks update the
last-use timestamp and refresh access tokens near expiry. After 14 days without
foreground use, or when the cookie expires or the server rejects the session,
sign-in is required. Network failures retain the credential; startup offers a
retry. Logout calls the backend and clears local credentials even if that call
fails; failed remote revocation cannot be guaranteed until the server expires it.

The backend's `refresh_token_days` must be at least 14 to allow the requested
two-week window. Its cookie expiration is always respected, even if earlier.
The local inactivity cutoff is not a replacement for server-side enforcement.
No refreshes run in the background. Existing installs require one successful
sign-in to populate the new secure store.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…