package dev.rohitrai.assistant.data

import kotlinx.serialization.Serializable

@Serializable
data class Ping(
    val status: String
)
