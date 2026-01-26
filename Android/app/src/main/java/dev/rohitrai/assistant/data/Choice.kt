package dev.rohitrai.assistant.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Choice(
    val delta: Message,
    @SerialName(value = "finish_reason")
    val finishReason: String?
)
