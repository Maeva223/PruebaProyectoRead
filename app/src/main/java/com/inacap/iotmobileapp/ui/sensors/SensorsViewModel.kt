package com.inacap.iotmobileapp.ui.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.data.api.SensorData
import com.inacap.iotmobileapp.utils.ApiConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorsViewModel : ViewModel() {

    private val apiService = RetrofitClient.sensorApiService

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                fetchSensorData()
                delay(10000) // Actualizar cada 10 segundos (API gratis tiene límite)
            }
        }
    }

    private fun fetchSensorData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                // Llamar a la API real de OpenWeatherMap
                val response = apiService.getWeatherData(
                    city = ApiConfig.DEFAULT_CITY,
                    apiKey = ApiConfig.OPENWEATHER_API_KEY
                )

                _sensorData.value = SensorData(
                    temperature = response.main.temp,
                    humidity = response.main.humidity,
                    city = response.name,
                    timestamp = response.dt * 1000 // API devuelve segundos, Java usa millis
                )

            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("401") == true -> "API Key inválida. Revisa ApiConfig.kt"
                    e.message?.contains("404") == true -> "Ciudad no encontrada"
                    else -> "Error de conexión: ${e.message}"
                }
                
                // Si falla, mantenemos el último dato o mostramos null
                if (_sensorData.value == null) {
                    _sensorData.value = SensorData(
                        temperature = 0.0,
                        humidity = 0,
                        city = "Sin Conexión",
                        timestamp = System.currentTimeMillis()
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        fetchSensorData()
    }
}
