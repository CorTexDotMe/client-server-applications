package displays.group.groups.models

import com.ukma.stockmanager.core.entities.Group

sealed class GroupAction {
    data class Navigate(val group: Group) : GroupAction()
}