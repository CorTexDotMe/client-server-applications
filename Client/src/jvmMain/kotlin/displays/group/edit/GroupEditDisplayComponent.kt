package displays.group.edit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.ukma.nechyporchuk.core.entities.Group
import navigation.Component

class GroupEditDisplayComponent(
    private val componentContext: ComponentContext,
    private val group: Group,
    private val onBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    @Composable
    override fun render() {
        GroupEditDisplay(
            group = group,
            onBackClicked = onBackClicked
        )
    }

}