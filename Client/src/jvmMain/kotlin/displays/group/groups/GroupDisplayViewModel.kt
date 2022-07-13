package displays.group.groups

import com.adeo.kviewmodel.BaseSharedViewModel
import com.ukma.nechyporchuk.core.entities.Group
import displays.group.groups.models.GroupAction
import displays.group.groups.models.GroupEvent
import displays.group.groups.models.GroupState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private fun loadGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.groups.value = Facade.getInstance().getAllGroups()
        }
    }

}