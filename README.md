[![Maven Central](https://img.shields.io/maven-central/v/network.chaintech/cmp-image-pick-n-crop.svg)](https://central.sonatype.com/artifact/network.chaintech/cmp-image-pick-n-crop)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.0.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.7.0-blue)](https://github.com/JetBrains/compose-multiplatform)
[![License](https://img.shields.io/github/license/Chaintech-Network/CMPCharts)](http://www.apache.org/licenses/LICENSE-2.0)

![badge-android](http://img.shields.io/badge/platform-android-3DDC84.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-FF375F.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-FF9500.svg?style=flat)
![badge-web](https://img.shields.io/badge/platform-web-33ceff.svg?style=flat)

# CMPImagePickNCrop

A CMPImagePickNCrop is image selection & cropping library for Compose Multiplatform for Android and iOS.

![CMP-image-pick-n-crop-org](https://github.com/user-attachments/assets/17eddc44-ae1f-4692-9989-c27ebe724f85)


## Installation

Add the dependency to your `build.gradle.kts` file:

```kotlin
commonMain.dependencies {
    implementation("network.chaintech:cmp-image-pick-n-crop:1.1.2")
}
```

## Add Permissions in Android and iOS

- Android : Include this at root level in your `AndroidManifest.xml`:

```xml
<uses-feature android:name="android.hardware.camera"/>
<uses-feature android:name="android.hardware.camera.autofocus"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.FLASHLIGHT"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
```

- iOS : Add below key to the `Info.plist` in your xcode project:

```xml
<key>NSCameraUsageDescription</key><string>$(PRODUCT_NAME) camera description.</string>
<key>NSPhotoLibraryUsageDescription</key><string>$(PRODUCT_NAME)photos description.</string>
```

## Usage

```kotlin
@Composable
fun CMPImagePickNCropDialog(
    imageCropper: ImageCropper = rememberImageCropper(),
    openImagePicker: Boolean,
    cropEnable: Boolean = true,
    showCameraOption: Boolean = true,
    showGalleryOption: Boolean = true,
    autoZoom: Boolean = true,
    enableRotationOption: Boolean = true,
    enabledFlipOption: Boolean = true,
    shapes: List<ImageCropShape>? = DefaultCropShapes,
    aspects: List<ImageAspectRatio>? = DefaultImageCropperAspectRatios,
    imagePickerDialogStyle: ImagePickerDialogStyle = ImagePickerDialogStyle(),
    defaultAspectRatio: ImageAspectRatio? = null,
    imagePickerDialogHandler: (Boolean) -> Unit,
    selectedImageCallback: (ImageBitmap) -> Unit,
    selectedImageFileCallback: (SharedImage) -> Unit
)
```

- `imageCropper`: Manages the image cropping logic (default is rememberImageCropper()).
- `openImagePicker`: Controls whether the image picker dialog is open.
- `cropEnable`: Enables or disables the image cropping feature (default is true).
- `showCameraOption`: Displays the option to pick an image from the camera.
- `showGalleryOption`: Displays the option to pick an image from the gallery.
- `autoZoom`: Automatically zooms to fit the cropped region within view bounds.
- `enableRotationOption`: Show or hide rotation options.
- `enabledFlipOption`: Show or hide flip options.
- `shapes`: Specifies the list of cropping shapes (default is DefaultCropShapes).
- `aspects`: Defines the aspect ratios available for cropping (default is DefaultImageCropperAspectRatios).
- `imagePickerDialogStyle`: Styling options for the image picker dialog (default is ImagePickerDialogStyle()).
- `defaultAspectRatio`: Aspect ratio to be preselected when cropping starts (default is null).
- `imagePickerDialogHandler`: Handles the visibility of the image picker dialog.
- `selectedImageCallback`: Callback invoked with the cropped image as an ImageBitmap.
- `selectedImageFileCallback`: Callback invoked with the shared image object used for image compress.

## Example

```kotlin
@Composable
internal fun App() = AppTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        val imageCropper = rememberImageCropper()
        var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
        var openImagePicker by remember { mutableStateOf(value = false) }

        CMPImagePickNCropDialog(
            imageCropper = imageCropper,
            openImagePicker = openImagePicker,
            defaultAspectRatio = ImageAspectRatio(16, 9),
            imagePickerDialogStyle = ImagePickerDialogStyle(
                title = "Choose from option",
                txtCamera = "From Camera",
                txtGallery = "From Gallery",
                txtCameraColor = Color.DarkGray,
                txtGalleryColor = Color.DarkGray,
                cameraIconTint = Color.DarkGray,
                galleryIconTint = Color.DarkGray,
                backgroundColor = Color.White
            ),
            autoZoom = true,
            imagePickerDialogHandler = {
                openImagePicker = it
            },
            selectedImageCallback = {
                selectedImage = it
            },
            selectedImageFileCallback = { sharedImage ->
              scope.launch {
                val compressedFile = compressImage(
                  sharedImage = sharedImage,
                  targetFileSize = 200 * 1024 // In KB
                )
                println("Compressed File Path : $compressedFile")
              }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedImage?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
            }
            if (selectedImage == null)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("No image selected !", color = Color.Black)
                }

            Button(
                onClick = {
                    openImagePicker = true
                },
            ) { Text("Choose Image") }
        }
    }
}
```

- For Crop

```kotlin
val selectedBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
val imageCropper = rememberImageCropper()
CMPImageCropDialog(imageCropper = imageCropper)

Button(
    modifier = Modifier
        .weight(2F)
        .padding(start = 12.dp, end = 12.dp),
    onClick = {
        coroutineScope.launch {
            selectedBitmap.value?.let { bitmap ->
                when (val result = imageCropper.cropImage(bmp = bitmap)) {
                    ImageCropResult.Cancelled -> {
                        // Handle cancellation if needed
                    }
                    is ImageCropError -> {
                        // Handle error if needed
                    }
                    is ImageCropResult.Success -> {
                        selectedBitmap.value = result.bitmap
                    }
                }
            }
        }
    }
) {
    Text(text = "CropImage")
}
```
<br>

- ByteArray <br><br>
  If you want a bytearray then you can convert the ImageBitmap received in the callback using the ImageBitmap.toByteArray()

```kotlin
// function to convert ImageBitmap to ByteArray
fun ImageBitmap.toByteArray(
    format: ImageFileFormat = ImageFileFormat.PNG,
    quality: Float = 1.0f
): ByteArray?

// Usage
val imageBitmap = .. // your selected imageBitmap
val byteArray = imageBitmap.toByteArray() // converted byteArray
```

<br>

- Image Compress <br><br>
  If you want to compress image then you can use the selectedImageFileCallback

```kotlin
selectedImageFileCallback = { sharedImage ->
  scope.launch {
    val compressedFile = compressImage(
      sharedImage = sharedImage,
      targetFileSize = 200 * 1024 // In KB
    )
    println("Compressed File Path : $compressedFile")
  }
}
```

## Screenshot - Android and iOS
![1_9Ea4YOYyvObPhPldxwKngQ](https://github.com/ChainTechNetwork/CMP-image-pick-n-crop/assets/143475887/4c681c09-1ae3-4075-b225-554f5931f49e)

## Screenshot - Desktop
![CMP-image-pick-n-crop-org](https://github.com/user-attachments/assets/441c948c-f0bc-47d6-937e-3b1a49429ea5)


- For Demo [Checkout This Class](https://github.com/ChainTechNetwork/CMP-image-pick-n-crop/blob/main/composeApp/src/commonMain/kotlin/network/chaintech/cmpimagepickncropdemo/App.kt)

- [Medium Article](https://medium.com/mobile-innovation-network/cmpimagepickncrop-compose-multiplatform-6963559d6f73) for detailed explaination.
  <br><br>

## 🌐 Stay Connected with Us
Stay connected and keep up with our latest innovations! 💼 Let's innovate together!<br><br>
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/showcase/mobile-innovation-network)
[![Medium](https://img.shields.io/badge/Medium-12100E?style=for-the-badge&logo=medium&logoColor=white)](https://medium.com/mobile-innovation-network)   
<br>

## 📄 License
```
Copyright 2023 Mobile Innovation Network

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
