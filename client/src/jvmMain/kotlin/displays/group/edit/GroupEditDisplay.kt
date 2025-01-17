package displays.group.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ukma.stockmanager.core.entities.Group
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Trash2
import displays.common.ChangeDialog
import displays.common.Field
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun GroupEditDisplay(
    group: Group,
    onBackClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val viewModel = remember { GroupEditViewModel(group) }

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
                        imageVector = FeatherIcons.Trash2,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            runBlocking {
                                viewModel.deleteGroup().await()

                                onDeleteClicked()
                            }
                        }
                    )
                }
            )
        }
    ) { padding ->
        GroupFields(
            modifier = Modifier.padding(padding),
            group = group,
            viewModel = viewModel
        )
    }
}


@Composable
fun GroupFields(
    modifier: Modifier = Modifier,
    group: Group,
    viewModel: GroupEditViewModel
) {
    val showDialog = remember { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }
    val dialogAction = remember { mutableStateOf({ _: String -> }) }
    val isError = remember { mutableStateOf(false) }
    val isErrorMessage = remember { mutableStateOf("") }

    if (showDialog.value)
        Dialog(
            onCloseRequest = {
                showDialog.value = false
                isError.value = false
            }
        ) {
            ChangeDialog(
                text = dialogText.value,
                isError = isError.value,
                isErrorMessage = isErrorMessage.value,
                onChangeClick = dialogAction.value
            )
        }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = group.name

                dialogAction.value = { newName ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        if (newName != group.name && viewModel.changeName(newName)) {
                            viewModel.viewStates().value.group.value!!.name = newName
                            showDialog.value = false
                            isError.value = false
                        } else {
                            isError.value = true
                            isErrorMessage.value = "Group name has to be unique"
                        }
                    }
                }

            }, text = group.name, style = MaterialTheme.typography.h4
        )

        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Description: ${group.description}"

                dialogAction.value = { newDescription ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.changeDescription(newDescription)
                        viewModel.viewStates().value.group.value!!.description = newDescription
                        showDialog.value = false
                    }
                }

            }, text = "Description: ${group.description}", style = MaterialTheme.typography.h5
        )
    }
}

