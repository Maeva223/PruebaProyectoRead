package com.inacap.iotmobileapp.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToUserManagement: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSensorManagement: () -> Unit = {},
    onNavigateToBarrierControl: () -> Unit = {},
    onLogout: () -> Unit
) {
    // Reloj en tiempo real
    var currentTime by remember { mutableStateOf(getCurrentDateTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentDateTime()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IoT Mobile App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
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
            // Logo
            Text("AC", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            // Fecha y Hora en tiempo real
            Text(
                text = currentTime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Botones del menú
            MenuButton(icon = Icons.Default.Person, text = "CRUD USUARIO", onClick = onNavigateToUserManagement)
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(icon = Icons.Default.Cloud, text = "DATOS SENSOR", onClick = onNavigateToSensors)
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(icon = Icons.Default.Code, text = "DESARROLLADOR", onClick = onNavigateToDeveloper)
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(icon = Icons.Default.Edit, text = "EDITAR MI PERFIL", onClick = onNavigateToEditProfile)
            Spacer(modifier = Modifier.height(16.dp))

            // NUEVOS BOTONES - Evaluación III: Sistema de Control de Acceso RFID
            MenuButton(
                icon = Icons.Default.Sensors,
                text = "GESTIÓN DE SENSORES RFID",
                onClick = onNavigateToSensorManagement,
                color = Color(0xFF2196F3) // Azul
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                icon = Icons.Default.DoorFront,
                text = "LLAVERO DIGITAL",
                onClick = onNavigateToBarrierControl,
                color = Color(0xFF4CAF50) // Verde
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de cerrar sesión
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("CERRAR SESIÓN", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color? = null
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color ?: MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuScreenPreview() {
    MaterialTheme {
        MainMenuScreen(
            onNavigateToUserManagement = {},
            onNavigateToSensors = {},
            onNavigateToDeveloper = {},
            onNavigateToEditProfile = {},
            onLogout = {}
        )
    }
}
