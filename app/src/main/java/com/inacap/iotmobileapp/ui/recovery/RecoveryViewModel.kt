package com.inacap.iotmobileapp.ui.recovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecoveryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = "")
    }

    fun onCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(code = code, errorMessage = "")
    }

    fun onSendCode() {
        val email = _uiState.value.email.trim()

        if (email.isEmpty() || !Validators.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email inválido")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.generateRecoveryCode(email)
            result.fold(
                onSuccess = { code ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Código enviado: $code (simulado)",
                        codeSent = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al enviar código"
                    )
                }
            )
        }
    }

    fun onVerifyCode(onSuccess: (String, String) -> Unit) {
        val email = _uiState.value.email.trim()
        val code = _uiState.value.code

        if (code.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Ingrese el código")
            return
        }

        if (!Validators.isValidRecoveryCode(code)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Código debe ser numérico de 5 dígitos")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.verifyRecoveryCode(email, code)
            result.fold(
                onSuccess = { recoveryCode ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess(email, code)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Código incorrecto o vencido"
                    )
                }
            )
        }
    }
}

data class RecoveryUiState(
    val email: String = "",
    val code: String = "",
    val codeSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)
