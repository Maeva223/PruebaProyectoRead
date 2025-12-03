package com.inacap.iotmobileapp.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- Modelos Comunes ---

data class BackendSensorResponse(
    val temperature: Double,
    val humidity: Double,
    val timestamp: String
)

// --- Modelos Registro ---
data class RegisterRequest(
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val message: String?,
    val error: String?,
    val success: Boolean?
)

// --- Modelos Login ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val message: String?,
    val user: BackendUser?
)

data class BackendUser(
    val id: Long,
    val name: String,
    val email: String
    // El backend no devuelve 'last_name' en el login según el código que vimos,
    // pero si lo devolviera podríamos agregarlo aquí.
)

// --- Modelos OpenWeatherMap ---
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

data class SensorData(
    val temperature: Double,
    val humidity: Int,
    val city: String,
    val timestamp: Long
)

// --- INTERFAZ 1: Tu Backend Node.js ---
interface BackendApiService {
    // Obtener datos simulados de tu servidor
    @GET("iot/data")
    suspend fun getBackendSensorData(): BackendSensorResponse

    // Registrar usuario en tu servidor
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    // Login de usuario en tu servidor
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}

// --- INTERFAZ 2: OpenWeatherMap API ---
interface WeatherApiService {
    // Obtener clima real
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
