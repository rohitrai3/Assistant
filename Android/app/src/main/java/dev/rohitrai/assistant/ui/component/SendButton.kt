package dev.rohitrai.assistant.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rohitrai.assistant.R
import dev.rohitrai.assistant.ui.theme.Black
import dev.rohitrai.assistant.ui.theme.Gray
import dev.rohitrai.assistant.ui.theme.LightGray

@Composable
fun SendButton(onClick: () -> Unit) {
    IconButton(
        colors = IconButtonColors(
            containerColor = Gray,
            contentColor = Black,
            disabledContainerColor = LightGray,
            disabledContentColor = Black
        ),
        modifier = Modifier.size(48.dp),
        onClick = onClick
    ) {
        Icon(
            contentDescription = stringResource(R.string.send_button_description),
            painter = painterResource(R.drawable.send)
        )
    }
}