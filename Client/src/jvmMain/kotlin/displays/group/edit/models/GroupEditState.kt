package displays.item.items.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.nechyporchuk.core.entities.Group

data class GroupEditState(
    val group: MutableState<Group?> = mutableStateOf(null)
)