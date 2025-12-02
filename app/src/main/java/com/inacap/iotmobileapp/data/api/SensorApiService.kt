package com.inacap.iotmobileapp.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

// Modelo de respuesta de OpenWeatherMap (para compatibilidad si se necesita)
data class WeatherResponse(
    val main: MainData,
    val name: String,
    val dt: Long
)

data class MainData(
    val temp: Double,           // Temperatura en Kelvin
    val humidity: Int,          // Humedad en %
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double
)

// Modelo de respuesta de TU Backend Node.js
// GET /api/iot/data
data class BackendSensorResponse(
    val temperature: Double,
    val humidity: Double, // Tu backend devuelve Double (ej: 23.5), aunque la app usa Int a veces
    val timestamp: String
)

// Modelo simplificado para la UI
data class SensorData(
    val temperature: Double,    // Temperatura en Celsius
    val humidity: Int,          // Humedad en %
    val city: String,
    val timestamp: Long
)

// Interfaz del servicio API
interface SensorApiService {

    // Endpoint de tu backend: GET http://54.85.65.240:3000/iot/data
    @GET("iot/data")
    suspend fun getBackendSensorData(): BackendSensorResponse

    // (Opcional) Endpoint de OpenWeatherMap
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
