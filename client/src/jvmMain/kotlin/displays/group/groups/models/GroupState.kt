package displays.group.groups.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.stockmanager.core.entities.Group

data class GroupState(
    val groups: MutableState<List<Group>> = mutableStateOf(emptyList())
)