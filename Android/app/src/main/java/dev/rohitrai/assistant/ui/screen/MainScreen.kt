package dev.rohitrai.assistant.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    MainView("Assistant")
}

@Composable
private fun MainView(name: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text("Hello $name!")
        }
    }
}
