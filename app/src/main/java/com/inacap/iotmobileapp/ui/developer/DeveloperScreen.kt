package com.inacap.iotmobileapp.ui.developer

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.DeveloperProfile
import com.inacap.iotmobileapp.ui.components.AppTopBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun DeveloperScreen(userId: Long, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: DeveloperViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }
    Scaffold(
        topBar = {
            AppTopBar(title = "DATOS DESARROLLADOR(ES)", onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (profile == null) {
                // Mostrar mensaje si no hay perfil
                Text(
                    text = "No has configurado tu perfil aún",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ve al Menú Principal → EDITAR MI PERFIL",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                // Avatar
                Text(
                    text = profile!!.avatarEmoji,
                    fontSize = 80.sp,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = profile!!.fullName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("Rol:", profile!!.role)
                InfoRow("Email:", profile!!.email)
                InfoRow("Institución:", profile!!.institution)
                InfoRow("Carrera:", profile!!.career)
                InfoRow("Sección:", profile!!.section)

                if (profile!!.github.isNotBlank() || profile!!.linkedin.isNotBlank() || profile!!.portfolio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (profile!!.github.isNotBlank()) {
                    InfoRow("GitHub:", profile!!.github)
                }
                if (profile!!.linkedin.isNotBlank()) {
                    InfoRow("LinkedIn:", profile!!.linkedin)
                }
                if (profile!!.portfolio.isNotBlank()) {
                    InfoRow("Portafolio:", profile!!.portfolio)
                }
            }
        }
    }
}

class DeveloperViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val profileDao = database.developerProfileDao()

    private val _profile = MutableStateFlow<DeveloperProfile?>(null)
    val profile: StateFlow<DeveloperProfile?> = _profile

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _profile.value = profileDao.getProfileByUserId(userId)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "$label ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.Gray)
    }
}
