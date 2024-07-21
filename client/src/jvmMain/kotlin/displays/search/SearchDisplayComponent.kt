package displays.search

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.stockmanager.core.entities.Item
import navigation.Component

class SearchDisplayComponent(
    componentContext: ComponentContext,
    private val onBackClicked: () -> Unit,
    private val onItemClicked: (Item) -> Unit
) : Component, ComponentContext by componentContext {

    @Composable
    override fun render() {
        SearchDisplay(
            onBackClicked = onBackClicked,
            onItemClicked = onItemClicked
        )
    }
}