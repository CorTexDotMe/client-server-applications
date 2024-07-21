package displays.item.edit

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adeo.kviewmodel.compose.observeAsState
import com.ukma.stockmanager.core.entities.Item
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Trash2
import displays.common.ChangeDialog
import displays.common.Field
import displays.item.items.models.ItemEditEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ItemEditDisplay(
    item: Item,
    onBackClicked: () -> Unit
) {
    val viewModel = remember { ItemEditViewModel(item) }
    val state = viewModel.viewStates().observeAsState()
    val viewAction = viewModel.viewActions().observeAsState()

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
                            viewModel.viewModelScope.launch {
                                viewModel.deleteItem()

                                onBackClicked()
                            }
                        }
                    )
                }
            )
        }
    ) { padding ->
        ItemFields(
            modifier = Modifier.padding(padding),
            item = state.value.item.value,
            viewModel = viewModel
        )
    }

    LaunchedEffect(viewAction) {
        viewModel.obtainEvent(ItemEditEvent.ItemUpdate)
    }
}


@Composable
fun ItemFields(
    modifier: Modifier = Modifier,
    item: Item?,
    viewModel: ItemEditViewModel
) {
    if (item == null)
        return

    val showDialog = remember { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }
    val dialogAction = remember { mutableStateOf({ _: String -> }) }
    val isError = remember { mutableStateOf(false) }
    val isErrorMessage = remember { mutableStateOf("") }
    val label = remember { mutableStateOf(null as String?) }

    if (showDialog.value)
        Dialog(onCloseRequest = {
            showDialog.value = false
            isError.value = false
        }) {
            ChangeDialog(
                text = dialogText.value,
                isError = isError.value,
                isErrorMessage = isErrorMessage.value,
                label = label.value,
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
                dialogText.value = item.name
                label.value = "Name"

                dialogAction.value = { newName ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        if (newName != item.name && viewModel.changeName(newName)) {
                            viewModel.viewStates().value.item.value!!.name = newName
//                            viewModel.obtainEvent(ItemEditEvent.ItemUpdate)
                            showDialog.value = false
                            isError.value = false
                        } else {
                            isError.value = true
                            isErrorMessage.value = "Item name has to be unique"
                        }
                    }
                }

            }, text = item.name, style = MaterialTheme.typography.h4
        )

        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Description: ${item.description}"
                label.value = "Description"

                dialogAction.value = { newDescription ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.changeDescription(newDescription)
                        viewModel.viewStates().value.item.value!!.description = newDescription
//                        viewModel.obtainEvent(ItemEditEvent.ItemUpdate)
                        showDialog.value = false
                    }
                }

            }, text = "Description: ${item.description}", style = MaterialTheme.typography.h5
        )
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Amount: ${item.amount}\n\nEnter amount to add"
                label.value = "Add amount"

                dialogAction.value = { newAmount ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        val amount = newAmount.toIntOrNull()
                        if (amount != null) {

                            val result = viewModel.viewStates().value.item.value!!.amount + amount
                            if (result >= 0) {
                                viewModel.addAmount(amount)
                                viewModel.viewStates().value.item.value!!.amount = result
//                            viewModel.obtainEvent(ItemEditEvent.ItemUpdate)

                                showDialog.value = false
                                isError.value = false
                            } else {
                                isError.value = true
                                isErrorMessage.value = "You don't have that many items"
                            }
                        } else {
                            isError.value = true
                            isErrorMessage.value = "Item amount has to be integer number"
                        }
                    }
                }

            }, text = "Amount: ${item.amount}", style = MaterialTheme.typography.h5
        )
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Cost: ${item.cost}"
                label.value = "Cost"

                dialogAction.value = { newCost ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        val cost = newCost.toDoubleOrNull()
                        if (cost != null && cost >= 0.0) {
                            viewModel.changeCost(cost)
                            viewModel.viewStates().value.item.value!!.cost = cost
//                            viewModel.obtainEvent(ItemEditEvent.ItemUpdate)
                            showDialog.value = false
                            isError.value = false
                        } else {
                            isError.value = true
                            isErrorMessage.value = "Item cost has to be positive number"
                        }
                    }
                }

            }, text = "Cost: ${item.cost}", style = MaterialTheme.typography.h5
        )
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Producer: ${item.producer}"
                label.value = "Producer"

                dialogAction.value = { newProducer ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.changeProducer(newProducer)
                        viewModel.viewStates().value.item.value!!.producer = newProducer
//                        viewModel.obtainEvent(ItemEditEvent.ItemUpdate)
                        showDialog.value = false
                    }
                }

            }, text = "Producer: ${item.producer}", style = MaterialTheme.typography.h5
        )
    }
}
