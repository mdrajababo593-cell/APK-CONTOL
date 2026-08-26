package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appId: Long? = null,
    val appName: String,
    val actionType: String, // CLONED, UPDATE_PUSHED, STATUS_CHANGED, POPUP_CUSTOMIZED, LAUNCHED, OFFLINE_BLOCKED
    val title: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
