package displays.search.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ukma.nechyporchuk.core.entities.Item

data class SearchState(
    val items: MutableState<List<Item>> = mutableStateOf(emptyList())
)