package displays.search.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.stockmanager.core.entities.Item

data class SearchState(
    val items: MutableState<List<Item>> = mutableStateOf(emptyList())
)