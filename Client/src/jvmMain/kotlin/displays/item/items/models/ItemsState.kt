package displays.item.items.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item

data class ItemsState(
    val group: MutableState<Group> = mutableStateOf(Group()),
    val items: MutableState<List<Item>> = mutableStateOf(emptyList())
)