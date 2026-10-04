import androidx.compose.ui.window.ComposeUIViewController
import io.github.imanih20.datetimewheelpicker.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
  configure = { enforceStrictPlistSanityCheck = false },
  content = { App() }
)
