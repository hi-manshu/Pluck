### Pluck - The image-picker library for Compose

This is an image-picker for your jetpack compose project. You can select from Gallery/Camera.

_Made with ❤️ for Android Developers by Himanshu_

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
  implementation("com.himanshoe:pluck:1.0.0-alpha01")
}
```

## Usage

#### 01

Now, to start using Pluck, use the composable `Pluck` like,

```kotlin
Pluck(onPhotoSelected = {
    // List of PluckImage when selecting from Gallery             
}, onPhotoClicked = {
    // Bitmap when using Camera
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
        // List of PluckImage when selecting from Gallery             
    }, onPhotoClicked = {
        // Bitmap when using Camera
    })
}
```

### Drop a ⭐ to keep me motivated to keep working on Open-Source. Updates coming Soon!

