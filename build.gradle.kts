plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply  false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.kmmbridge) apply false
}

subprojects {
    val GROUP_NAME: String by project
    val LIBRARY_VERSION: String by project

    group = GROUP_NAME
    version = LIBRARY_VERSION
}