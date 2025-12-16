package com.inacap.iotmobileapp.data.api.models

/**
 * Modelos de datos para las APIs de sensores RFID y control de acceso
 * Evaluación Sumativa III - Sistema de Control de Acceso IoT
 */

// Respuesta de lista de sensores
data class SensorResponse(
    val sensores: List<SensorDTO>
)

// DTO de sensor individual
data class SensorDTO(
    val id_sensor: Int,
    val codigo_sensor: String,
    val estado: String,
    val tipo: String,
    val id_departamento: Int,
    val alias: String?,
    val fecha_alta: String
)

// Request para registrar un nuevo sensor
data class RegisterSensorRequest(
    val codigo_sensor: String,
    val tipo: String,
    val alias: String?
)

// Respuesta genérica de operaciones con sensores
data class RegisterSensorResponse(
    val success: Boolean,
    val mensaje: String,
    val sensor_id: Int? = null
)

// Respuesta de validación de acceso RFID
data class AccessValidationResponse(
    val acceso_permitido: Boolean,
    val mensaje: String,
    val sensor: SensorInfo?
)

// Información del sensor en respuesta de validación
data class SensorInfo(
    val id: Int,
    val tipo: String,
    val alias: String?,
    val departamento: String
)

// Respuesta de historial de eventos
data class EventoAccesoResponse(
    val eventos: List<EventoAccesoDTO>,
    val total: Int
)

// DTO de evento de acceso individual
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

// Respuesta de control manual de barrera
data class ManualControlResponse(
    val success: Boolean,
    val mensaje: String,
    val usuario: String?
)
