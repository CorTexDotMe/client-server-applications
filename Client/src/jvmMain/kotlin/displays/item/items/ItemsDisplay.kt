package displays.item.items

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adeo.kviewmodel.compose.observeAsState
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Settings
import displays.common.CreateButton
import displays.common.CreateDialog
import displays.common.ItemButton
import displays.item.items.models.ItemsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ItemsDisplay(
    group: Group,
    onItemClicked: (item: Item) -> Unit,
    onChangeGroup: () -> Unit,
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
                },
                actions = {
                    Icon(
                        imageVector = FeatherIcons.Settings,
                        contentDescription = "",
                        modifier = Modifier.clickable { onChangeGroup() }
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
            viewModel = viewModel,
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
    viewModel: ItemsDisplayViewModel,
    onItemClicked: (item: Item) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }

    if (showDialog.value)
        Dialog(
            onCloseRequest = {
                showDialog.value = false
                isError.value = false
            }
        ) {
            CreateDialog(
                text = "Create a new item",
                isError = isError.value,
                isErrorMessage = "Item name has to be unique",
                onCreateClick = { name ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        if (viewModel.createItem(name, group.id)) {
                            showDialog.value = false
                            isError.value = false
                            viewModel.obtainEvent(ItemsEvent.ItemsDisplay)
                        } else {
                            isError.value = true
                        }
                    }
                }
            )
        }

    Box(modifier = modifier.fillMaxSize()) {
        val state = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(end = 12.dp),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 6.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = state
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
                ItemButton(
                    item = item,
                    onItemClicked = onItemClicked
                )
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )

        CreateButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onCreateButton = { showDialog.value = true }
        )
    }
}