package com.inacap.iotmobileapp.ui.sensors

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.inacap.iotmobileapp.ui.components.AppTopBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val viewModel: SensorsViewModel = viewModel()

    val sensorData by viewModel.sensorData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val temperature = sensorData?.temperature ?: 0.0
    val humidity = sensorData?.humidity ?: 0

    var bulbOn by remember { mutableStateOf(false) }
    var flashOn by remember { mutableStateOf(false) }
    var bulbMessage by remember { mutableStateOf("") }
    var flashMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(title = "DATOS SENSORES", onNavigateBack = onNavigateBack)
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
            // Título y estado
            Text(
                text = sensorData?.city ?: "Cargando...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Humedad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("HUMEDAD", fontWeight = FontWeight.Bold)
                    Text("$humidity%", fontSize = 24.sp, color = Color.Blue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Temperatura
            Row(verticalAlignment = Alignment.CenterVertically) {
                val tempIcon = if (temperature > 20) Icons.Default.Thermostat else Icons.Default.AcUnit
                val tempColor = if (temperature > 20) Color.Red else Color.Blue

                Icon(tempIcon, contentDescription = null, tint = tempColor, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("TEMPERATURA", fontWeight = FontWeight.Bold)
                    Text("${String.format("%.1f", temperature)}°", fontSize = 24.sp, color = tempColor)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Ampolleta
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Ampolleta",
                    tint = if (bulbOn) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            bulbOn = !bulbOn
                            bulbMessage = if (bulbOn) "Ampolleta encendida" else "Ampolleta apagada"
                        }
                )
                if (bulbMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(bulbMessage, fontSize = 14.sp, color = if (bulbOn) Color(0xFFFFD700) else Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Linterna
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (flashOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    contentDescription = "Linterna",
                    tint = if (flashOn) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            if (cameraPermissionState.status.isGranted) {
                                flashOn = !flashOn
                                toggleFlashlight(context, flashOn)
                                flashMessage = if (flashOn) "Linterna activada" else "Linterna desactivada"
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                )
                if (flashMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(flashMessage, fontSize = 14.sp, color = if (flashOn) Color.Yellow else Color.Gray)
                }
            }
        }
    }
}

fun toggleFlashlight(context: Context, turnOn: Boolean) {
    try {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraId, turnOn)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
