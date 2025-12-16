package com.inacap.iotmobileapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inacap.iotmobileapp.data.database.dao.DeveloperProfileDao
import com.inacap.iotmobileapp.data.database.dao.RecoveryCodeDao
import com.inacap.iotmobileapp.data.database.dao.UserDao
import com.inacap.iotmobileapp.data.database.entities.DeveloperProfile
import com.inacap.iotmobileapp.data.database.entities.RecoveryCode
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.database.entities.Departamento
import com.inacap.iotmobileapp.data.database.entities.Sensor
import com.inacap.iotmobileapp.data.database.entities.EventoAcceso

/**
 * Base de datos principal de la aplicación
 * Contiene las tablas de usuarios, códigos de recuperación, perfiles de desarrollador,
 * y las nuevas tablas para el sistema de control de acceso RFID (Evaluación III)
 */
@Database(
    entities = [
        User::class,
        RecoveryCode::class,
        DeveloperProfile::class,
        Departamento::class,
        Sensor::class,
        EventoAcceso::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun recoveryCodeDao(): RecoveryCodeDao
    abstract fun developerProfileDao(): DeveloperProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "iot_mobile_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
