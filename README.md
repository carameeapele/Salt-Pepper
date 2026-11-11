# Salt&Pepper

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

### Test Accounts
ilincatestsp@yopmail.com / Parola1234
ceyaqueuvaddo-9785@yopmail.com / Parola1234

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

### Profile Setup Preview

After registration, a skippable three-step flow collects household size, dietary
and health preferences, allergies, and preferred cuisines. It can be reopened
from Profile. Choices are held only in memory for the current signed-in session
and reset on logout, account deletion, or a new sign-in. They are not yet sent
to the household API or used to filter recipes. Backend saving is intentionally
deferred until the accepted preference IDs are available.

### Profile Recipes

Profile loads the display name and membership date from `GET /me` and
authored/imported recipes from `GET /recipes`. Its Favorite recipes card opens a
dedicated Favorites screen, which loads `GET /recipes/favorites` and displays a
vertical list of recipe cards. Both screens have Loading, Success, and Error
states with retry, and refresh when resumed. Favorites failures do not block
Profile. Empty favorites show guidance and Browse recipes to Home; empty
personal recipes show disabled import/write actions until those features are
implemented.

### Account Deletion

The settings icon beside the profile name opens Settings, which contains logout
and permanent account deletion with email confirmation:
`POST /me/delete/code` sends the code, then `POST /me/delete` submits the
six-digit code and `delete_created_recipes`. Both requests use the access token.
Deleting created recipes is opt-in; published video recipes remain public.
Failed requests keep the session and show an error so the user can retry.
After successful deletion, local credentials are cleared and the app returns to
the welcome screen without an additional logout request.

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