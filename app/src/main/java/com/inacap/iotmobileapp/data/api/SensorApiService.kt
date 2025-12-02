package com.inacap.iotmobileapp.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- Modelos para SENSORES ---

// Modelo de respuesta de TU Backend Node.js
data class BackendSensorResponse(
    val temperature: Double,
    val humidity: Double,
    val timestamp: String
)

// Modelo simplificado para la UI
data class SensorData(
    val temperature: Double,
    val humidity: Int,
    val city: String,
    val timestamp: Long
)

// --- Modelos para AUTENTICACIÓN ---

// Lo que enviamos al Backend para registrar
data class RegisterRequest(
    val name: String,
    @SerializedName("last_name") val lastName: String, // Mapeamos last_name del JSON a lastName de Kotlin
    val email: String,
    val password: String
)

// Lo que responde el Backend
data class RegisterResponse(
    val message: String?, // "Usuario registrado correctamente"
    val error: String?,   // En caso de error
    val success: Boolean? // Algunos backends lo usan
)

// --- Modelos para OpenWeatherMap (Legacy) ---
data class WeatherResponse(
    val main: MainData,
    val name: String,
    val dt: Long
)

data class MainData(
    val temp: Double,
    val humidity: Int,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)

// --- INTERFAZ DE LA API ---

interface SensorApiService {

    // 1. Obtener datos del sensor (Backend Node.js)
    @GET("iot/data")
    suspend fun getBackendSensorData(): BackendSensorResponse

    // 2. Registrar usuario (Backend Node.js)
    // Usamos Response<T> para poder leer el código de error (400, 409, etc)
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    // 3. OpenWeatherMap (Opcional)
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
