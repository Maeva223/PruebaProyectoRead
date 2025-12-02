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
                delay(5000) // Actualizar cada 5 segundos
            }
        }
    }

    private fun fetchSensorData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                // Llamar a tu Backend Node.js
                val response = apiService.getBackendSensorData()

                _sensorData.value = SensorData(
                    temperature = response.temperature,
                    humidity = response.humidity.toInt(), // Convertir a Int para la UI
                    city = "Lab IoT (Node.js)",
                    timestamp = System.currentTimeMillis()
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar con Node.js: ${e.message}"
                
                // Mantener datos anteriores o mostrar simulados si falla la conexión
                if (_sensorData.value == null) {
                    _sensorData.value = SensorData(
                        temperature = 0.0,
                        humidity = 0,
                        city = "Desconectado",
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
