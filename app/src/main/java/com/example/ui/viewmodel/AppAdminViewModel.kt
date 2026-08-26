package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppRepository
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import com.example.data.scanner.InstalledAppInfo
import com.example.data.scanner.InstalledAppScanner
import com.example.util.AntiTamperGuard
import com.example.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppAdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val managedApps: StateFlow<List<ManagedAppEntity>>
    val activityLogs: StateFlow<List<ActivityLogEntity>>

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _selectedApp = MutableStateFlow<ManagedAppEntity?>(null)
    val selectedApp: StateFlow<ManagedAppEntity?> = _selectedApp.asStateFlow()

    private val _selectedPopupConfig = MutableStateFlow<PopupConfigEntity?>(null)
    val selectedPopupConfig: StateFlow<PopupConfigEntity?> = _selectedPopupConfig.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Active Secondary App Running Context (for testing / simulated launch)
    private val _activeSecondaryLaunch = MutableStateFlow<Pair<ManagedAppEntity, PopupConfigEntity?>?>(null)
    val activeSecondaryLaunch: StateFlow<Pair<ManagedAppEntity, PopupConfigEntity?>?> = _activeSecondaryLaunch.asStateFlow()

    // Cloning & APK Export Progress State
    private val _isCloningInProgress = MutableStateFlow(false)
    val isCloningInProgress: StateFlow<Boolean> = _isCloningInProgress.asStateFlow()

    private val _lastExportResult = MutableStateFlow<com.example.util.ApkClonerExtractorHelper.ExportResult?>(null)
    val lastExportResult: StateFlow<com.example.util.ApkClonerExtractorHelper.ExportResult?> = _lastExportResult.asStateFlow()

    // Simulated Tamper Flag for testing Anti-Tamper Lockdown in Sandbox
    private val _isTamperViolationSimulated = MutableStateFlow(false)
    val isTamperViolationSimulated: StateFlow<Boolean> = _isTamperViolationSimulated.asStateFlow()

    // Banner message or feedback toast state
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = AppRepository(db.appDao())

        managedApps = repository.allManagedApps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activityLogs = repository.allActivityLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Observe network connectivity
        viewModelScope.launch {
            NetworkUtils.observeNetworkState(application).collect { online ->
                _isOnline.value = online
            }
        }

        // Initial scan of installed apps and seed if first launch
        viewModelScope.launch {
            repository.seedInitialAppsIfEmpty()
        }
        scanInstalledApps()
    }

    fun scanInstalledApps() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                val apps = InstalledAppScanner.getInstalledApps(getApplication())
                _installedApps.value = apps
            } catch (e: Exception) {
                _installedApps.value = emptyList()
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun cloneInstalledApp(
        installedApp: InstalledAppInfo,
        secondaryCustomName: String,
        isOfflineBlocked: Boolean,
        isAntiTamperProtected: Boolean = true,
        initialStatus: String = "ACTIVE"
    ) {
        viewModelScope.launch {
            _isCloningInProgress.value = true
            try {
                val customName = secondaryCustomName.ifBlank { "${installedApp.appName} (Clone)" }
                
                // 1. Perform actual APK clone extraction & save directly to Downloads
                val exportResult = com.example.util.ApkClonerExtractorHelper.cloneAndExportApk(
                    context = getApplication(),
                    appInfo = installedApp,
                    customName = customName
                )
                _lastExportResult.value = exportResult

                val fingerprint = AntiTamperGuard.generateSecurityFingerprint(
                    installedApp.packageName,
                    installedApp.versionCode
                )
                val entity = ManagedAppEntity(
                    packageName = installedApp.packageName,
                    appName = installedApp.appName,
                    secondaryName = customName,
                    versionName = installedApp.versionName,
                    versionCode = installedApp.versionCode,
                    status = initialStatus,
                    isOfflineBlocked = isOfflineBlocked,
                    isAntiTamperProtected = isAntiTamperProtected,
                    securityFingerprint = fingerprint,
                    isDexIntegrityLocked = isAntiTamperProtected,
                    themeColorHex = installedApp.primaryColorHex,
                    appCategory = installedApp.category,
                    sourceApkPath = installedApp.sourceApkPath,
                    extractedApkPath = exportResult.filePath,
                    apkSizeFormatted = exportResult.fileSizeFormatted.ifBlank { installedApp.apkSizeFormatted }
                )
                val newId = repository.createOrUpdateApp(entity)
                _feedbackMessage.value = exportResult.message
                val created = repository.getAppById(newId)
                _selectedApp.value = created
                if (created != null) {
                    loadPopupConfigForApp(created.id)
                }
            } catch (e: Exception) {
                _feedbackMessage.value = "ক্লোন করতে সমস্যা হয়েছে: ${e.message}"
            } finally {
                _isCloningInProgress.value = false
            }
        }
    }

    fun exportAndDownloadApk(app: ManagedAppEntity) {
        viewModelScope.launch {
            _isCloningInProgress.value = true
            try {
                val installedInfo = InstalledAppInfo(
                    packageName = app.packageName,
                    appName = app.appName,
                    versionName = app.versionName,
                    versionCode = app.versionCode,
                    isSystemApp = false,
                    sourceApkPath = app.sourceApkPath,
                    primaryColorHex = app.themeColorHex
                )
                val exportResult = com.example.util.ApkClonerExtractorHelper.cloneAndExportApk(
                    context = getApplication(),
                    appInfo = installedInfo,
                    customName = app.secondaryName
                )
                _lastExportResult.value = exportResult
                _feedbackMessage.value = exportResult.message
            } catch (e: Exception) {
                _feedbackMessage.value = "APK এক্সপোর্ট ও ডাউনলোড করতে সমস্যা হয়েছে: ${e.message}"
            } finally {
                _isCloningInProgress.value = false
            }
        }
    }

    fun clearExportResult() {
        _lastExportResult.value = null
    }

    fun selectApp(app: ManagedAppEntity) {
        _selectedApp.value = app
        loadPopupConfigForApp(app.id)
    }

    fun loadPopupConfigForApp(appId: Long) {
        viewModelScope.launch {
            val config = repository.getPopupConfigDirect(appId)
            _selectedPopupConfig.value = config
        }
    }

    fun updateAppStatus(appId: Long, appName: String, status: String) {
        viewModelScope.launch {
            repository.updateAppStatus(appId, appName, status)
            _selectedApp.value?.let { current ->
                if (current.id == appId) {
                    _selectedApp.value = current.copy(status = status)
                }
            }
            _feedbackMessage.value = "স্ট্যাটাস পরিবর্তন করা হয়েছে: $status"
        }
    }

    fun toggleOfflineShield(appId: Long, appName: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.updateOfflineBlocked(appId, appName, isBlocked)
            _selectedApp.value?.let { current ->
                if (current.id == appId) {
                    _selectedApp.value = current.copy(isOfflineBlocked = isBlocked)
                }
            }
            _feedbackMessage.value = if (isBlocked) "অফলাইন ব্লক সক্রিয় (ইন্টারনেট ছাড়া ঢুকতে পারবে না)" else "অফলাইন ব্লক নিষ্ক্রিয়"
        }
    }

    fun toggleAntiTamper(appId: Long, appName: String, isProtected: Boolean) {
        viewModelScope.launch {
            repository.updateAntiTamperProtected(appId, appName, isProtected)
            _selectedApp.value?.let { current ->
                if (current.id == appId) {
                    _selectedApp.value = current.copy(
                        isAntiTamperProtected = isProtected,
                        isDexIntegrityLocked = isProtected
                    )
                }
            }
            _feedbackMessage.value = if (isProtected) "এন্টি-এডিট ও টেম্পার প্রোটেকশন সক্রিয় করা হয়েছে!" else "এন্টি-টেম্পার প্রোটেকশন নিষ্ক্রিয় করা হয়েছে"
        }
    }

    fun savePopupConfig(config: PopupConfigEntity, appName: String) {
        viewModelScope.launch {
            repository.savePopupConfig(config, appName)
            _selectedPopupConfig.value = config
            _feedbackMessage.value = "পপ-আপ ডিজাইন, বাটন ও শিডিউল পলিসি সফলভাবে সেভ হয়েছে!"
        }
    }

    fun pushInstantUpdate(
        appId: Long,
        appName: String,
        version: String,
        downloadUrl: String,
        changelog: String
    ) {
        viewModelScope.launch {
            repository.pushInstantUpdate(appId, appName, version, downloadUrl, changelog)
            _selectedApp.value?.let { current ->
                if (current.id == appId) {
                    _selectedApp.value = current.copy(status = "UPDATE_REQUIRED")
                }
            }
            loadPopupConfigForApp(appId)
            _feedbackMessage.value = "লাইভ আপডেট সফলভাবে পুশ করা হয়েছে!"
        }
    }

    fun pushScheduledUpdate(
        appId: Long,
        appName: String,
        version: String,
        downloadUrl: String,
        changelog: String,
        isScheduled: Boolean,
        scheduledTimestamp: Long,
        scheduleLabel: String
    ) {
        viewModelScope.launch {
            repository.pushUpdateWithSchedule(
                appId = appId,
                appName = appName,
                version = version,
                downloadUrl = downloadUrl,
                changelog = changelog,
                isScheduled = isScheduled,
                scheduledTimestamp = scheduledTimestamp,
                scheduleLabel = scheduleLabel
            )
            _selectedApp.value?.let { current ->
                if (current.id == appId) {
                    _selectedApp.value = current.copy(
                        status = "UPDATE_REQUIRED",
                        scheduledUpdateTimestamp = if (isScheduled) scheduledTimestamp else 0L
                    )
                }
            }
            loadPopupConfigForApp(appId)
            _feedbackMessage.value = if (isScheduled) "অনলাইন আপডেট শিডিউল করা হয়েছে ($scheduleLabel)!" else "লাইভ আপডেট তাৎক্ষণিক পুশ করা হয়েছে!"
        }
    }

    fun deleteManagedApp(appId: Long, appName: String) {
        viewModelScope.launch {
            repository.deleteApp(appId, appName)
            if (_selectedApp.value?.id == appId) {
                _selectedApp.value = null
                _selectedPopupConfig.value = null
            }
            _feedbackMessage.value = "অ্যাপটি মুছে ফেলা হয়েছে।"
        }
    }

    fun startSecondaryAppRun(app: ManagedAppEntity) {
        viewModelScope.launch {
            _isTamperViolationSimulated.value = false
            repository.recordLaunch(app.id, app.secondaryName)
            val config = repository.getPopupConfigDirect(app.id)
            _activeSecondaryLaunch.value = Pair(app, config)
        }
    }

    fun simulateTamperViolation(isViolated: Boolean) {
        _isTamperViolationSimulated.value = isViolated
    }

    fun closeSecondaryAppRun() {
        _activeSecondaryLaunch.value = null
        _isTamperViolationSimulated.value = false
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            _feedbackMessage.value = "সকল অ্যাক্টিভিটি লগ মুছে ফেলা হয়েছে।"
        }
    }
}
