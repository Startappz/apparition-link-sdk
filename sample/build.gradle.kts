import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.maps.secret)
}

android {
    namespace = "link.apparition.sdk.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.startappz.apparition.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    signingConfigs {
        val props = Properties().apply {
            FileInputStream("local.properties").use {
                it.bufferedReader().use { reader ->
                    load(reader)
                }
            }
        }
        create("release") {
            storeFile = file(props["KEYSTORE_PATH"] as String)
            storePassword = props["KEYSTORE_PASSWORD"] as String
            keyAlias = props["KEYSTORE_ALIAS"] as String
            keyPassword = props["KEYSTORE_PASSWORD"] as String
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":library"))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}
