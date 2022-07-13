package displays.group.groups.models

import com.ukma.nechyporchuk.core.entities.Group

sealed class GroupAction {
    data class Navigate(val group: Group) : GroupAction()
}