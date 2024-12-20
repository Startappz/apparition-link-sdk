import co.touchlab.skie.configuration.DefaultArgumentInterop
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.skie)
}

val LIBRARY_VERSION: String by project

group = "link.apparition"
version = LIBRARY_VERSION
val artifactId = "sdk"
val sdkName = "ApparitionSDK"


kotlin {
    jvmToolchain(17)
    val xcf = XCFramework(sdkName)

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            binaryOption("bundleId", group.toString())
            baseName = sdkName
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.bundles.ktor.common)

            implementation(libs.kotlinx.serialization.json)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.startappz.apparition"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), artifactId, version.toString())

    pom {
        name = sdkName
        description = "Apparition SDK"
        inceptionYear = "2024"
        url = "https://github.com/Startappz/apparition-link-sdk"
        licenses {
            license {
                name = "MPL-2.0"
                url = "https://www.mozilla.org/en-US/MPL/2.0/"
                distribution = "https://www.mozilla.org/en-US/MPL/2.0/"
            }
        }
        developers {
            developer {
                id = "StartAppz"
                name = "StartAppz Engineering Team"
                url = "https://github.com/Startappz/apparition-link-sdk"
            }
        }
        scm {
            url = "https://github.com/Startappz/apparition-link-sdk"
            connection = "scm:git:git://github.com/Startappz/apparition-link-sdk.git"
            developerConnection = "scm:git:ssh://git@github.com:Startappz/apparition-link-sdk.git"
        }
    }
}

skie {
//    isEnabled.set(false)
    build {
        produceDistributableFramework()
    }

    analytics {
        disableUpload.set(true)
    }

    features {
        group {
            DefaultArgumentInterop.Enabled(true)
        }
    }
}
