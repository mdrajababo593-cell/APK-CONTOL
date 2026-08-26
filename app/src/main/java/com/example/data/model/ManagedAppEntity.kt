package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "managed_apps")
data class ManagedAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val secondaryName: String,
    val versionName: String,
    val versionCode: Int,
    val status: String = "ACTIVE", // ACTIVE, UPDATE_REQUIRED, MAINTENANCE, BLOCKED
    val isOfflineBlocked: Boolean = false, // If true: cannot enter when offline
    val isAntiTamperProtected: Boolean = true, // Anti-edit, anti-decompile & integrity seal
    val securityFingerprint: String = "", // SHA-256 signature hash
    val isDexIntegrityLocked: Boolean = true, // Block execution if DEX/assets are altered
    val pinProtection: String? = null, // Optional 4-digit PIN lock
    val themeColorHex: String = "#6366F1",
    val appCategory: String = "Productivity",
    val totalLaunches: Int = 0,
    val lastLaunchedAt: Long = 0L,
    val scheduledUpdateTimestamp: Long = 0L, // If scheduled update is active
    val sourceApkPath: String? = null,
    val extractedApkPath: String? = null,
    val apkSizeFormatted: String = "15.4 MB",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val customNotes: String = ""
)
