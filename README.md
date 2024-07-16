# CMPImagePickNCrop

A CMPImagePickNCrop is image selection & cropping library for Compose Multiplatform for Android and iOS.

![CMP-image-pick-n-crop-org](https://github.com/user-attachments/assets/17eddc44-ae1f-4692-9989-c27ebe724f85)


## Installation

Add the dependency to your `build.gradle.kts` file:

```
commonMain.dependencies {
    implementation("network.chaintech:cmp-image-pick-n-crop:1.0.1")
}
```

## Add Permissions in Android and iOS

- Android : Include this at root level in your `AndroidManifest.xml`:

```
<uses-feature android:name="android.hardware.camera"/>
<uses-feature android:name="android.hardware.camera.autofocus"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.FLASHLIGHT"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
```

- iOS : Add below key to the `Info.plist` in your xcode project:

```
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
    imagePickerDialogHandler: (Boolean) -> Unit,
    selectedImageCallback: (ImageBitmap) -> Unit
)
```

- `imageCropper`: Manages the image cropping logic (default is rememberImageCropper()).
- `openImagePicker`: Controls whether the image picker dialog is open.
- `cropEnable`: Enables or disables the image cropping feature (default is true).
- `imagePickerDialogHandler`: Handles the opening and closing of the image picker dialog.
- `selectedImageCallback`: Called when an image is selected and cropped, providing the cropped image.

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
            imagePickerDialogHandler = {
                openImagePicker = it
            },
            selectedImageCallback = {
                selectedImage = it
            })

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

## Screenshot
![1_9Ea4YOYyvObPhPldxwKngQ](https://github.com/ChainTechNetwork/CMP-image-pick-n-crop/assets/143475887/4c681c09-1ae3-4075-b225-554f5931f49e)


- For Demo [Checkout This Class](https://github.com/ChainTechNetwork/CMP-image-pick-n-crop/blob/main/composeApp/src/commonMain/kotlin/network/chaintech/cmpimagepickncropdemo/App.kt)

- [Medium Article](https://medium.com/mobile-innovation-network/cmpimagepickncrop-compose-multiplatform-6963559d6f73) for detailed explaination.

- Connect us on [LinkedIn](https://www.linkedin.com/showcase/mobile-innovation-network)
