package navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.essenty.parcelable.Parcelable
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import displays.group.groups.GroupDisplayComponent
import displays.group.edit.GroupEditDisplayComponent
import displays.item.edit.ItemEditDisplayComponent
import displays.item.items.ItemsDisplayComponent

class NavHostComponent(
    componentContext: ComponentContext
) : Component, ComponentContext by componentContext {

    private val router = router<ScreenConfig, Component>(
        initialConfiguration = ScreenConfig.GroupDisplay,
        childFactory = ::createScreenComponent
    )


    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is ScreenConfig.GroupDisplay -> GroupDisplayComponent(
                componentContext = componentContext,
                onGroupClicked = { group ->
                    router.push(ScreenConfig.ItemsDisplay(group))
                }
            )

            is ScreenConfig.ItemsDisplay -> ItemsDisplayComponent(
                componentContext = componentContext,
                group = screenConfig.group,
                onItemClicked = { item ->
                    router.push(ScreenConfig.ItemEdit(item))
                },
                onChangeGroup = {
                    router.push(ScreenConfig.GroupEdit(screenConfig.group))
                },
                onBackClicked = router::pop
            )

            is ScreenConfig.ItemEdit -> ItemEditDisplayComponent(
                componentContext = componentContext,
                item = screenConfig.item,
                onBackClicked = router::pop
            )

            is ScreenConfig.GroupEdit -> GroupEditDisplayComponent(
                componentContext = componentContext,
                group = screenConfig.group,
                onBackClicked = router::pop
            )
        }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        Children(routerState = router.state) {
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object GroupDisplay : ScreenConfig()
        data class GroupEdit(val group: Group) : ScreenConfig()
        data class ItemsDisplay(val group: Group) : ScreenConfig()
        data class ItemEdit(val item: Item) : ScreenConfig()
    }
}