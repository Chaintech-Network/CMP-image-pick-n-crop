import androidx.compose.ui.window.ComposeUIViewController
import network.chaintech.imagepickncrop.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
