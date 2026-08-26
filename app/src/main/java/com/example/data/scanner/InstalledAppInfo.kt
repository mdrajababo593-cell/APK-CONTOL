package com.example.data.scanner

import android.graphics.drawable.Drawable

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Int,
    val isSystemApp: Boolean,
    val icon: Drawable? = null,
    val category: String = "App",
    val primaryColorHex: String = "#6366F1",
    val sourceApkPath: String? = null,
    val apkSizeFormatted: String = "15.4 MB"
)
