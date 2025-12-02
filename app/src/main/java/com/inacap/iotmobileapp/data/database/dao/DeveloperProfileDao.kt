package com.inacap.iotmobileapp.data.database.dao

import androidx.room.*
import com.inacap.iotmobileapp.data.database.entities.DeveloperProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface DeveloperProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: DeveloperProfile): Long

    @Query("SELECT * FROM developer_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: Long): DeveloperProfile?

    @Query("SELECT * FROM developer_profile WHERE userId = :userId LIMIT 1")
    fun getProfileByUserIdFlow(userId: Long): Flow<DeveloperProfile?>

    @Update
    suspend fun updateProfile(profile: DeveloperProfile)

    @Query("DELETE FROM developer_profile WHERE userId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)
}
