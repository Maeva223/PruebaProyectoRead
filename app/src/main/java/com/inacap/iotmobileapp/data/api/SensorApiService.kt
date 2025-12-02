package com.inacap.iotmobileapp.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

// Modelo de respuesta de OpenWeatherMap
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

// Modelo simplificado para la UI
data class SensorData(
    val temperature: Double,    // Temperatura en Celsius
    val humidity: Int,          // Humedad en %
    val city: String,
    val timestamp: Long
)

// Interfaz del servicio API de OpenWeatherMap
interface SensorApiService {

    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") city: String,           // Ciudad, ej: "La Serena,CL"
        @Query("appid") apiKey: String,     // API Key de OpenWeatherMap
        @Query("units") units: String = "metric"  // metric = Celsius
    ): WeatherResponse
}
