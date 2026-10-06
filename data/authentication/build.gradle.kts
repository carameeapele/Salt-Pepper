import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    id("com.codingfeline.buildkonfig") version "0.17.1"
}

val localSecrets = Properties().apply {
    providers.fileContents(rootProject.layout.projectDirectory.file("secrets.properties"))
        .asText.orNull?.let { contents ->
            contents.reader().use { load(it) }
        }
}

fun requiredSupabaseConfig(name: String): String =
    providers.environmentVariable(name).orNull?.takeIf { it.isNotBlank() }
        ?: localSecrets.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: error("Set $name in root secret.properties or the build environment")

buildkonfig {
    packageName = "com.example.cuisinonsensemble.data.config"

    defaultConfigs {
        buildConfigField(STRING, "SUPABASE_URL", requiredSupabaseConfig("SUPABASE_URL"))
        buildConfigField(
            STRING,
            "SUPABASE_PUBLISHABLE_KEY",
            requiredSupabaseConfig("SUPABASE_PUBLISHABLE_KEY")
        )
    }
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DataAuthentication"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)

            // Supabase
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.auth)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.example.cuisinonsensemble.data.authentication"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}