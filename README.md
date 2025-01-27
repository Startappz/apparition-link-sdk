## Apparition Mobile SDK

The Apparition Mobile SDK (Android / iOS) for deeplinking

### Features
* Expand; expand URL by retrieving the the deeplink content.

## 🚧 Under Heavy Development 🚧

## Quick Start

### Setup
Android

```yaml
apparition-sdk = { module = "link.apparition:sdk-android", version.ref = "apparitionSdk" }
```

```kotlin
dependencies {
    implementation(libs.apparition.sdk)
}
```

Common (KMP)
```yaml
apparition-sdk = { module = "link.apparition:sdk", version.ref = "apparitionSdk" }
```

```kotlin
sourceSets {
    commonMain.dependencies {
        implementation(libs.apparition.sdk)
    }
}
```

