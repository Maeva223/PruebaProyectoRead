package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "developer_profile")
data class DeveloperProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,                   // ID del usuario logeado
    val fullName: String,               // Nombre completo
    val role: String,                   // Rol: Full Stack, Frontend, etc.
    val email: String,                  // Email institucional
    val institution: String,            // INACAP La Serena
    val career: String,                 // Ingeniería en Informática
    val section: String,                // Sección
    val github: String = "",            // GitHub (opcional)
    val linkedin: String = "",          // LinkedIn (opcional)
    val portfolio: String = "",         // Portafolio (opcional)
    val avatarEmoji: String = "👨‍💻"    // Emoji de avatar
)
