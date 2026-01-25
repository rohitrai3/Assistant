package dev.rohitrai.assistant.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val role: String
)
