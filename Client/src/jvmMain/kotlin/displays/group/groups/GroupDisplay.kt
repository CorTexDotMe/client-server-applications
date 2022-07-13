import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.adeo.kviewmodel.compose.observeAsState
import com.ukma.nechyporchuk.core.entities.Group
import displays.group.groups.GroupDisplayViewModel
import displays.group.groups.models.GroupEvent
import displays.item.items.models.ItemsEvent


@Composable
fun GroupDisplay(
    onGroupClicked: (group: Group) -> Unit
) {
    val viewModel = remember { GroupDisplayViewModel() }
    val state = viewModel.viewStates().observeAsState()

    Groups(
        groups = state.value.groups.value,
        onGroupClicked = onGroupClicked
    )

    LaunchedEffect(key1 = true) {
        viewModel.obtainEvent(GroupEvent.GroupDisplay)
    }
}

@Composable
fun Groups(
    groups: List<Group>,
    onGroupClicked: (group: Group) -> Unit
) {
    LazyColumn {
        item {
            Text(
                text = "Storage",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.h2
            )
        }
        items(groups) { group ->
            Button(
                onClick = { onGroupClicked(group) },
                modifier = Modifier
            ) {
                Text(
                    text = group.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}