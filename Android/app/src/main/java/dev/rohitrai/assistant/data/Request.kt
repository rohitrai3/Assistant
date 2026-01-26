package dev.rohitrai.assistant.data

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val messages: List<Message>,
    val stream: Boolean
)
