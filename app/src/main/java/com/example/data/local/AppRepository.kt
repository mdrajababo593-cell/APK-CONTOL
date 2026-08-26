package com.example.data.local

import com.example.data.model.ActivityLogEntity
import com.example.data.model.CustomPopupButton
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import com.example.util.AntiTamperGuard
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allManagedApps: Flow<List<ManagedAppEntity>> = appDao.getAllManagedApps()
    val allActivityLogs: Flow<List<ActivityLogEntity>> = appDao.getAllActivityLogs()

    suspend fun getAppById(id: Long): ManagedAppEntity? = appDao.getManagedAppById(id)

    suspend fun getAppByPackage(packageName: String): ManagedAppEntity? = appDao.getManagedAppByPackage(packageName)

    suspend fun createOrUpdateApp(app: ManagedAppEntity): Long {
        val id = if (app.id == 0L) {
            val fingerprint = if (app.securityFingerprint.isBlank()) {
                AntiTamperGuard.generateSecurityFingerprint(app.packageName, app.versionCode)
            } else app.securityFingerprint

            val entityToSave = app.copy(securityFingerprint = fingerprint)
            val newId = appDao.insertManagedApp(entityToSave)

            // Create default popup config with WhatsApp / Telegram buttons enabled/ready
            val defaultButtons = CustomPopupButton.defaultButtons()
            val defaultConfig = PopupConfigEntity(
                appId = newId,
                title = "নতুন সংস্করণ আপডেট!",
                message = "${app.secondaryName} এর নতুন সংস্করণ উপলব্ধ। আরও দ্রুত ও নিরাপদ অভিজ্ঞতা পেতে এখনই আপডেট করুন।",
                primaryBtnText = "এখনই আপডেট করুন",
                primaryBtnUrl = "https://play.google.com/store/apps/details?id=${app.packageName}",
                isDismissible = false,
                themeColorHex = app.themeColorHex,
                extraButtonsJson = CustomPopupButton.listToJsonString(defaultButtons)
            )
            appDao.insertOrUpdatePopupConfig(defaultConfig)
            appDao.insertActivityLog(
                ActivityLogEntity(
                    appId = newId,
                    appName = app.secondaryName,
                    actionType = "CLONED",
                    title = "সেকেন্ডারি অ্যাপ তৈরি সম্পন্ন",
                    details = "${app.appName} (${app.packageName}) কে সেকেন্ডারি ইনস্ট্যান্স হিসেবে কনফিগার করা হয়েছে। এন্টি-টেম্পার সিল: $fingerprint"
                )
            )
            newId
        } else {
            appDao.updateManagedApp(app)
            appDao.insertActivityLog(
                ActivityLogEntity(
                    appId = app.id,
                    appName = app.secondaryName,
                    actionType = "UPDATED",
                    title = "অ্যাপ সেটিংস আপডেট",
                    details = "অ্যাপের কন্ট্রোল পলিসি ও সেটিংস পরিমার্জন করা হয়েছে।"
                )
            )
            app.id
        }
        return id
    }

    suspend fun deleteApp(id: Long, appName: String) {
        appDao.deleteManagedAppById(id)
        appDao.deletePopupConfigByAppId(id)
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = null,
                appName = appName,
                actionType = "DELETED",
                title = "ম্যানেজড অ্যাপ মুছে ফেলা হয়েছে",
                details = "$appName কে ম্যানেজড তালিকা থেকে অপসারণ করা হয়েছে।"
            )
        )
    }

    suspend fun updateAppStatus(id: Long, appName: String, status: String) {
        appDao.updateAppStatus(id, status)
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = id,
                appName = appName,
                actionType = "STATUS_CHANGED",
                title = "স্ট্যাটাস পরিবর্তন: $status",
                details = "অ্যাপের অ্যাডমিন স্ট্যাটাস পরিবর্তন করে '$status' নির্ধারণ করা হয়েছে।"
            )
        )
    }

    suspend fun updateOfflineBlocked(id: Long, appName: String, isBlocked: Boolean) {
        appDao.updateOfflineBlocked(id, isBlocked)
        val statusText = if (isBlocked) "চালু (ইন্টারনেট ছাড়া ব্লক)" else "বন্ধ (অফলাইনে ঢুকতে পারবে)"
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = id,
                appName = appName,
                actionType = "OFFLINE_GUARD",
                title = "অফলাইন শিল্ড $statusText",
                details = "অফলাইন ব্লকিং পলিসি আপডেট করা হয়েছে।"
            )
        )
    }

    suspend fun updateAntiTamperProtected(id: Long, appName: String, isProtected: Boolean) {
        appDao.updateAntiTamperProtected(id, isProtected)
        val statusText = if (isProtected) "সক্রিয় (এন্টি-এডিট ও DEX লক)" else "নিষ্ক্রিয়"
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = id,
                appName = appName,
                actionType = "SECURITY_GUARD",
                title = "এন্টি-টেম্পার সিকিউরিটি $statusText",
                details = "পাবলিক APK ফাইল এডিট ও রিভার্স ইঞ্জিনিয়ারিং প্রটেকশন পলিসি আপডেট করা হয়েছে।"
            )
        )
    }

    suspend fun recordLaunch(id: Long, appName: String) {
        appDao.incrementLaunchCount(id)
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = id,
                appName = appName,
                actionType = "LAUNCHED",
                title = "সেকেন্ডারি অ্যাপ লঞ্চ",
                details = "$appName সফলভাবে ওপেন করা হয়েছে।"
            )
        )
    }

    fun getPopupConfig(appId: Long): Flow<PopupConfigEntity?> = appDao.getPopupConfigByAppId(appId)

    suspend fun getPopupConfigDirect(appId: Long): PopupConfigEntity? = appDao.getPopupConfigDirect(appId)

    suspend fun savePopupConfig(config: PopupConfigEntity, appName: String) {
        appDao.insertOrUpdatePopupConfig(config)
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = config.appId,
                appName = appName,
                actionType = "POPUP_CUSTOMIZED",
                title = "পপ-আপ ডিজাইন ও পলিসি সংরক্ষিত",
                details = "শিরোনাম: '${config.title}' | টাইপ: ${config.popupType} | বাটন ও শিডিউল পলিসি সংরক্ষিত"
            )
        )
    }

    suspend fun pushInstantUpdate(
        appId: Long,
        appName: String,
        version: String,
        downloadUrl: String,
        changelog: String
    ) {
        pushUpdateWithSchedule(
            appId = appId,
            appName = appName,
            version = version,
            downloadUrl = downloadUrl,
            changelog = changelog,
            isScheduled = false,
            scheduledTimestamp = System.currentTimeMillis(),
            scheduleLabel = "তাৎক্ষণিক (Instant)"
        )
    }

    suspend fun pushUpdateWithSchedule(
        appId: Long,
        appName: String,
        version: String,
        downloadUrl: String,
        changelog: String,
        isScheduled: Boolean,
        scheduledTimestamp: Long,
        scheduleLabel: String
    ) {
        appDao.updateAppStatus(appId, "UPDATE_REQUIRED")
        appDao.updateScheduledTimestamp(appId, if (isScheduled) scheduledTimestamp else 0L)
        val currentConfig = appDao.getPopupConfigDirect(appId)
        val newConfig = (currentConfig ?: PopupConfigEntity(appId = appId)).copy(
            isEnabled = true,
            popupType = "FORCE_UPDATE",
            title = "জরুরী নতুন আপডেট v$version!",
            message = "অ্যাপটির উন্নত কার্যক্ষমতার জন্য নতুন সংস্করণ $version প্রকাশ করা হয়েছে। অব্যাহত রাখতে এখনই আপডেট করুন।",
            primaryBtnText = "এখনই আপডেট ডাউনলোড করুন",
            primaryBtnUrl = downloadUrl,
            isDismissible = false,
            showChangelogList = changelog.isNotBlank(),
            changelogItems = changelog.ifBlank { "🚀 নতুন আপডেট প্রকাশ\n⚡ বাগ ফিক্স এবং পারফরম্যান্স উন্নয়ন\n🛡️ সিকিউরিটি ও স্ট্যাবিলিটি আপডেট" },
            minRequiredVersion = version,
            isScheduled = isScheduled,
            scheduledTimestamp = scheduledTimestamp,
            scheduleLabel = scheduleLabel,
            updatedAt = System.currentTimeMillis()
        )
        appDao.insertOrUpdatePopupConfig(newConfig)
        val timingText = if (isScheduled) "শিডিউল্ড রিলিজ ($scheduleLabel)" else "তাৎক্ষণিক লাইভ"
        appDao.insertActivityLog(
            ActivityLogEntity(
                appId = appId,
                appName = appName,
                actionType = "UPDATE_PUSHED",
                title = "অনলাইন আপডেট পুশ করা হয়েছে (v$version)",
                details = "টাইমিং: $timingText | সকল পাবলিক ইউজারদের স্ক্রিনে আপডেট সক্রিয় হবে।"
            )
        )
    }

    suspend fun seedInitialAppsIfEmpty() {
        val apps = appDao.getManagedAppsDirect()
        if (apps.isEmpty()) {
            val sample1 = ManagedAppEntity(
                packageName = "com.whatsapp",
                appName = "WhatsApp Messenger",
                secondaryName = "WhatsApp Pro (Clone)",
                versionName = "2.24.18",
                versionCode = 2241801,
                status = "ACTIVE",
                isOfflineBlocked = false,
                isAntiTamperProtected = true,
                themeColorHex = "#25D366",
                appCategory = "Communication",
                apkSizeFormatted = "28.4 MB"
            )
            val sample2 = ManagedAppEntity(
                packageName = "org.telegram.messenger",
                appName = "Telegram",
                secondaryName = "Telegram VIP (Clone)",
                versionName = "10.14.5",
                versionCode = 1014500,
                status = "UPDATE_REQUIRED",
                isOfflineBlocked = true,
                isAntiTamperProtected = true,
                themeColorHex = "#0088CC",
                appCategory = "Social",
                apkSizeFormatted = "32.1 MB"
            )
            createOrUpdateApp(sample1)
            createOrUpdateApp(sample2)
        }
    }

    suspend fun clearLogs() {
        appDao.clearAllLogs()
    }
}
