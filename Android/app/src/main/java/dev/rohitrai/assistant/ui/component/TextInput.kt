package dev.rohitrai.assistant.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rohitrai.assistant.ui.theme.Gray
import dev.rohitrai.assistant.ui.theme.LightRed
import dev.rohitrai.assistant.ui.theme.Red
import dev.rohitrai.assistant.ui.theme.White

@Composable
fun TextInput(
    errorMessage: String,
    isError: MutableState<Boolean>,
    label: String,
    modifier: Modifier = Modifier,
    state: TextFieldState
) {
    BasicTextField(
        cursorBrush = SolidColor(White),
        decorator = { innerTextField ->
            if(state.text.isEmpty()) BasicText(text = label, style = TextStyle(
                color = if (isError.value) LightRed else Gray,
                fontSize = 16.sp
            ))
            innerTextField()
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
            .border(
                color = if (isError.value) Red else Gray,
                shape = RoundedCornerShape(16.dp),
                width = 1.dp
            )
            .padding(16.dp),
        state = state,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = White
        )
    )
    if (isError.value) BasicText(
        style = TextStyle(color = Red),
        text = errorMessage
    )
}
