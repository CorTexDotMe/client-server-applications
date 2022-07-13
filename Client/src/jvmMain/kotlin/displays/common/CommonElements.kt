package displays.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    label: String? = null,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = textValue,
        isError = isError,
        label = {
            label?.let {
                Text(text = it, style = MaterialTheme.typography.caption)
            }
        },
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

@Composable
fun CreateDialog(
    text: String,
    isError: Boolean,
    isErrorMessage: String,
    onCreateClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textFieldText = remember { mutableStateOf("") }

        Field(
            text = text,
            style = MaterialTheme.typography.h5
        )

        ValidationTextField(
            isError = isError,
            isErrorMessage = isErrorMessage,
            textValue = textFieldText.value,
            label = "Name",
            onValueChanged = { newText -> textFieldText.value = newText }
        )

        Button(
            onClick = { onCreateClick(textFieldText.value) }
        ) {
            Text(text = "Create", style = MaterialTheme.typography.button)
        }
    }
}

@Composable
fun CreateButton(
    modifier: Modifier = Modifier,
    onCreateButton: () -> Unit,
) {
    Button(
        onClick = onCreateButton,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Create",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 26.sp,
            style = MaterialTheme.typography.button
        )
    }
}
