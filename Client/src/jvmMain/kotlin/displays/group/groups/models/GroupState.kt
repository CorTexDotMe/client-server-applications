package displays.group.groups.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.nechyporchuk.core.entities.Group

data class GroupState(
    val groups: MutableState<List<Group>> = mutableStateOf(emptyList())
)