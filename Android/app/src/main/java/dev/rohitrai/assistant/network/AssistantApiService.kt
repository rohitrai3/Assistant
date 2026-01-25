package dev.rohitrai.assistant.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.rohitrai.assistant.data.Ping
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://192.168.1.2"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface AssistantApiService {
    @GET("health")
    suspend fun ping(): Ping
}

object AssistantApi {
    val retrofitService : AssistantApiService by lazy {
        retrofit.create(AssistantApiService::class.java)
    }
}
