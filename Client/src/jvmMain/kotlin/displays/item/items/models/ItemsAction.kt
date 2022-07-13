package displays.item.items.models

import com.ukma.nechyporchuk.core.entities.Group

sealed class ItemsAction {
    data class Navigate(val group: Group) : ItemsAction()
}