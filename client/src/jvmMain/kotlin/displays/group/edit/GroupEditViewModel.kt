package displays.group.edit

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.stockmanager.core.entities.Group
import displays.item.items.models.GroupEditAction
import displays.item.items.models.GroupEditState
import displays.item.items.models.ItemEditEvent
import kotlinx.coroutines.*
import utils.Facade

class GroupEditViewModel(group: Group) :
    BaseSharedViewModel<GroupEditState, GroupEditAction, ItemEditEvent>(initialState = GroupEditState(mutableStateOf(group))) {


//    init {
//        loadItems()
//    }

    override fun obtainEvent(viewEvent: ItemEditEvent) {
    }

    fun deleteGroup(): Deferred<Boolean> {
        return viewModelScope.async(Dispatchers.IO) {
            Facade.getInstance().deleteGroup(viewState.group.value!!.id)
            true
        }
    }

    suspend fun changeName(name: String): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Facade.getInstance().updateGroupName(viewState.group.value!!.id, name)
        }
    }

    fun changeDescription(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Facade.getInstance().updateGroupDescription(viewState.group.value!!.id, description)
        }
    }
}