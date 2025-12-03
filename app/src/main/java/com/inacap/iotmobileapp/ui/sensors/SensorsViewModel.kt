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

    // IMPORTANTE: Ahora usamos el servicio de tu Backend (Node.js)
    private val backendService = RetrofitClient.backendApiService

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
                delay(2000) // Actualizar cada 2 segundos
            }
        }
    }

    private fun fetchSensorData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                // Llamada a tu Backend en AWS (http://54.85.65.240/iot/data)
                val response = backendService.getBackendSensorData()

                _sensorData.value = SensorData(
                    temperature = response.temperature,
                    humidity = response.humidity.toInt(), // Convertimos el Double a Int para la UI
                    city = "Lab IoT (Node.js)", // Nombre fijo para tu backend
                    timestamp = System.currentTimeMillis() // Usamos el tiempo actual
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando al Backend: ${e.message}"
                
                // Si falla, mostramos estado desconectado
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
