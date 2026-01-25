package dev.rohitrai.assistant.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://192.168.1.2"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AssistantApiService {
    @GET("health")
    suspend fun ping(): String
}

object AssistantApi {
    val retrofitService : AssistantApiService by lazy {
        retrofit.create(AssistantApiService::class.java)
    }
}
