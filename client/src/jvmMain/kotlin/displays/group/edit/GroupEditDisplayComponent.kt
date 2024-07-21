package displays.group.edit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.stockmanager.core.entities.Group
import navigation.Component

class GroupEditDisplayComponent(
    private val componentContext: ComponentContext,
    private val group: Group,
    private val onBackClicked: () -> Unit,
    private val onDeleteClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    @Composable
    override fun render() {
        GroupEditDisplay(
            group = group,
            onBackClicked = onBackClicked,
            onDeleteClicked = onDeleteClicked
        )
    }

}