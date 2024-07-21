package displays.item.items.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.stockmanager.core.entities.Group

data class GroupEditState(
    val group: MutableState<Group?> = mutableStateOf(null)
)