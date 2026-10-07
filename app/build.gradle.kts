import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val keystoreFileEnv: String? = System.getenv("KEYSTORE_FILE")?.takeIf { it.isNotBlank() }
val hasReleaseSigning = keystorePropertiesFile.exists() || keystoreFileEnv != null

android {
    namespace = "com.aether.application"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.aether.application"
        minSdk = 28
        targetSdk = 36

        versionCode = providers
            .gradleProperty("versionCode")
            .orElse("1")
            .get()
            .toInt()

        versionName = providers
            .gradleProperty("versionName")
            .orElse("1.0.0")
            .get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file(
                    keystoreProperties["storeFile"] as String
                )
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } else if (keystoreFileEnv != null) {
                storeFile = rootProject.file(keystoreFileEnv)

                storePassword = System.getenv("KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }
                    ?: error("KEYSTORE_PASSWORD environment variable is not set")

                keyAlias = System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }
                    ?: error("KEY_ALIAS environment variable is not set")

                keyPassword = System.getenv("KEY_PASSWORD")?.takeIf { it.isNotBlank() }
                    ?: error("KEY_PASSWORD environment variable is not set")
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://10.0.2.2:8080/\""
            )
        }

        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://api.aether.app/\""
            )

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // =========================
    // Android
    // =========================

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)


    // =========================
    // Compose
    // =========================

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)


    // =========================
    // Networking
    // =========================

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)


    // =========================
    // DataStore
    // =========================

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)


    // =========================
    // DI
    // =========================

    implementation(platform(libs.koin.bom))

    implementation(libs.insert.koin.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.navigation3)


    // =========================
    // Other
    // =========================

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.icons.lucide.android)
    implementation(libs.liquid)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.haze)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // =========================
    // Debug
    // =========================

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // =========================
    // Unit tests
    // =========================

    testImplementation(libs.junit)


    // =========================
    // Instrumentation tests
    // =========================

    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}