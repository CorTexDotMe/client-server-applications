import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import navigation.NavHostComponent
import ui.WroteTheme

fun main() {
    val lifecycle = LifecycleRegistry()


    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = ""
        ) {
            WroteTheme {
                rememberRootComponent(lifecycle = lifecycle, factory = ::NavHostComponent)
                    .render()
            }
        }
    }
}