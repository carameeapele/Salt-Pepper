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

### Supabase Configuration

Before syncing Gradle or building either platform, copy the configuration template
to the project root:

```shell
cp secrets.properties.example secrets.properties
```

Set `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY` in `secret.properties` to your
Supabase project's URL and publishable key. The local file is ignored by Git;
only the template should be committed. Do not surround property values with quotes.

The authentication data module uses BuildKonfig to generate shared Kotlin
configuration for Android and iOS. Missing or blank values fail the build with
an actionable error. After changing configuration, sync Gradle and rebuild.

In CI, supply `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY` as environment
variables. Environment values take precedence over the local file. No
`secret.properties` file is needed in CI. Generated configuration remains under
the ignored build directory; configuration may also appear in Gradle caches,
so do not publish these caches indiscriminately.

Supabase URLs and publishable keys are public client configuration and remain
extractable from compiled apps. Secure data with Row Level Security and
authorization policies. Never embed service-role or secret keys in the app.
Removing values from source does not remove them from existing Git history;
rotate any privileged key that was committed.

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