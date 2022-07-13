import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ui.WroteTheme
import navigation.NavHostComponent

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
//private fun createStoreRoot(componentContext: ComponentContext): StoreRoot{
//
//}
//
//@Composable
//fun RootContent() {
//    val router = rememberRouter<Screen>(
//        initialConfiguration = { NavHostComponent.ScreenConfig.GroupDisplay }
//    )
//
//    Children(
//        routerState = router.state
//    ) { screen ->
//        when (screenConfig) {
//            is NavHostComponent.ScreenConfig.GroupDisplay -> GroupDisplayComponent(
//                componentContext = componentContext,
//                onGroupClicked = { group ->
//                    router.push(NavHostComponent.ScreenConfig.ItemsDisplay(group))
//                }
//            )
//
//            is NavHostComponent.ScreenConfig.ItemsDisplay -> ItemsDisplayComponent(
//                componentContext = componentContext,
//                onItemClicked = { item ->
//
//                }
//            )
//            when (val configuration = screen.configuration) {
//                is Screen.List -> List(onItemClick = { router.push(Screen.Details(text = it)) })
//                is Screen.Details -> Details(text = configuration.text, onBack = router::pop)
//            }
//        }
//    }