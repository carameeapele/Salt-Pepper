plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:shared"))
            implementation(libs.koin.core)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}