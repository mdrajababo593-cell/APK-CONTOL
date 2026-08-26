package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Managed Apps
    @Query("SELECT * FROM managed_apps ORDER BY updatedAt DESC")
    fun getAllManagedApps(): Flow<List<ManagedAppEntity>>

    @Query("SELECT * FROM managed_apps")
    suspend fun getManagedAppsDirect(): List<ManagedAppEntity>

    @Query("SELECT * FROM managed_apps WHERE id = :id LIMIT 1")
    suspend fun getManagedAppById(id: Long): ManagedAppEntity?

    @Query("SELECT * FROM managed_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getManagedAppByPackage(packageName: String): ManagedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManagedApp(app: ManagedAppEntity): Long

    @Update
    suspend fun updateManagedApp(app: ManagedAppEntity)

    @Query("DELETE FROM managed_apps WHERE id = :id")
    suspend fun deleteManagedAppById(id: Long)

    @Query("UPDATE managed_apps SET totalLaunches = totalLaunches + 1, lastLaunchedAt = :now WHERE id = :id")
    suspend fun incrementLaunchCount(id: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE managed_apps SET status = :status, updatedAt = :now WHERE id = :id")
    suspend fun updateAppStatus(id: Long, status: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE managed_apps SET isOfflineBlocked = :isBlocked, updatedAt = :now WHERE id = :id")
    suspend fun updateOfflineBlocked(id: Long, isBlocked: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE managed_apps SET isAntiTamperProtected = :isProtected, isDexIntegrityLocked = :isProtected, updatedAt = :now WHERE id = :id")
    suspend fun updateAntiTamperProtected(id: Long, isProtected: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE managed_apps SET scheduledUpdateTimestamp = :timestamp, updatedAt = :now WHERE id = :id")
    suspend fun updateScheduledTimestamp(id: Long, timestamp: Long, now: Long = System.currentTimeMillis())

    // Popup Configs
    @Query("SELECT * FROM popup_configs WHERE appId = :appId LIMIT 1")
    fun getPopupConfigByAppId(appId: Long): Flow<PopupConfigEntity?>

    @Query("SELECT * FROM popup_configs WHERE appId = :appId LIMIT 1")
    suspend fun getPopupConfigDirect(appId: Long): PopupConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePopupConfig(config: PopupConfigEntity): Long

    @Query("DELETE FROM popup_configs WHERE appId = :appId")
    suspend fun deletePopupConfigByAppId(appId: Long)

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity): Long

    @Query("DELETE FROM activity_logs")
    suspend fun clearAllLogs()
}
