package displays.search.models

sealed class SearchEvent {
    object ItemsSearchInit : SearchEvent()
}