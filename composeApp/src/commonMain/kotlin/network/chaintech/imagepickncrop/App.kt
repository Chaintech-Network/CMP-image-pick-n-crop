package network.chaintech.imagepickncrop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.ImageAspectRatio
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import network.chaintech.cmpimagepickncrop.utils.ImagePickerDialogStyle
import network.chaintech.imagepickncrop.theme.AppTheme

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

