package displays.item.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft

@Composable
fun ItemEditDisplay(
    item: Item,
    onBackClicked: () -> Unit
) {
//    val viewModel = remember { ItemsDisplayViewModel(ItemsState(group = mutableStateOf(group))) }
//    val state = viewModel.viewStates().observeAsState()

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
//        Items(
//            modifier = Modifier.padding(padding),
//            group = state.value.group.value,
//            items = state.value.items.value,
//            onItemClicked = onItemClicked
//        )
        Text(item.toString(), modifier = Modifier.padding(padding))
    }
}


@Composable
fun Items(
    modifier: Modifier = Modifier,
    group: Group,
    items: List<Item>,
    onItemClicked: (item: Item) -> Unit
) {
    LazyColumn(modifier) {
        item {
            Text(
                text = group.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.h2
            )
        }
        items(items) { item ->
            Button(
                onClick = { onItemClicked(item) },
                modifier = Modifier
            ) {
                Text(
                    text = item.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}