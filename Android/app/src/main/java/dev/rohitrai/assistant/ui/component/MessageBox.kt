package dev.rohitrai.assistant.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rohitrai.assistant.data.Message
import dev.rohitrai.assistant.ui.theme.DarkGray
import dev.rohitrai.assistant.ui.theme.LightGray

@Composable
fun MessageBox(messages: List<Message>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().padding(0.dp, 8.dp),
        state = rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE),
        verticalArrangement = Arrangement.Bottom,
    ) {
        itemsIndexed(messages) { _, message ->
            if (message.role == "user") {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .background(LightGray, RoundedCornerShape(16.dp))
                            .padding(16.dp, 4.dp),
                        text = message.content.toString()
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .background(DarkGray, RoundedCornerShape(16.dp))
                            .padding(16.dp, 4.dp),
                        text = message.content.toString()
                    )
                }
            }
        }
    }
}
