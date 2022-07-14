import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
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

@Composable
fun <T> rememberRootComponent(
    lifecycle: Lifecycle,
    factory: (ComponentContext) -> T
): T {
    return remember {
        val componentContext =
            DefaultComponentContext(
                lifecycle = lifecycle,
            )

        factory(componentContext)
    }
}