package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import com.example.ui.components.CustomPopupDialog
import com.example.ui.components.OfflineBlockerView
import com.example.ui.components.TamperLockoutView
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldActive
import com.example.util.AntiTamperGuard
import com.example.util.AppLauncherHelper
import com.example.util.NetworkUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryAppSandboxScreen(
    app: ManagedAppEntity,
    popupConfig: PopupConfigEntity?,
    isDeviceOnline: Boolean,
    isSimulatedTampered: Boolean = false,
    onSimulateTamperToggle: (Boolean) -> Unit = {},
    onBackToAdmin: () -> Unit
) {
    val context = LocalContext.current
    var showPopupDialog by remember { mutableStateOf(false) }
    var hasDismissedPopup by remember { mutableStateOf(false) }

    val themeColor = remember(app.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(app.themeColorHex))
        } catch (e: Exception) {
            ElectricIndigo
        }
    }

    // Check anti-tamper integrity status
    val isTamperLockoutActive = app.isAntiTamperProtected && isSimulatedTampered

    // Determine if offline blocker is active
    val isOfflineBlocked = app.isOfflineBlocked && !isDeviceOnline

    // Trigger popup on entrance if update is required or popup is active
    LaunchedEffect(app.status, popupConfig) {
        if (app.status == "UPDATE_REQUIRED" || (popupConfig != null && popupConfig.isEnabled)) {
            showPopupDialog = true
        }
    }

    if (isTamperLockoutActive) {
        // Render Anti-Tamper Lockout Guard
        TamperLockoutView(
            app = app,
            onResetTamperState = {
                onSimulateTamperToggle(false)
            },
            onBackToAdmin = {
                onSimulateTamperToggle(false)
                onBackToAdmin()
            }
        )
    } else if (isOfflineBlocked) {
        // Render 100% working Offline Blocker view
        OfflineBlockerView(
            app = app,
            onRetry = {
                val onlineNow = NetworkUtils.isOnline(context)
                if (onlineNow) {
                    Toast.makeText(context, "ইন্টারনেট সংযোগ পাওয়া গেছে!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "এখনও ইন্টারনেট সংযোগ নেই। দয়া করে কানেক্ট করুন।", Toast.LENGTH_SHORT).show()
                }
            },
            onBackToAdmin = onBackToAdmin
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(themeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app.secondaryName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = app.secondaryName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ক্লোনকৃত মূল অ্যাপ: ${app.appName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackToAdmin, modifier = Modifier.testTag("sandbox_back_btn")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Quick Admin Bubble Button with original app indicator
                        Surface(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onBackToAdmin() }
                                .testTag("sandbox_admin_control_bubble"),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "এডমিন প্যানেল",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Sandbox Hero Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(
                                listOf(themeColor.copy(alpha = 0.5f), MaterialTheme.colorScheme.outlineVariant)
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(themeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app.secondaryName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = app.secondaryName,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "ক্লোনড মূল অ্যাপ: ${app.appName} (${app.packageName})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Policy Status Badges
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = EmeraldActive.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldActive, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("সেকেন্ডারি সক্রিয়", color = EmeraldActive, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (app.isAntiTamperProtected) {
                                    Surface(
                                        color = EmeraldActive.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldActive, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("এন্টি-টেম্পার সিল্ড", color = EmeraldActive, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (app.isOfflineBlocked) {
                                    Surface(
                                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("অফলাইন শিল্ড অন", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Launch Real Application Button
                            Button(
                                onClick = {
                                    val launched = AppLauncherHelper.launchAppPackage(context, app.packageName)
                                    if (launched) {
                                        Toast.makeText(context, "${app.secondaryName} চালু করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("launch_native_app_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সরাসরি মূল অ্যাপ ওপেন করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Test Trigger Popup Button
                            OutlinedButton(
                                onClick = { showPopupDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("test_trigger_popup_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("পপ-আপ ও বাটন পুনরায় টেস্ট করুন", fontWeight = FontWeight.SemiBold)
                            }

                            if (app.isAntiTamperProtected) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { onSimulateTamperToggle(true) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("test_tamper_violation_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("এন্টি-টেম্পার লকআউট টেস্ট", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Custom Popup Dialog when triggered
            if (showPopupDialog && popupConfig != null) {
                CustomPopupDialog(
                    config = popupConfig,
                    onPrimaryAction = { url ->
                        AppLauncherHelper.openUrl(context, url)
                        if (popupConfig.isDismissible) {
                            showPopupDialog = false
                            hasDismissedPopup = true
                        }
                    },
                    onSecondaryAction = {
                        if (popupConfig.isDismissible) {
                            showPopupDialog = false
                            hasDismissedPopup = true
                        }
                    },
                    onCustomButtonAction = { btn ->
                        AppLauncherHelper.openUrl(context, btn.url)
                    }
                )
            }
        }
    }
}
