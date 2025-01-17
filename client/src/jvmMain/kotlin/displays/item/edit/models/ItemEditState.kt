package displays.item.items.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.stockmanager.core.entities.Item

data class ItemEditState(
    val item: MutableState<Item?> = mutableStateOf(null)
)