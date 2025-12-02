package com.inacap.iotmobileapp.data.repository

import com.inacap.iotmobileapp.data.database.dao.RecoveryCodeDao
import com.inacap.iotmobileapp.data.database.dao.UserDao
import com.inacap.iotmobileapp.data.database.entities.RecoveryCode
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar operaciones de usuarios
 */
class UserRepository(
    private val userDao: UserDao,
    private val recoveryCodeDao: RecoveryCodeDao
) {

    // Observar todos los usuarios
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    // Buscar usuarios
    fun searchUsers(query: String): Flow<List<User>> = userDao.searchUsers(query)

    // Obtener usuario por email
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    // Login
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    // Registrar nuevo usuario
    suspend fun registerUser(user: User): Result<Long> {
        return try {
            // Verificar si el email ya existe
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("Email ya registrado"))
            } else {
                val userId = userDao.insertUser(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar usuario
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar usuario
    suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            userDao.deleteUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar contraseña
    suspend fun updatePassword(email: String, newPassword: String): Result<Unit> {
        return try {
            userDao.updatePassword(email, newPassword)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Generar código de recuperación
    suspend fun generateRecoveryCode(email: String): Result<String> {
        return try {
            // Verificar que el usuario existe
            val user = userDao.getUserByEmail(email)
                ?: return Result.failure(Exception("Email no registrado"))

            // Eliminar códigos anteriores
            recoveryCodeDao.deleteOldCodes(email)

            // Generar nuevo código
            val code = Validators.generateRecoveryCode()
            val recoveryCode = RecoveryCode(
                email = email,
                code = code
            )

            recoveryCodeDao.insertCode(recoveryCode)
            Result.success(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verificar código de recuperación
    suspend fun verifyRecoveryCode(email: String, code: String): Result<RecoveryCode> {
        return try {
            val recoveryCode = recoveryCodeDao.getValidCode(email, code)
                ?: return Result.failure(Exception("Código incorrecto"))

            // Verificar si el código está vencido
            if (System.currentTimeMillis() > recoveryCode.expiresAt) {
                return Result.failure(Exception("Código vencido"))
            }

            Result.success(recoveryCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Marcar código como usado
    suspend fun markCodeAsUsed(codeId: Long) {
        recoveryCodeDao.markCodeAsUsed(codeId)
    }

    // Actualizar intentos fallidos de login
    suspend fun updateFailedAttempts(email: String, attempts: Int) {
        userDao.updateFailedAttempts(email, attempts)
    }

    // Bloquear/Desbloquear usuario
    suspend fun updateBlockedStatus(email: String, blocked: Boolean) {
        userDao.updateBlockedStatus(email, blocked)
    }

    // Limpiar códigos expirados
    suspend fun cleanExpiredCodes() {
        recoveryCodeDao.deleteExpiredCodes()
    }
}
