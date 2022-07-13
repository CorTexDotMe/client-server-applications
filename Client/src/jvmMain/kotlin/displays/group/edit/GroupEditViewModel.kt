package displays.group.edit

import androidx.compose.runtime.mutableStateOf
import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import displays.item.items.models.GroupEditAction
import displays.item.items.models.ItemEditEvent
import displays.item.items.models.GroupEditState
import kotlinx.coroutines.*
import utils.Facade

class GroupEditViewModel(group: Group) :
    BaseSharedViewModel<GroupEditState, GroupEditAction, ItemEditEvent>(initialState = GroupEditState(mutableStateOf(group))) {


//    init {
//        loadItems()
//    }

    override fun obtainEvent(viewEvent: ItemEditEvent) {
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