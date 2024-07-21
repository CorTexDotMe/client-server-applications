package displays.group.groups

import com.adeo.kviewmodel.BaseSharedViewModel
import displays.group.groups.models.GroupAction
import displays.group.groups.models.GroupEvent
import displays.group.groups.models.GroupState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Facade

class GroupDisplayViewModel : BaseSharedViewModel<GroupState, GroupAction, GroupEvent>(
    initialState = GroupState()
) {

//    init {
//        loadGroups()
//    }

    override fun obtainEvent(viewEvent: GroupEvent) {
        if (viewEvent == GroupEvent.GroupDisplay) loadGroups()
    }

    suspend fun createGroup(name: String): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Facade.getInstance().createGroup(name, "")
        }
    }

    private fun loadGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.groups.value = Facade.getInstance().getAllGroups()
        }
    }

}