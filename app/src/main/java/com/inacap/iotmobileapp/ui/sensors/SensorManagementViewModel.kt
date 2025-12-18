package com.inacap.iotmobileapp.ui.sensors

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.data.api.models.*
import com.inacap.iotmobileapp.utils.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val _sensors = MutableStateFlow<List<SensorDTO>>(emptyList())
    val sensors: StateFlow<List<SensorDTO>> = _sensors

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        checkUserRole()
        loadSensors()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            val user = UserSession.currentUser
            _isAdmin.value = user?.rol == "ADMIN"
        }
    }

    fun loadSensors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = UserSession.currentUser
                val departmentId = user?.id_departamento

                if (departmentId == null) {
                    _message.value = "Usuario no tiene departamento asignado"
                    _isLoading.value = false
                    return@launch
                }

                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = RetrofitClient.rfidSensorApiService.getSensorsByDepartment(
                    departmentId,
                    authToken
                )

                if (response.isSuccessful) {
                    _sensors.value = response.body()?.sensores ?: emptyList()
                } else {
                    _message.value = "Error al cargar sensores: ${response.code()}"
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    fun registerSensor(mac: String, type: String, alias: String) {
        viewModelScope.launch {
            _registrationSuccess.value = false  // Resetear estado
            try {
                val user = UserSession.currentUser ?: return@launch
                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val request = RegisterSensorRequest(
                    codigo_sensor = mac.trim().uppercase(),
                    tipo = type,
                    alias = alias.ifBlank { null }
                )

                val response = RetrofitClient.rfidSensorApiService.registerSensor(request, authToken)

                if (response.isSuccessful) {
                    _message.value = "Sensor registrado exitosamente"
                    loadSensors()
                    _registrationSuccess.value = true  // Indicar éxito para cerrar el diálogo
                } else {
                    _message.value = "Error al registrar: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun resetRegistrationSuccess() {
        _registrationSuccess.value = false
    }

    fun activateSensor(sensorId: Int) {
        updateSensorState(sensorId, "activar") {
            RetrofitClient.rfidSensorApiService.activateSensor(sensorId, it)
        }
    }

    fun deactivateSensor(sensorId: Int) {
        updateSensorState(sensorId, "desactivar") {
            RetrofitClient.rfidSensorApiService.deactivateSensor(sensorId, it)
        }
    }

    fun blockSensor(sensorId: Int) {
        updateSensorState(sensorId, "bloquear") {
            RetrofitClient.rfidSensorApiService.blockSensor(sensorId, it)
        }
    }

    fun markSensorAsLost(sensorId: Int) {
        updateSensorState(sensorId, "marcar como perdido") {
            RetrofitClient.rfidSensorApiService.markSensorAsLost(sensorId, it)
        }
    }

    fun deleteSensor(sensorId: Int) {
        updateSensorState(sensorId, "eliminar") {
            RetrofitClient.rfidSensorApiService.deleteSensor(sensorId, it)
        }
    }

    private fun updateSensorState(
        sensorId: Int,
        action: String,
        apiCall: suspend (String) -> retrofit2.Response<RegisterSensorResponse>
    ) {
        viewModelScope.launch {
            try {
                val user = UserSession.currentUser ?: return@launch
                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = apiCall(authToken)

                if (response.isSuccessful) {
                    _message.value = response.body()?.mensaje ?: "Operación exitosa"
                    loadSensors()
                } else {
                    _message.value = "Error al $action: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }
}
