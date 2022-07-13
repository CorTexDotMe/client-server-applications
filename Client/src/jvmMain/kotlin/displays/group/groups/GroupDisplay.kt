import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adeo.kviewmodel.compose.observeAsState
import com.ukma.nechyporchuk.core.entities.Group
import displays.common.CreateButton
import displays.common.CreateDialog
import displays.group.groups.GroupDisplayViewModel
import displays.group.groups.models.GroupEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun GroupDisplay(
    onGroupClicked: (group: Group) -> Unit
) {
    val viewModel = remember { GroupDisplayViewModel() }
    val state = viewModel.viewStates().observeAsState()

    Groups(
        groups = state.value.groups.value,
        viewModel = viewModel,
        onGroupClicked = onGroupClicked
    )

    LaunchedEffect(key1 = true) {
        viewModel.obtainEvent(GroupEvent.GroupDisplay)
    }
}

@Composable
fun Groups(
    groups: List<Group>,
    viewModel: GroupDisplayViewModel,
    onGroupClicked: (group: Group) -> Unit
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
                text = "Create a new group",
                isError = isError.value,
                isErrorMessage = "Group name has to be unique",
                onCreateClick = { name ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        if (viewModel.createGroup(name)) {
                            showDialog.value = false
                            isError.value = false
                            viewModel.obtainEvent(GroupEvent.GroupDisplay)
                        } else {
                            isError.value = true
                        }
                    }
                }
            )
        }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    text = "Storage",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.h2
                )
            }
            items(groups) { group ->
                Button(
                    onClick = { onGroupClicked(group) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primaryVariant
                    ),
                    modifier = Modifier
                ) {
                    Column {
                        Text(
                            text = group.name,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.onSecondary,
                            style = MaterialTheme.typography.h5
                        )

                        Text(
                            text = group.description,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.onSecondary,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
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