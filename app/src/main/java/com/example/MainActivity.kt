package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ActivityLogScreen
import com.example.ui.screens.AppAdminDetailScreen
import com.example.ui.screens.AppClonePickerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SecondaryAppSandboxScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppAdminViewModel

enum class Screen {
    DASHBOARD,
    CLONE_PICKER,
    APP_ADMIN_DETAIL,
    SECONDARY_SANDBOX,
    ACTIVITY_LOGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: AppAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val managedApps by viewModel.managedApps.collectAsStateWithLifecycle()
                val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
                val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
                val selectedApp by viewModel.selectedApp.collectAsStateWithLifecycle()
                val selectedPopupConfig by viewModel.selectedPopupConfig.collectAsStateWithLifecycle()
                val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
                val activeSecondaryLaunch by viewModel.activeSecondaryLaunch.collectAsStateWithLifecycle()
                val isTamperSimulated by viewModel.isTamperViolationSimulated.collectAsStateWithLifecycle()
                val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
                val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()

                var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
                val snackbarHostState = remember { SnackbarHostState() }

                // Display toast or snackbar feedback
                LaunchedEffect(feedbackMessage) {
                    feedbackMessage?.let { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearFeedback()
                    }
                }

                // Handle system back navigation
                BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
                    when (currentScreen) {
                        Screen.CLONE_PICKER, Screen.ACTIVITY_LOGS -> currentScreen = Screen.DASHBOARD
                        Screen.APP_ADMIN_DETAIL -> currentScreen = Screen.DASHBOARD
                        Screen.SECONDARY_SANDBOX -> {
                            viewModel.closeSecondaryAppRun()
                            currentScreen = Screen.DASHBOARD
                        }
                        Screen.DASHBOARD -> {}
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition",
                        modifier = Modifier.padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            Screen.DASHBOARD -> {
                                DashboardScreen(
                                    managedApps = managedApps,
                                    isOnline = isOnline,
                                    onNavigateToClonePicker = {
                                        viewModel.scanInstalledApps()
                                        currentScreen = Screen.CLONE_PICKER
                                    },
                                    onSelectAppAdmin = { app ->
                                        viewModel.selectApp(app)
                                        currentScreen = Screen.APP_ADMIN_DETAIL
                                    },
                                    onLaunchSecondaryApp = { app ->
                                        viewModel.startSecondaryAppRun(app)
                                        currentScreen = Screen.SECONDARY_SANDBOX
                                    },
                                    onToggleOfflineShield = { app, isBlocked ->
                                        viewModel.toggleOfflineShield(app.id, app.secondaryName, isBlocked)
                                    },
                                    onNavigateToLogs = {
                                        currentScreen = Screen.ACTIVITY_LOGS
                                    }
                                )
                            }

                            Screen.CLONE_PICKER -> {
                                AppClonePickerScreen(
                                    installedApps = installedApps,
                                    isScanning = isScanning,
                                    onRefreshScan = { viewModel.scanInstalledApps() },
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    onConfirmClone = { appInfo, customName, isOfflineBlocked ->
                                        viewModel.cloneInstalledApp(
                                            installedApp = appInfo,
                                            secondaryCustomName = customName,
                                            isOfflineBlocked = isOfflineBlocked,
                                            isAntiTamperProtected = true
                                        )
                                        currentScreen = Screen.APP_ADMIN_DETAIL
                                    }
                                )
                            }

                            Screen.APP_ADMIN_DETAIL -> {
                                val currentApp = selectedApp
                                if (currentApp != null) {
                                    AppAdminDetailScreen(
                                        app = currentApp,
                                        popupConfig = selectedPopupConfig,
                                        onBack = { currentScreen = Screen.DASHBOARD },
                                        onUpdateStatus = { status ->
                                            viewModel.updateAppStatus(currentApp.id, currentApp.secondaryName, status)
                                        },
                                        onToggleOfflineShield = { isBlocked ->
                                            viewModel.toggleOfflineShield(currentApp.id, currentApp.secondaryName, isBlocked)
                                        },
                                        onToggleAntiTamper = { isProtected ->
                                            viewModel.toggleAntiTamper(currentApp.id, currentApp.secondaryName, isProtected)
                                        },
                                        onPushScheduledUpdate = { version, url, changelog, isScheduled, timestamp, label ->
                                            viewModel.pushScheduledUpdate(
                                                appId = currentApp.id,
                                                appName = currentApp.secondaryName,
                                                version = version,
                                                downloadUrl = url,
                                                changelog = changelog,
                                                isScheduled = isScheduled,
                                                scheduledTimestamp = timestamp,
                                                scheduleLabel = label
                                            )
                                        },
                                        onSavePopupConfig = { config ->
                                            viewModel.savePopupConfig(config, currentApp.secondaryName)
                                        },
                                        onLaunchSecondaryApp = {
                                            viewModel.startSecondaryAppRun(currentApp)
                                            currentScreen = Screen.SECONDARY_SANDBOX
                                        },
                                        onDeleteApp = {
                                            viewModel.deleteManagedApp(currentApp.id, currentApp.secondaryName)
                                            currentScreen = Screen.DASHBOARD
                                        }
                                    )
                                } else {
                                    currentScreen = Screen.DASHBOARD
                                }
                            }

                            Screen.SECONDARY_SANDBOX -> {
                                val launchData = activeSecondaryLaunch
                                if (launchData != null) {
                                    SecondaryAppSandboxScreen(
                                        app = launchData.first,
                                        popupConfig = launchData.second,
                                        isDeviceOnline = isOnline,
                                        isSimulatedTampered = isTamperSimulated,
                                        onSimulateTamperToggle = { viewModel.simulateTamperViolation(it) },
                                        onBackToAdmin = {
                                            viewModel.closeSecondaryAppRun()
                                            currentScreen = Screen.DASHBOARD
                                        }
                                    )
                                } else {
                                    currentScreen = Screen.DASHBOARD
                                }
                            }

                            Screen.ACTIVITY_LOGS -> {
                                ActivityLogScreen(
                                    logs = activityLogs,
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    onClearLogs = { viewModel.clearAllLogs() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
