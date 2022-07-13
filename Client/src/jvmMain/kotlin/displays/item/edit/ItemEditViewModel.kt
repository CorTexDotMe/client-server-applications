package displays.item.edit

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.nechyporchuk.core.entities.Item
import displays.item.items.models.*
import kotlinx.coroutines.*
import utils.Facade

class ItemEditViewModel(item: Item) :
    BaseSharedViewModel<ItemEditState, ItemEditAction, ItemEditEvent>(initialState = ItemEditState(mutableStateOf(item))) {


//    init {
//        loadItems()
//    }

    override fun obtainEvent(viewEvent: ItemEditEvent) {
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

    fun changeAmount(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().updateAmount(viewState.item.value!!.id, amount)
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
}