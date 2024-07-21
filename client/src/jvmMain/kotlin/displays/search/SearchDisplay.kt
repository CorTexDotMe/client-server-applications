package displays.search

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
import androidx.compose.ui.unit.dp
import com.ukma.stockmanager.core.entities.Item
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import displays.common.ItemButton
import displays.search.models.SearchEvent

@Composable
fun SearchDisplay(
    onItemClicked: (Item) -> Unit,
    onBackClicked: () -> Unit
) {
    val viewModel = remember { SearchDisplayViewModel() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Icon(
                        imageVector = FeatherIcons.ArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .clickable { onBackClicked() }
                    )
                }
            )
        }
    ) { padding ->
        SearchItems(
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            onItemClicked = onItemClicked
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.obtainEvent(SearchEvent.ItemsSearchInit)
    }
}

@Composable
fun SearchItems(
    modifier: Modifier = Modifier,
    viewModel: SearchDisplayViewModel,
    onItemClicked: (Item) -> Unit
) {
    val textFieldText = remember { mutableStateOf("") }

    Column(modifier) {
//        Row {
        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = textFieldText.value,
            onValueChange = { newText -> textFieldText.value = newText },
            label = { Text("Search") }
        )
//            Icon(
//                imageVector = FeatherIcons.Search,
//                contentDescription = "Search"
//            )
//        }
        SearchItems(
            items = viewModel.searchItems(textFieldText.value),
            onItemClicked = onItemClicked
        )
    }
}

@Composable
fun SearchItems(
    modifier: Modifier = Modifier,
    items: List<Item>,
    onItemClicked: (Item) -> Unit
) {
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
            items(items) { item ->
                ItemButton(
                    item = item,
                    onItemClicked = onItemClicked
                )
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}