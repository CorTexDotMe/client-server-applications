package displays.group.groups

import GroupDisplay
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.nechyporchuk.core.entities.Group
import navigation.Component

class GroupDisplayComponent(
    private val componentContext: ComponentContext,
    private val onGroupClicked: (group: Group) -> Unit,
    private val onSearchClicked: () -> Unit
) : Component, ComponentContext by componentContext {
//    private var state by mutableSetOf("")

    @Composable
    override fun render() {
        GroupDisplay(
            onGroupClicked = onGroupClicked,
            onSearchClicked = onSearchClicked
        )
    }

}