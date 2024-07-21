package displays.search

import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.stockmanager.core.entities.Item
import displays.search.models.SearchAction
import displays.search.models.SearchEvent
import displays.search.models.SearchState
import kotlinx.coroutines.launch
import utils.Facade

class SearchDisplayViewModel :
    BaseSharedViewModel<SearchState, SearchAction, SearchEvent>(initialState = SearchState()) {


    override fun obtainEvent(viewEvent: SearchEvent) {
        if (viewEvent == SearchEvent.ItemsSearchInit) initialiseItemsForSearch()
    }

    fun searchItems(query: String): List<Item> {
        return viewState.items.value.filter { item -> item.name.lowercase().matches("${query.lowercase()}.*".toRegex()) }
    }

    private fun initialiseItemsForSearch() {
        viewModelScope.launch {
            viewState.items.value = Facade.getInstance().getAllItems()
        }
    }
}