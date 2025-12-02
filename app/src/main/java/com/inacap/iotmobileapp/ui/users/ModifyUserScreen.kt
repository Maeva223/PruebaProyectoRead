package com.inacap.iotmobileapp.ui.users

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.ui.components.*
import com.inacap.iotmobileapp.ui.theme.IoTMobileAppTheme
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ModifyUserScreen(userId: Long, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ModifyUserViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    ModifyUserScreenContent(
        uiState = uiState,
        onNombresChange = viewModel::onNombresChange,
        onApellidosChange = viewModel::onApellidosChange,
        onEmailChange = viewModel::onEmailChange,
        onModify = { viewModel.onModify(onNavigateBack) },
        onDelete = { viewModel.onDelete(onNavigateBack) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ModifyUserScreenContent(
    uiState: ModifyUserUiState,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onModify: () -> Unit,
    onDelete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "MODIFICAR DATOS USUARIO", onNavigateBack = onNavigateBack)
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                AppTextField(value = uiState.nombres, onValueChange = onNombresChange, label = "INGRESE NOMBRES")
                Spacer(modifier = Modifier.height(12.dp))
                AppTextField(value = uiState.apellidos, onValueChange = onApellidosChange, label = "INGRESE APELLIDOS")
                Spacer(modifier = Modifier.height(12.dp))
                AppTextField(value = uiState.email, onValueChange = onEmailChange, label = "INGRESE E-MAIL", keyboardType = KeyboardType.Email)
                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onModify,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("MODIFICAR")
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("ELIMINAR")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(message = uiState.errorMessage)
                SuccessMessage(message = uiState.successMessage)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ModifyUserScreenPreview() {
    IoTMobileAppTheme {
        ModifyUserScreenContent(
            uiState = ModifyUserUiState(
                nombres = "Juan",
                apellidos = "Pérez",
                email = "juan.perez@example.com"
            ),
            onNombresChange = {},
            onApellidosChange = {},
            onEmailChange = {},
            onModify = {},
            onDelete = {},
            onNavigateBack = {}
        )
    }
}

class ModifyUserViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())

    private val _uiState = MutableStateFlow(ModifyUserUiState())
    val uiState: StateFlow<ModifyUserUiState> = _uiState

    private var currentUser: User? = null

    fun loadUser(userId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val user = database.userDao().getUserById(userId)
            currentUser = user
            if (user != null) {
                _uiState.value = ModifyUserUiState(
                    nombres = user.nombres,
                    apellidos = user.apellidos,
                    email = user.email,
                    isLoading = false
                )
            }
        }
    }

    fun onNombresChange(value: String) { _uiState.value = _uiState.value.copy(nombres = value, errorMessage = "") }
    fun onApellidosChange(value: String) { _uiState.value = _uiState.value.copy(apellidos = value, errorMessage = "") }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, errorMessage = "") }

    fun onModify(onSuccess: () -> Unit) {
        val user = currentUser ?: return
        val nombres = _uiState.value.nombres.trim()
        val apellidos = _uiState.value.apellidos.trim()
        val email = _uiState.value.email.trim()

        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Campos vacíos")
            return
        }

        viewModelScope.launch {
            val updatedUser = user.copy(nombres = nombres, apellidos = apellidos, email = email)
            val result = repository.updateUser(updatedUser)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "Usuario modificado")
                    kotlinx.coroutines.delay(1500)
                    onSuccess()
                },
                onFailure = { _uiState.value = _uiState.value.copy(errorMessage = "Error al modificar") }
            )
        }
    }

    fun onDelete(onSuccess: () -> Unit) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val result = repository.deleteUser(user)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "Usuario eliminado")
                    kotlinx.coroutines.delay(1500)
                    onSuccess()
                },
                onFailure = { _uiState.value = _uiState.value.copy(errorMessage = "Error al eliminar") }
            )
        }
    }
}

data class ModifyUserUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)
