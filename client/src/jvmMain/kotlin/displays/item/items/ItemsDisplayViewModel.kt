package displays.item.items

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.stockmanager.core.entities.Group
import displays.item.items.models.ItemsAction
import displays.item.items.models.ItemsEvent
import displays.item.items.models.ItemsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Facade

class ItemsDisplayViewModel(group: Group) :
    BaseSharedViewModel<ItemsState, ItemsAction, ItemsEvent>(initialState = ItemsState(mutableStateOf(group))) {

    var itemsTotalCost = 0.0;

//    init {
//        loadItems()
//    }

    override fun obtainEvent(viewEvent: ItemsEvent) {
        if (viewEvent == ItemsEvent.ItemsDisplay) loadItems()
    }

    suspend fun createItem(name: String, groupId: Int): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Facade.getInstance().createItem(name, "", 0, 0.0, "", groupId)
        }
    }

    private fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val group = viewState.group.value
            viewState.items.value = Facade.getInstance().getAllItemsByGroup(group.id)

            itemsTotalCost = viewState.items.value.sumOf { it.cost * it.amount }
        }
    }
}