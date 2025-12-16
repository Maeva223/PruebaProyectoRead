package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "eventos_acceso",
    foreignKeys = [
        ForeignKey(
            entity = Sensor::class,
            parentColumns = ["id_sensor"],
            childColumns = ["id_sensor"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Departamento::class,
            parentColumns = ["id_departamento"],
            childColumns = ["id_departamento"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class EventoAcceso(
    @PrimaryKey(autoGenerate = true)
    val id_evento: Int = 0,
    val id_sensor: Int? = null,
    val id_usuario: Int? = null,
    val id_departamento: Int? = null,
    val tipo_evento: String, // ACCESO_VALIDO, ACCESO_RECHAZADO, APERTURA_MANUAL, etc.
    val resultado: String, // PERMITIDO, DENEGADO
    val mac_sensor: String? = null,
    val detalles: String? = null,
    val fecha_hora: Long = System.currentTimeMillis()
)
