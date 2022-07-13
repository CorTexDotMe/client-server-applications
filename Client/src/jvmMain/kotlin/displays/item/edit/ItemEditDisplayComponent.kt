package displays.item.edit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import navigation.Component

class ItemEditDisplayComponent(
    private val componentContext: ComponentContext,
    private val item: Item,
    private val onBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {
//    private var state by mutableSetOf("")

    @Composable
    override fun render() {
        ItemEditDisplay(
            item = item,
            onBackClicked = onBackClicked
        )
    }

}