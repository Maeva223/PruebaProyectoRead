package com.inacap.iotmobileapp.ui.profile

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.DeveloperProfile
import com.inacap.iotmobileapp.ui.components.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    userId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: EditProfileViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "EDITAR MI PERFIL", onNavigateBack = onNavigateBack)
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
                // Avatar Emoji
                Text("Selecciona tu avatar:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("👨‍💻", "👩‍💻", "🧑‍💻", "😎", "🚀", "💡").forEach { emoji ->
                        ElevatedButton(
                            onClick = { viewModel.onAvatarChange(emoji) },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (uiState.avatarEmoji == emoji)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(emoji, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AppTextField(
                    value = uiState.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = "Nombre Completo"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.role,
                    onValueChange = { viewModel.onRoleChange(it) },
                    label = "Rol (ej: Full Stack Developer)"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = "Email Institucional",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.institution,
                    onValueChange = { viewModel.onInstitutionChange(it) },
                    label = "Institución (ej: INACAP La Serena)"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.career,
                    onValueChange = { viewModel.onCareerChange(it) },
                    label = "Carrera"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.section,
                    onValueChange = { viewModel.onSectionChange(it) },
                    label = "Sección (ej: 001V)"
                )
                Spacer(modifier = Modifier.height(16.dp))

                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Redes Sociales (opcional):", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))

                AppTextField(
                    value = uiState.github,
                    onValueChange = { viewModel.onGithubChange(it) },
                    label = "GitHub (ej: github.com/usuario)"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.linkedin,
                    onValueChange = { viewModel.onLinkedinChange(it) },
                    label = "LinkedIn (ej: linkedin.com/in/usuario)"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = uiState.portfolio,
                    onValueChange = { viewModel.onPortfolioChange(it) },
                    label = "Portafolio (opcional)"
                )
                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = "GUARDAR PERFIL",
                    onClick = { viewModel.onSave(userId, onNavigateBack) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(message = uiState.errorMessage)
                SuccessMessage(message = uiState.successMessage)
            }
        }
    }
}

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val profileDao = database.developerProfileDao()

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    fun loadProfile(userId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val profile = profileDao.getProfileByUserId(userId)
            if (profile != null) {
                _uiState.value = EditProfileUiState(
                    fullName = profile.fullName,
                    role = profile.role,
                    email = profile.email,
                    institution = profile.institution,
                    career = profile.career,
                    section = profile.section,
                    github = profile.github,
                    linkedin = profile.linkedin,
                    portfolio = profile.portfolio,
                    avatarEmoji = profile.avatarEmoji,
                    isLoading = false
                )
            } else {
                // Valores por defecto
                _uiState.value = _uiState.value.copy(
                    institution = "INACAP La Serena",
                    career = "Ingeniería en Informática",
                    isLoading = false
                )
            }
        }
    }

    fun onFullNameChange(value: String) { _uiState.value = _uiState.value.copy(fullName = value, errorMessage = "") }
    fun onRoleChange(value: String) { _uiState.value = _uiState.value.copy(role = value, errorMessage = "") }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, errorMessage = "") }
    fun onInstitutionChange(value: String) { _uiState.value = _uiState.value.copy(institution = value, errorMessage = "") }
    fun onCareerChange(value: String) { _uiState.value = _uiState.value.copy(career = value, errorMessage = "") }
    fun onSectionChange(value: String) { _uiState.value = _uiState.value.copy(section = value, errorMessage = "") }
    fun onGithubChange(value: String) { _uiState.value = _uiState.value.copy(github = value, errorMessage = "") }
    fun onLinkedinChange(value: String) { _uiState.value = _uiState.value.copy(linkedin = value, errorMessage = "") }
    fun onPortfolioChange(value: String) { _uiState.value = _uiState.value.copy(portfolio = value, errorMessage = "") }
    fun onAvatarChange(value: String) { _uiState.value = _uiState.value.copy(avatarEmoji = value, errorMessage = "") }

    fun onSave(userId: Long, onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validaciones
        if (state.fullName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre completo es obligatorio")
            return
        }
        if (state.role.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El rol es obligatorio")
            return
        }
        if (state.email.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El email es obligatorio")
            return
        }
        if (state.section.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La sección es obligatoria")
            return
        }

        viewModelScope.launch {
            try {
                val profile = DeveloperProfile(
                    userId = userId,
                    fullName = state.fullName.trim(),
                    role = state.role.trim(),
                    email = state.email.trim(),
                    institution = state.institution.trim(),
                    career = state.career.trim(),
                    section = state.section.trim(),
                    github = state.github.trim(),
                    linkedin = state.linkedin.trim(),
                    portfolio = state.portfolio.trim(),
                    avatarEmoji = state.avatarEmoji
                )
                profileDao.insertOrUpdateProfile(profile)
                _uiState.value = state.copy(successMessage = "Perfil guardado correctamente")
                kotlinx.coroutines.delay(1500)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(errorMessage = "Error al guardar: ${e.message}")
            }
        }
    }
}

data class EditProfileUiState(
    val fullName: String = "",
    val role: String = "",
    val email: String = "",
    val institution: String = "",
    val career: String = "",
    val section: String = "",
    val github: String = "",
    val linkedin: String = "",
    val portfolio: String = "",
    val avatarEmoji: String = "👨‍💻",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)
