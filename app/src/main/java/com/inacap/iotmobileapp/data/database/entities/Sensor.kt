package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sensores",
    foreignKeys = [
        ForeignKey(
            entity = Departamento::class,
            parentColumns = ["id_departamento"],
            childColumns = ["id_departamento"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Sensor(
    @PrimaryKey(autoGenerate = true)
    val id_sensor: Int = 0,
    val codigo_sensor: String, // MAC del RFID
    val estado: String = "ACTIVO", // ACTIVO, INACTIVO, PERDIDO, BLOQUEADO
    val tipo: String = "Tarjeta", // Tarjeta, Llavero
    val id_departamento: Int,
    val alias: String? = null,
    val fecha_alta: Long = System.currentTimeMillis(),
    val fecha_baja: Long? = null
)
