package com.inacap.iotmobileapp.ui.recovery

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.ui.components.*
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun CreatePasswordScreen(
    email: String,
    code: String,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CreatePasswordViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(email, code) {
        viewModel.setEmailAndCode(email, code)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "CREAR CONTRASEÑAS",
                onNavigateBack = null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            PasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "INGRESE CLAVE",
                isError = uiState.errorMessage.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = "REPETIR CLAVE",
                isError = uiState.errorMessage.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "CREAR",
                onClick = { viewModel.onCreatePassword(onNavigateToLogin) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = uiState.errorMessage)
            SuccessMessage(message = uiState.successMessage)

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

class CreatePasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())

    private val _uiState = MutableStateFlow(CreatePasswordUiState())
    val uiState: StateFlow<CreatePasswordUiState> = _uiState

    private var email: String = ""
    private var code: String = ""

    fun setEmailAndCode(email: String, code: String) {
        this.email = email
        this.code = code
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = "")
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = "")
    }

    fun onCreatePassword(onSuccess: () -> Unit) {
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Complete ambos campos")
            return
        }

        if (!Validators.isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Contraseña débil. ${Validators.getPasswordErrorMessage(password)}"
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Contraseñas no coinciden")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.updatePassword(email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Contraseña cambiada correctamente"
                    )
                    kotlinx.coroutines.delay(1500)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error: ${error.message}"
                    )
                }
            )
        }
    }
}

data class CreatePasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)
