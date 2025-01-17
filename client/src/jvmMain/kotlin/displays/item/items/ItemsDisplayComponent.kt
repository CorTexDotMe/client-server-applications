package displays.item.items

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.stockmanager.core.entities.Group
import com.ukma.stockmanager.core.entities.Item
import navigation.Component

class ItemsDisplayComponent(
    private val componentContext: ComponentContext,
    private val group: Group,
    private val onItemClicked: (item: Item) -> Unit,
    private val onChangeGroup: () -> Unit,
    private val onBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {
//    private var state by mutableSetOf("")

    @Composable
    override fun render() {
        ItemsDisplay(
            group = group,
            onItemClicked = onItemClicked,
            onChangeGroup = onChangeGroup,
            onBackClicked = onBackClicked
        )
    }

}