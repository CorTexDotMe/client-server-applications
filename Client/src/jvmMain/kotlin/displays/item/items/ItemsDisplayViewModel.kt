package displays.item.items

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.nechyporchuk.core.entities.Group
import displays.item.items.models.ItemsAction
import displays.item.items.models.ItemsEvent
import displays.item.items.models.ItemsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val group = viewState.group.value
            viewState.items.value = Facade.getInstance().getAllItemsByGroup(group.id)

            itemsTotalCost = viewState.items.value.sumOf { it.cost * it.amount }
        }
    }
}