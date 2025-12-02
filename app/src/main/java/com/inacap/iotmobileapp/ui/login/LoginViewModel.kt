package com.inacap.iotmobileapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.utils.Constants
import com.inacap.iotmobileapp.utils.UserSession
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(
        database.userDao(),
        database.recoveryCodeDao()
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = ""
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = ""
        )
    }

    fun onLogin(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Validaciones
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Campos obligatorios vacíos"
            )
            return
        }

        if (!Validators.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Formato de email inválido"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Verificar si el usuario está bloqueado
                val user = repository.getUserByEmail(email)

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales inválidas"
                    )
                    return@launch
                }

                if (user.isBlocked) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario bloqueado. Contacte al administrador"
                    )
                    return@launch
                }

                // Intentar login
                val loggedUser = repository.login(email, password)

                if (loggedUser != null) {
                    // Login exitoso, resetear intentos fallidos
                    repository.updateFailedAttempts(email, 0)

                    // Guardar sesión del usuario
                    UserSession.login(loggedUser.id)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Login correcto"
                    )

                    // Trigger animación de éxito
                    triggerSuccessAnimation()

                    // Esperar un poco para que se vea la animación
                    kotlinx.coroutines.delay(1500)
                    onSuccess()
                } else {
                    // Login fallido, incrementar intentos
                    val failedAttempts = user.failedLoginAttempts + 1

                    repository.updateFailedAttempts(email, failedAttempts)

                    // Bloquear si se alcanza el máximo de intentos
                    if (failedAttempts >= Constants.MAX_LOGIN_ATTEMPTS) {
                        repository.updateBlockedStatus(email, true)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Usuario bloqueado por múltiples intentos fallidos"
                        )
                        triggerErrorAnimation()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Credenciales inválidas. Intentos restantes: ${Constants.MAX_LOGIN_ATTEMPTS - failedAttempts}"
                        )
                        triggerErrorAnimation()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = "",
            successMessage = ""
        )
    }

    fun setSuccessMessage(message: String) {
        _uiState.value = _uiState.value.copy(
            successMessage = message,
            errorMessage = ""
        )
    }

    fun togglePasswordVisibility() {
        val newVisibility = !_uiState.value.passwordVisible
        _uiState.value = _uiState.value.copy(
            passwordVisible = newVisibility,
            animationState = if (newVisibility) LoginAnimationState.PASSWORD_SHOW else LoginAnimationState.PASSWORD_HIDE
        )
        // Volver a idle después de la animación
        viewModelScope.launch {
            kotlinx.coroutines.delay(700)
            if (_uiState.value.animationState == LoginAnimationState.PASSWORD_SHOW ||
                _uiState.value.animationState == LoginAnimationState.PASSWORD_HIDE) {
                _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.IDLE)
            }
        }
    }

    private fun triggerErrorAnimation() {
        _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.ERROR)
        // Volver a idle después de la animación
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.IDLE)
        }
    }

    private fun triggerSuccessAnimation() {
        _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.SUCCESS)
    }
}

/**
 * Estado de la UI de Login
 */
enum class LoginAnimationState {
    IDLE,           // Saludando
    PASSWORD_SHOW,  // Mostrando password
    PASSWORD_HIDE,  // Ocultando password
    ERROR,          // Error - sacudir cabeza
    SUCCESS         // Éxito - checkmark
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = "",
    val animationState: LoginAnimationState = LoginAnimationState.IDLE,
    val passwordVisible: Boolean = false
)
