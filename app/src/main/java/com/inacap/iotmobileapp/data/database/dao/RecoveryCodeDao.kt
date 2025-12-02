package com.inacap.iotmobileapp.data.database.dao

import androidx.room.*
import com.inacap.iotmobileapp.data.database.entities.RecoveryCode

/**
 * DAO para códigos de recuperación de contraseña
 */
@Dao
interface RecoveryCodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(code: RecoveryCode): Long

    @Query("SELECT * FROM recovery_codes WHERE email = :email AND code = :code AND isUsed = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getValidCode(email: String, code: String): RecoveryCode?

    @Query("UPDATE recovery_codes SET isUsed = 1 WHERE id = :codeId")
    suspend fun markCodeAsUsed(codeId: Long)

    @Query("DELETE FROM recovery_codes WHERE email = :email")
    suspend fun deleteOldCodes(email: String)

    @Query("DELETE FROM recovery_codes WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredCodes(currentTime: Long = System.currentTimeMillis())
}
