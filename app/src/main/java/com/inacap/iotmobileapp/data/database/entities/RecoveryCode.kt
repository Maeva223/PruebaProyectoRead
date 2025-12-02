package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad RecoveryCode para almacenar códigos de recuperación de contraseña
 * Los códigos tienen vigencia de 1 minuto
 */
@Entity(tableName = "recovery_codes")
data class RecoveryCode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val email: String,
    val code: String, // Código de 5 dígitos
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 60000, // 1 minuto de vigencia
    val isUsed: Boolean = false
)
