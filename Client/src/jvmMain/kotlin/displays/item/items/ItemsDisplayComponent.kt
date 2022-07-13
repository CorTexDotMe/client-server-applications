package displays.item.items

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import navigation.Component

class ItemsDisplayComponent(
    private val componentContext: ComponentContext,
    private val group: Group,
    private val onItemClicked: (item: Item) -> Unit,
    private val onBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {
//    private var state by mutableSetOf("")

    @Composable
    override fun render() {
        ItemsDisplay(
            group = group,
            onItemClicked = onItemClicked,
            onBackClicked = onBackClicked
        )
    }

}