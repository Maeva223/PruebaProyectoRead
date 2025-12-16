# 📱 IMPLEMENTACIÓN ANDROID - EVALUACIÓN SUMATIVA III

## Funcionalidades Críticas a Implementar

---

## 🎯 **PRIORIDAD MÁXIMA - LO QUE FALTA**

### 1. **Modificar Entidad User** ✅ (parcialmente)

Agregar campos en `User.kt`:

```kotlin
// Archivo: app/src/main/java/com/inacap/iotmobileapp/data/database/entities/User.kt

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val last_name: String? = null,  // YA EXISTE
    val email: String,
    val password: String,

    // NUEVOS CAMPOS REQUERIDOS:
    val rol: String = "OPERADOR", // "ADMIN" o "OPERADOR"
    val estado: String = "ACTIVO", // "ACTIVO", "INACTIVO", "BLOQUEADO"
    val id_departamento: Int? = null, // FK a departamentos

    val isBlocked: Boolean = false
)
```

---

### 2. **Crear Modelos de API para Backend**

```kotlin
// Archivo: app/src/main/java/com/inacap/iotmobileapp/data/api/models/SensorModels.kt

package com.inacap.iotmobileapp.data.api.models

data class SensorResponse(
    val sensores: List<SensorDTO>
)

data class SensorDTO(
    val id_sensor: Int,
    val codigo_sensor: String,
    val estado: String,
    val tipo: String,
    val id_departamento: Int,
    val alias: String?,
    val fecha_alta: String
)

data class RegisterSensorRequest(
    val codigo_sensor: String,
    val tipo: String,
    val alias: String?
)

data class RegisterSensorResponse(
    val success: Boolean,
    val mensaje: String,
    val sensor_id: Int?
)

data class AccessValidationResponse(
    val acceso_permitido: Boolean,
    val mensaje: String,
    val sensor: SensorInfo?
)

data class SensorInfo(
    val id: Int,
    val tipo: String,
    val alias: String?,
    val departamento: String
)

data class EventoAccesoResponse(
    val eventos: List<EventoAccesoDTO>,
    val total: Int
)

data class EventoAccesoDTO(
    val id_evento: Int,
    val tipo_evento: String,
    val resultado: String,
    val mac_sensor: String?,
    val detalles: String?,
    val fecha_hora: String,
    val usuario_nombre: String?,
    val sensor_alias: String?,
    val sensor_tipo: String?
)

data class ManualControlResponse(
    val success: Boolean,
    val mensaje: String,
    val usuario: String?
)
```

---

### 3. **Crear Servicios de API Retrofit**

```kotlin
// Archivo: app/src/main/java/com/inacap/iotmobileapp/data/api/SensorApiService.kt

package com.inacap.iotmobileapp.data.api

import com.inacap.iotmobileapp.data.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface SensorApiService {

    // Gestión de sensores
    @GET("api/sensors/department/{departmentId}")
    suspend fun getSensorsByDepartment(
        @Path("departmentId") departmentId: Int,
        @Header("Authorization") token: String
    ): Response<SensorResponse>

    @POST("api/sensors/register")
    suspend fun registerSensor(
        @Body request: RegisterSensorRequest,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    @PUT("api/sensors/{sensorId}/activate")
    suspend fun activateSensor(
        @Path("sensorId") sensorId: Int,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    @PUT("api/sensors/{sensorId}/deactivate")
    suspend fun deactivateSensor(
        @Path("sensorId") sensorId: Int,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    @PUT("api/sensors/{sensorId}/block")
    suspend fun blockSensor(
        @Path("sensorId") sensorId: Int,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    @PUT("api/sensors/{sensorId}/mark-lost")
    suspend fun markSensorAsLost(
        @Path("sensorId") sensorId: Int,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    @DELETE("api/sensors/{sensorId}")
    suspend fun deleteSensor(
        @Path("sensorId") sensorId: Int,
        @Header("Authorization") token: String
    ): Response<RegisterSensorResponse>

    // Control de acceso
    @POST("api/access/manual-open")
    suspend fun openBarrier(
        @Header("Authorization") token: String
    ): Response<ManualControlResponse>

    @POST("api/access/manual-close")
    suspend fun closeBarrier(
        @Header("Authorization") token: String
    ): Response<ManualControlResponse>

    @GET("api/access/history/{departmentId}")
    suspend fun getAccessHistory(
        @Path("departmentId") departmentId: Int,
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String
    ): Response<EventoAccesoResponse>
}

// Agregar al RetrofitClient.kt existente
object RetrofitClient {
    private const val BASE_URL = "http://54.85.65.240/"

    val sensorApi: SensorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SensorApiService::class.java)
    }
}
```

---

### 4. **Pantalla Crítica: Gestión de Sensores (ADMIN)**

