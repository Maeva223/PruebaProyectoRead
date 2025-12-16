package com.inacap.iotmobileapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departamentos")
data class Departamento(
    @PrimaryKey(autoGenerate = true)
    val id_departamento: Int = 0,
    val numero: String,
    val torre: String?,
    val condominio: String = "Condominio Principal",
    val piso: Int? = null
)
