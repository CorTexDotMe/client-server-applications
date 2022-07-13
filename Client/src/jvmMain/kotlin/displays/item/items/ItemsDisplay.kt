package displays.item.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adeo.kviewmodel.compose.observeAsState
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import displays.item.items.models.ItemsState
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import displays.item.items.models.ItemsEvent

@Composable
fun ItemsDisplay(
    group: Group,
    onItemClicked: (item: Item) -> Unit,
    onBackClicked: () -> Unit
) {
    val viewModel = remember { ItemsDisplayViewModel(group) }
    val state = viewModel.viewStates().observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Icon(
                        imageVector = FeatherIcons.ArrowLeft,
                        contentDescription = "",
                        modifier = Modifier.clickable { onBackClicked() }
                    )
                }
            )
        }
    ) { padding ->
        Items(
            modifier = Modifier.padding(padding),
            group = state.value.group.value,
            items = state.value.items.value,
            itemsTotalCost = viewModel.itemsTotalCost,
            onItemClicked = onItemClicked
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.obtainEvent(ItemsEvent.ItemsDisplay)
    }
}


@Composable
fun Items(
    modifier: Modifier = Modifier,
    group: Group,
    items: List<Item>,
    itemsTotalCost: Double,
    onItemClicked: (item: Item) -> Unit
) {
//    TODO ScrollBar
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 6.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = group.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.h4
            )
            if (itemsTotalCost != 0.0)
                Text(
                    text = "Total cost: $itemsTotalCost",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.subtitle1
                )
        }
        items(items) { item ->
            Button(
                onClick = { onItemClicked(item) },
                modifier = Modifier
            ) {
                Column {
                    Text(
                        text = item.name,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.h6
                    )
                    Divider(
                        color = MaterialTheme.colors.onSecondary, thickness = 3.dp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    Text(
                        text = item.additionalInfo(),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}