package com.inacap.iotmobileapp.ui.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.api.models.SensorDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorManagementScreen(
    viewModel: SensorManagementViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val sensors by viewModel.sensors.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Cerrar diálogo automáticamente cuando el registro sea exitoso
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            showAddDialog = false
            viewModel.resetRegistrationSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Sensores RFID") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, "Agregar Sensor")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!isAdmin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        "Solo administradores pueden gestionar sensores",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sensors) { sensor ->
                        SensorCard(
                            sensor = sensor,
                            isAdmin = isAdmin,
                            onActivate = { viewModel.activateSensor(sensor.id_sensor) },
                            onDeactivate = { viewModel.deactivateSensor(sensor.id_sensor) },
                            onBlock = { viewModel.blockSensor(sensor.id_sensor) },
                            onMarkLost = { viewModel.markSensorAsLost(sensor.id_sensor) },
                            onDelete = { viewModel.deleteSensor(sensor.id_sensor) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSensorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { mac, type, alias ->
                    viewModel.registerSensor(mac, type, alias)
                    // El diálogo se cerrará automáticamente cuando registrationSuccess sea true
                }
            )
        }
    }
}

@Composable
fun SensorCard(
    sensor: SensorDTO,
    isAdmin: Boolean,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onBlock: () -> Unit,
    onMarkLost: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val backgroundColor = when (sensor.estado) {
        "ACTIVO" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        "INACTIVO" -> Color(0xFFFF9800).copy(alpha = 0.1f)
        "BLOQUEADO" -> Color(0xFFF44336).copy(alpha = 0.1f)
        "PERDIDO" -> Color(0xFF9C27B0).copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.alias ?: "Sin alias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MAC: ${sensor.codigo_sensor}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Tipo: ${sensor.tipo}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(estado = sensor.estado)
            }

            if (isAdmin) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Opciones")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (sensor.estado != "ACTIVO") {
                            DropdownMenuItem(
                                text = { Text("Activar") },
                                onClick = {
                                    onActivate()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, null) }
                            )
                        }
                        if (sensor.estado == "ACTIVO") {
                            DropdownMenuItem(
                                text = { Text("Desactivar") },
                                onClick = {
                                    onDeactivate()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Close, null) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Bloquear") },
                            onClick = {
                                onBlock()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Block, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Marcar como Perdido") },
                            onClick = {
                                onMarkLost()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Warning, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(estado: String) {
    val color = when (estado) {
        "ACTIVO" -> Color(0xFF4CAF50)
        "INACTIVO" -> Color(0xFFFF9800)
        "BLOQUEADO" -> Color(0xFFF44336)
        "PERDIDO" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddSensorDialog(
    onDismiss: () -> Unit,
    onConfirm: (mac: String, type: String, alias: String) -> Unit
) {
    var mac by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Tarjeta") }
    var alias by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nuevo Sensor RFID") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mac,
                    onValueChange = { mac = it.uppercase() },
                    label = { Text("MAC del Sensor") },
                    placeholder = { Text("AA:BB:CC:DD") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de Sensor:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "Tarjeta",
                        onClick = { selectedType = "Tarjeta" },
                        label = { Text("Tarjeta") }
                    )
                    FilterChip(
                        selected = selectedType == "Llavero",
                        onClick = { selectedType = "Llavero" },
                        label = { Text("Llavero") }
                    )
                }

                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Alias (opcional)") },
                    placeholder = { Text("Ej: Tarjeta de Juan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Acerca la tarjeta al lector RFID para obtener la MAC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(mac, selectedType, alias) },
                enabled = mac.isNotBlank()
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
