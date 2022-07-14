package displays.item.items.models

sealed class ItemEditEvent {
    object ItemUpdate: ItemEditEvent()
}