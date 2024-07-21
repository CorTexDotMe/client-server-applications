package displays.item.edit

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.stockmanager.core.entities.Item
import displays.item.items.models.ItemEditAction
import displays.item.items.models.ItemEditEvent
import displays.item.items.models.ItemEditState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Facade

class ItemEditViewModel(item: Item) :
    BaseSharedViewModel<ItemEditState, ItemEditAction, ItemEditEvent>(initialState = ItemEditState(mutableStateOf(item))) {


//    init {
//        loadItems()
//    }

    override fun obtainEvent(viewEvent: ItemEditEvent) {
        if (viewEvent is ItemEditEvent.ItemUpdate) updateItem()
    }

    fun deleteItem() {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().deleteItem(viewState.item.value!!.id)
        }
    }

    suspend fun changeName(name: String): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Facade.getInstance().updateItemName(viewState.item.value!!.id, name)
        }
    }

    fun changeDescription(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().updateItemDescription(viewState.item.value!!.id, description)
        }
    }

    fun addAmount(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().addAmount(viewState.item.value!!.id, amount)
        }
    }

    fun changeCost(cost: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().updateCost(viewState.item.value!!.id, cost)
        }
    }

    fun changeProducer(producer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().updateProducer(viewState.item.value!!.id, producer)
        }
    }

    private fun updateItem() {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.item.value = Facade.getInstance().getItem(viewState.item.value!!.id)
        }
    }
}