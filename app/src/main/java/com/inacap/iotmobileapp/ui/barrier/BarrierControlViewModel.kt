package com.inacap.iotmobileapp.ui.barrier

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.utils.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class BarrierState {
    OPEN,
    CLOSED,
    OPENING,
    CLOSING,
    UNKNOWN
}

class BarrierControlViewModel(application: Application) : AndroidViewModel(application) {

    private val _barrierState = MutableStateFlow(BarrierState.CLOSED)
    val barrierState: StateFlow<BarrierState> = _barrierState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun openBarrier() {
        viewModelScope.launch {
            _isLoading.value = true
            _barrierState.value = BarrierState.OPENING
            _message.value = "Enviando comando de apertura..."

            try {
                val user = UserSession.currentUser
                if (user == null) {
                    _message.value = "Error: Usuario no autenticado"
                    _barrierState.value = BarrierState.UNKNOWN
                    _isLoading.value = false
                    return@launch
                }

                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = RetrofitClient.rfidSensorApiService.openBarrier(authToken)

                if (response.isSuccessful) {
                    _barrierState.value = BarrierState.OPEN
                    _message.value = response.body()?.mensaje ?: "Barrera abierta"

                    // Simular cierre automático después de 10 segundos
                    delay(10000)
                    _barrierState.value = BarrierState.CLOSED
                    _message.value = "Barrera cerrada automáticamente"
                } else {
                    _message.value = "Error: ${response.message()}"
                    _barrierState.value = BarrierState.UNKNOWN
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
                _barrierState.value = BarrierState.UNKNOWN
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun closeBarrier() {
        viewModelScope.launch {
            _isLoading.value = true
            _barrierState.value = BarrierState.CLOSING
            _message.value = "Enviando comando de cierre..."

            try {
                val user = UserSession.currentUser
                if (user == null) {
                    _message.value = "Error: Usuario no autenticado"
                    _barrierState.value = BarrierState.UNKNOWN
                    _isLoading.value = false
                    return@launch
                }

                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = RetrofitClient.rfidSensorApiService.closeBarrier(authToken)

                if (response.isSuccessful) {
                    _barrierState.value = BarrierState.CLOSED
                    _message.value = response.body()?.mensaje ?: "Barrera cerrada"
                } else {
                    _message.value = "Error: ${response.message()}"
                    _barrierState.value = BarrierState.UNKNOWN
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
                _barrierState.value = BarrierState.UNKNOWN
            } finally {
                _isLoading.value = false
            }
        }
    }
}
