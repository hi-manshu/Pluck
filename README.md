### Pluck - The image-picker library for Compose

![Pluck](art/pluck.jpg)

This is an image-picker for your jetpack compose project. You can select from Gallery/Camera.

_Made with ❤️ for Android Developers by Himanshu_

[![Maven Central](https://img.shields.io/maven-central/v/com.himanshoe/pluck)](https://search.maven.org/artifact/com.himanshoe/pluck)
[![Github Followers](https://img.shields.io/github/followers/hi-manshu?label=Follow&style=social)](https://github.com/hi-manshu)
[![Twitter Follow](https://img.shields.io/twitter/follow/hi_man_shoe?label=Follow&style=social)](https://twitter.com/hi_man_shoe)


## Implementation

#### Step: 01

Add the specific permission in `AndroidManifest.xml` file

```xml

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<uses-permission android:name="android.permission.CAMERA" />

```

#### Step: 02

In `build.gradle` of app module, include the following dependency

```gradle
dependencies {
  implementation("com.himanshoe:pluck:1.0.0-RC1")
}
```

## Usage

#### 01

Now, to start using Pluck, use the composable `Pluck` like,

```kotlin
Pluck(onPhotoSelected = {
    // List of PluckImage when selecting from Gallery/Camera. When checking with Camera
    // It returns only one item in list
})
```

#### 02

Now, if you want `Pluck` to handle the Permission for you as well. Use it like,

```kotlin
Permission(
    permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ),
    goToAppSettings = {
        // Go to App Settings
    }
) {
    Pluck(onPhotoSelected = {
        // List of PluckImage when selecting from Gallery/Camera. When checking with Camera
        // It returns only one item in list
    })
}
```

### Drop a ⭐ to keep me motivated to keep working on Open-Source. Updates coming Soon!

