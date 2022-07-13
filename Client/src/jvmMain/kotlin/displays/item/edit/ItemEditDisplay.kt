package displays.item.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ukma.nechyporchuk.core.entities.Item
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ItemEditDisplay(
    item: Item,
    onBackClicked: () -> Unit
) {
    val viewModel = remember { ItemEditViewModel(item) }

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
        ItemFields(
            modifier = Modifier.padding(padding),
            item = item,
            viewModel = viewModel
        )
    }
}


@Composable
fun ItemFields(
    modifier: Modifier = Modifier,
    item: Item,
    viewModel: ItemEditViewModel
) {
    val showDialog = remember { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }
    val dialogAction = remember { mutableStateOf({ _: String -> }) }
    val isError = remember { mutableStateOf(false) }
    val isErrorMessage = remember { mutableStateOf("") }

    if (showDialog.value)
        Dialog(onCloseRequest = {
            showDialog.value = false
            isError.value = false
        }) {
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
                dialogText.value = item.name

                dialogAction.value = { newName ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        if (newName != item.name && viewModel.changeName(newName)) {
                            viewModel.viewStates().value.item.value!!.name = newName
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
                dialogText.value = item.description

                dialogAction.value = { newDescription ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.changeDescription(newDescription)
                        viewModel.viewStates().value.item.value!!.description = newDescription
                        showDialog.value = false
                    }
                }

            }, text = item.description, style = MaterialTheme.typography.h5
        )
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Amount: ${item.amount}"

                dialogAction.value = { newAmount ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        val amount = newAmount.toIntOrNull()
                        if (amount != null && amount >= 0) {
                            viewModel.changeAmount(amount)
                            viewModel.viewStates().value.item.value!!.amount = amount
                            showDialog.value = false
                            isError.value = false
                        } else {
                            isError.value = true
                            isErrorMessage.value = "Item amount has to be positive integer number"
                        }
                    }
                }

            }, text = "Amount: ${item.amount}", style = MaterialTheme.typography.h5
        )
        Field(
            modifier = Modifier.clickable {
                showDialog.value = true
                dialogText.value = "Cost: ${item.cost}"

                dialogAction.value = { newCost ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        val cost = newCost.toDoubleOrNull()
                        if (cost != null && cost >= 0.0) {
                            viewModel.changeCost(cost)
                            viewModel.viewStates().value.item.value!!.cost = cost
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

                dialogAction.value = { newProducer ->
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.changeProducer(newProducer)
                        viewModel.viewStates().value.item.value!!.producer = newProducer
                        showDialog.value = false
                    }
                }

            }, text = "Producer: ${item.producer}", style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun Field(
    modifier: Modifier = Modifier,
    text: String,
    style: androidx.compose.ui.text.TextStyle
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
        style = style
    )
}

@Composable
fun ChangeDialog(
    text: String,
    isError: Boolean,
    isErrorMessage: String,
    onChangeClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textFieldText = remember { mutableStateOf("") }

        Field(text = text, style = MaterialTheme.typography.h6)

        ValidationTextField(
            isError = isError,
            isErrorMessage = isErrorMessage,
            textValue = textFieldText.value,
            onValueChanged = { newText -> textFieldText.value = newText }
        )

        Button(
            onClick = { onChangeClick(textFieldText.value) }
        ) {
            Text(text = "Change", style = MaterialTheme.typography.button)
        }
    }
}

@Composable
fun ValidationTextField(
    isError: Boolean,
    isErrorMessage: String,
    textValue: String,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = textValue,
        isError = isError,
        onValueChange = onValueChanged
    )
    if (isError) {
        Text(
            text = isErrorMessage,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