```kotlin
// Archivo: app/src/main/java/com/inacap/iotmobileapp/ui/sensors/SensorManagementScreen.kt

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorManagementScreen(
    viewModel: SensorManagementViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val sensors by viewModel.sensors.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
                    showAddDialog = false
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (sensor.estado) {
                "ACTIVO" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                "INACTIVO" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                "BLOQUEADO" -> Color(0xFFF44336).copy(alpha = 0.1f)
                "PERDIDO" -> Color(0xFF9C27B0).copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
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
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "MAC: ${sensor.codigo_sensor}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Tipo: ${sensor.tipo}",
                    style = MaterialTheme.typography.bodySmall
                )
                Chip(
                    text = sensor.estado,
                    color = when (sensor.estado) {
                        "ACTIVO" -> Color(0xFF4CAF50)
                        "INACTIVO" -> Color(0xFFFF9800)
                        "BLOQUEADO" -> Color(0xFFF44336)
                        "PERDIDO" -> Color(0xFF9C27B0)
                        else -> Color.Gray
                    }
                )
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
                            text = { Text("Eliminar") },
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
fun Chip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
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
                    supportingText = { Text("Acerca la tarjeta al lector para obtener la MAC") }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tipo:")
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
                    placeholder = { Text("Ej: Tarjeta de Juan") }
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
```

---

### 5. **ViewModel para Gestión de Sensores**

```kotlin
// Archivo: app/src/main/java/com/inacap/iotmobileapp/ui/sensors/SensorManagementViewModel.kt

package com.inacap.iotmobileapp.ui.sensors

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.data.api.models.*
import com.inacap.iotmobileapp.utils.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val _sensors = MutableStateFlow<List<SensorDTO>>(emptyList())
    val sensors: StateFlow<List<SensorDTO>> = _sensors

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        checkUserRole()
        loadSensors()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            val user = UserSession.currentUser
            _isAdmin.value = user?.rol == "ADMIN"
        }
    }

    fun loadSensors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = UserSession.currentUser
                val departmentId = user?.id_departamento

                if (departmentId == null) {
                    _message.value = "Usuario no tiene departamento asignado"
                    return@launch
                }

                val token = "Bearer ${user.token}" // Asumiendo que guardas el token
                val response = RetrofitClient.sensorApi.getSensorsByDepartment(
                    departmentId,
                    token
                )

                if (response.isSuccessful) {
                    _sensors.value = response.body()?.sensores ?: emptyList()
                } else {
                    _message.value = "Error al cargar sensores: ${response.code()}"
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerSensor(mac: String, type: String, alias: String) {
        viewModelScope.launch {
            try {
                val user = UserSession.currentUser ?: return@launch
                val token = "Bearer ${user.token}"

                val request = RegisterSensorRequest(
                    codigo_sensor = mac,
                    tipo = type,
                    alias = alias.ifBlank { null }
                )

                val response = RetrofitClient.sensorApi.registerSensor(request, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor registrado exitosamente"
                    loadSensors()
                } else {
                    _message.value = "Error al registrar: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun activateSensor(sensorId: Int) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${UserSession.currentUser?.token}"
                val response = RetrofitClient.sensorApi.activateSensor(sensorId, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor activado"
                    loadSensors()
                } else {
                    _message.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deactivateSensor(sensorId: Int) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${UserSession.currentUser?.token}"
                val response = RetrofitClient.sensorApi.deactivateSensor(sensorId, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor desactivado"
                    loadSensors()
                } else {
                    _message.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun blockSensor(sensorId: Int) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${UserSession.currentUser?.token}"
                val response = RetrofitClient.sensorApi.blockSensor(sensorId, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor bloqueado"
                    loadSensors()
                } else {
                    _message.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun markSensorAsLost(sensorId: Int) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${UserSession.currentUser?.token}"
                val response = RetrofitClient.sensorApi.markSensorAsLost(sensorId, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor marcado como perdido"
                    loadSensors()
                } else {
                    _message.value = "Error: ${response.message}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteSensor(sensorId: Int) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${UserSession.currentUser?.token}"
                val response = RetrofitClient.sensorApi.deleteSensor(sensorId, token)

                if (response.isSuccessful) {
                    _message.value = "Sensor eliminado"
                    loadSensors()
                } else {
                    _message.value = response.body()?.mensaje ?: "Error al eliminar"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }
}
```

---

## ✅ **RESUMEN DE ARCHIVOS A CREAR/MODIFICAR**

### Crear Nuevos Archivos:
1. ✅ `Departamento.kt` - Entidad Room
2. ✅ `Sensor.kt` - Entidad Room
3. ✅ `EventoAcceso.kt` - Entidad Room
4. `SensorModels.kt` - Modelos de API
5. `SensorApiService.kt` - Interface Retrofit
6. `SensorManagementScreen.kt` - Pantalla gestión sensores
7. `SensorManagementViewModel.kt` - ViewModel
8. `BarrierControlScreen.kt` - Pantalla control barrera
9. `AccessHistoryScreen.kt` - Pantalla historial

### Modificar Archivos Existentes:
1. `User.kt` - Agregar campos rol, estado, id_departamento
2. `AppDatabase.kt` - Agregar nuevas entidades
3. `RetrofitClient.kt` - Agregar SensorApiService
4. `NavGraph.kt` - Agregar rutas nuevas pantallas
5. `MainMenuScreen.kt` - Agregar botones para nuevas pantallas

---

## 📝 **SIGUIENTES PASOS**

1. Implementar los archivos listados arriba
2. Actualizar `AppDatabase.kt` con las nuevas entidades
3. Crear pantalla "Control de Barrera" con botones Abrir/Cerrar
4. Crear pantalla "Historial de Accesos"
5. Modificar `UserSession` para guardar token JWT
6. Probar integración completa

¿Quieres que continúe creando las pantallas restantes (Control de Barrera e Historial)?
