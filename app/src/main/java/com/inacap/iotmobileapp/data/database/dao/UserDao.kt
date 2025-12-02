package com.inacap.iotmobileapp.data.database.dao

import androidx.room.*
import com.inacap.iotmobileapp.data.database.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de usuarios
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY apellidos ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET failedLoginAttempts = :attempts WHERE email = :email")
    suspend fun updateFailedAttempts(email: String, attempts: Int)

    @Query("UPDATE users SET isBlocked = :blocked WHERE email = :email")
    suspend fun updateBlockedStatus(email: String, blocked: Boolean)

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String)

    @Query("SELECT * FROM users WHERE nombres LIKE '%' || :query || '%' OR apellidos LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
