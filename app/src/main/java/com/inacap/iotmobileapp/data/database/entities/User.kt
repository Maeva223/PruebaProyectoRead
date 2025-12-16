package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad User para Room Database
 * Representa un usuario en la base de datos local
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombres: String,
    val apellidos: String,
    val email: String,
    val password: String,

    // Campos adicionales para control de sesión
    val createdAt: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false,
    val failedLoginAttempts: Int = 0,

    // NUEVOS CAMPOS PARA EVALUACIÓN III - Sistema de Control de Acceso
    val rol: String = "OPERADOR", // "ADMIN" o "OPERADOR"
    val estado: String = "ACTIVO", // "ACTIVO", "INACTIVO", "BLOQUEADO"
    val id_departamento: Int? = null, // FK a departamentos
    val token: String? = null // JWT token del backend
)
