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
    val email: String,
    val rol: String? = null,
    val id_departamento: Int? = null
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

// --- INTERFAZ 3: API de Sensores RFID y Control de Acceso (Evaluación III) ---
interface RFIDSensorApiService {

    // ==================== GESTIÓN DE SENSORES ====================

    @GET("api/sensors/department/{departmentId}")
    suspend fun getSensorsByDepartment(
        @retrofit2.http.Path("departmentId") departmentId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.SensorResponse>

    @POST("api/sensors/register")
    suspend fun registerSensor(
        @Body request: com.inacap.iotmobileapp.data.api.models.RegisterSensorRequest,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    @retrofit2.http.PUT("api/sensors/{sensorId}/activate")
    suspend fun activateSensor(
        @retrofit2.http.Path("sensorId") sensorId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    @retrofit2.http.PUT("api/sensors/{sensorId}/deactivate")
    suspend fun deactivateSensor(
        @retrofit2.http.Path("sensorId") sensorId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    @retrofit2.http.PUT("api/sensors/{sensorId}/block")
    suspend fun blockSensor(
        @retrofit2.http.Path("sensorId") sensorId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    @retrofit2.http.PUT("api/sensors/{sensorId}/mark-lost")
    suspend fun markSensorAsLost(
        @retrofit2.http.Path("sensorId") sensorId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    @retrofit2.http.DELETE("api/sensors/{sensorId}")
    suspend fun deleteSensor(
        @retrofit2.http.Path("sensorId") sensorId: Int,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.RegisterSensorResponse>

    // ==================== CONTROL DE ACCESO ====================

    @POST("api/access/manual-open")
    suspend fun openBarrier(
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.ManualControlResponse>

    @POST("api/access/manual-close")
    suspend fun closeBarrier(
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.ManualControlResponse>

    @GET("api/access/barrier-status")
    suspend fun getBarrierStatus(): Response<com.inacap.iotmobileapp.data.api.models.BarrierStatusResponse>

    @GET("api/access/history/{departmentId}")
    suspend fun getAccessHistory(
        @retrofit2.http.Path("departmentId") departmentId: Int,
        @Query("limit") limit: Int = 50,
        @retrofit2.http.Header("Authorization") token: String
    ): Response<com.inacap.iotmobileapp.data.api.models.EventoAccesoResponse>
}
