package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldActive
import com.example.util.AntiTamperGuard
import com.example.util.AppLauncherHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAdminDetailScreen(
    app: ManagedAppEntity,
    popupConfig: PopupConfigEntity?,
    onBack: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onToggleOfflineShield: (Boolean) -> Unit,
    onToggleAntiTamper: (Boolean) -> Unit,
    onPushScheduledUpdate: (version: String, url: String, changelog: String, isScheduled: Boolean, scheduledTimestamp: Long, scheduleLabel: String) -> Unit,
    onSavePopupConfig: (PopupConfigEntity) -> Unit,
    onLaunchSecondaryApp: () -> Unit,
    onDeleteApp: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPushUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSecurityCertDialog by remember { mutableStateOf(false) }

    val themeColor = remember(app.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(app.themeColorHex))
        } catch (e: Exception) {
            ElectricIndigo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = app.secondaryName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "অ্যাডমিন কন্ট্রোল প্যানেল",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_detail_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val shareText = """
                                🚀 App: ${app.secondaryName} (${app.packageName})
                                📌 Version: ${app.versionName}
                                🛡️ Anti-Tamper Shield: ${if (app.isAntiTamperProtected) "ACTIVE (SHA-256 Locked)" else "Disabled"}
                                🌐 Offline Shield: ${if (app.isOfflineBlocked) "Active" else "Disabled"}
                                ⚡ Status: ${app.status}
                                🔑 Security Hash: ${app.securityFingerprint}
                                🔗 Update Link: ${popupConfig?.primaryBtnUrl ?: "N/A"}
                            """.trimIndent()
                            AppLauncherHelper.shareConfigText(context, shareText, "App Config: ${app.secondaryName}")
                        },
                        modifier = Modifier.testTag("share_config_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Config")
                    }
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("delete_app_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = themeColor
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("মেইন কন্ট্রোল", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("পপ-আপ ও বাটন", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            if (selectedTab == 0) {
                MainControlTabContent(
                    app = app,
                    popupConfig = popupConfig,
                    themeColor = themeColor,
                    onUpdateStatus = onUpdateStatus,
                    onToggleOfflineShield = onToggleOfflineShield,
                    onToggleAntiTamper = onToggleAntiTamper,
                    onOpenPushUpdateDialog = { showPushUpdateDialog = true },
                    onOpenSecurityCert = { showSecurityCertDialog = true },
                    onLaunchSecondaryApp = onLaunchSecondaryApp,
                    onSwitchToDesigner = { selectedTab = 1 }
                )
            } else {
                val currentConfig = popupConfig ?: PopupConfigEntity(appId = app.id, themeColorHex = app.themeColorHex)
                PopupDesignerScreen(
                    initialConfig = currentConfig,
                    app = app,
                    onSaveConfig = { newConfig ->
                        onSavePopupConfig(newConfig)
                    }
                )
            }
        }
    }

    // Push Update Modal Dialog (with Scheduled timing support)
    if (showPushUpdateDialog) {
        PushUpdateDialog(
            app = app,
            currentConfig = popupConfig,
            onDismiss = { showPushUpdateDialog = false },
            onConfirmPush = { version, url, changelog, isScheduled, scheduledTimestamp, scheduleLabel ->
                onPushScheduledUpdate(version, url, changelog, isScheduled, scheduledTimestamp, scheduleLabel)
                showPushUpdateDialog = false
            }
        )
    }

    // Anti-Tamper Security Certificate Dialog
    if (showSecurityCertDialog) {
        val certText = AntiTamperGuard.generateSecurityCertificateText(
            appName = app.secondaryName,
            packageName = app.packageName,
            version = app.versionName,
            fingerprint = app.securityFingerprint.ifBlank { "APK-GUARD-SHA256-DEFAULT-SECURE" },
            isDexProtected = app.isDexIntegrityLocked
        )
        AlertDialog(
            onDismissRequest = { showSecurityCertDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldActive)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("এন্টি-টেম্পার সিকিউরিটি সার্টিফিকেট", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Surface(
                    color = Color(0xFF140E1E),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = certText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFE5E7EB),
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppLauncherHelper.shareConfigText(context, certText, "Security Certificate: ${app.secondaryName}")
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সার্টিফিকেট শেয়ার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecurityCertDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    // Delete confirmation
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("অ্যাপ ক্লোন মুছে ফেলবেন?") },
            text = { Text("আপনি কি নিশ্চিত যে ‘${app.secondaryName}’ কে অ্যাডমিন কন্ট্রোল থেকে মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteApp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("মুছে ফেলুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun MainControlTabContent(
    app: ManagedAppEntity,
    popupConfig: PopupConfigEntity?,
    themeColor: Color,
    onUpdateStatus: (String) -> Unit,
    onToggleOfflineShield: (Boolean) -> Unit,
    onToggleAntiTamper: (Boolean) -> Unit,
    onOpenPushUpdateDialog: () -> Unit,
    onOpenSecurityCert: () -> Unit,
    onLaunchSecondaryApp: () -> Unit,
    onSwitchToDesigner: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // App Identity Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(themeColor.copy(alpha = 0.5f), MaterialTheme.colorScheme.outlineVariant)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(themeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.secondaryName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.secondaryName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "প্যাকেজ: ${app.packageName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "মূল সংস্করণ: v${app.versionName} • মোট লঞ্চ: ${app.totalLaunches} বার",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Live Push Update Action Card (Hero Section)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (app.status == "UPDATE_REQUIRED") Color(0xFFEF4444).copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (app.status == "UPDATE_REQUIRED") listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                        else listOf(MaterialTheme.colorScheme.primary, CyanAccent)
                    ),
                    width = 1.5.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (app.status == "UPDATE_REQUIRED") Color(0xFFEF4444).copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = if (app.status == "UPDATE_REQUIRED") Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "অনলাইন লাইভ ও শিডিউল্ড আপডেট কন্ট্রোল",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (app.status == "UPDATE_REQUIRED") {
                                    if (app.scheduledUpdateTimestamp > System.currentTimeMillis()) {
                                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(app.scheduledUpdateTimestamp))
                                        "শিডিউল আপডেট সেট করা আছে (লাইভ হবে: $timeStr)"
                                    } else "বর্তমানে পপ-আপ ফোর্স আপডেট সক্রিয় রয়েছে"
                                } else "তাৎক্ষণিক বা নির্দিষ্ট সময়ে অটোমেটিক পুশ শিডিউল করুন",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenPushUpdateDialog,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("push_instant_update_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (app.status == "UPDATE_REQUIRED") Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন আপডেট পুশ / শিডিউল", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (app.status == "UPDATE_REQUIRED") {
                            OutlinedButton(
                                onClick = { onUpdateStatus("ACTIVE") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("cancel_update_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("আপডেট বন্ধ", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Anti-Tamper & Security Shield Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (app.isAntiTamperProtected) EmeraldActive.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (app.isAntiTamperProtected) listOf(EmeraldActive.copy(alpha = 0.5f), EmeraldActive.copy(alpha = 0.2f))
                        else listOf(MaterialTheme.colorScheme.outlineVariant, Color.Transparent)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (app.isAntiTamperProtected) EmeraldActive.copy(alpha = 0.2f)
                                    else Color(0xFF6B7280).copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (app.isAntiTamperProtected) EmeraldActive else Color(0xFF9CA3AF),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "এন্টি-এডিট ও টেম্পার প্রোটেকশন শিল্ড",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (app.isAntiTamperProtected) "সক্রিয়: পাবলিক APK এডিট বা কোড পরিবর্তনের চেষ্টা করলে লক হবে।"
                                else "নিষ্ক্রিয়: কোনো টেম্পার প্রোটেকশন কার্যকর নয়।",
                                fontSize = 12.sp,
                                color = if (app.isAntiTamperProtected) EmeraldActive else MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }

                        Switch(
                            checked = app.isAntiTamperProtected,
                            onCheckedChange = { onToggleAntiTamper(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldActive
                            ),
                            modifier = Modifier.testTag("admin_antitamper_switch")
                        )
                    }

                    if (app.securityFingerprint.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = EmeraldActive, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SHA-256: ${app.securityFingerprint.take(18)}...",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(
                                    onClick = onOpenSecurityCert,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Text("সার্টিফিকেট দেখুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Offline Shield Security Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (app.isOfflineBlocked) Color(0xFFEF4444).copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (app.isOfflineBlocked) listOf(Color(0xFFEF4444).copy(alpha = 0.5f), Color(0xFFEF4444).copy(alpha = 0.2f))
                        else listOf(MaterialTheme.colorScheme.outlineVariant, Color.Transparent)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (app.isOfflineBlocked) Color(0xFFEF4444).copy(alpha = 0.2f)
                                else EmeraldActive.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (app.isOfflineBlocked) Icons.Default.WifiOff else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (app.isOfflineBlocked) Color(0xFFEF4444) else EmeraldActive,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "অফলাইন ব্লকার (Offline Shield)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (app.isOfflineBlocked) "সক্রিয়: ইন্টারনেট না থাকলে ঢুকতে পারবে না।"
                            else "নিষ্ক্রিয়: অফলাইনেও স্বাভাবিক কাজ করবে।",
                            fontSize = 12.sp,
                            color = if (app.isOfflineBlocked) Color(0xFFF87171) else MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = app.isOfflineBlocked,
                        onCheckedChange = { onToggleOfflineShield(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier.testTag("admin_offline_switch")
                    )
                }
            }
        }

        // App Status Selector (4 Modes)
        item {
            Text(
                text = "অ্যাপের স্ট্যাটাস ও মোড সিলেক্টর:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusOptionRow(
                    title = "সক্রিয় মোড (Active)",
                    subtitle = "অ্যাপ স্বাভাবিকভাবে চালু থাকবে এবং ব্যবহার করা যাবে",
                    icon = Icons.Default.CheckCircle,
                    accentColor = EmeraldActive,
                    isSelected = app.status == "ACTIVE",
                    onClick = { onUpdateStatus("ACTIVE") }
                )

                StatusOptionRow(
                    title = "ফোর্স আপডেট মোড (Force Update)",
                    subtitle = "অ্যাপে ঢোকার সাথে সাথে আপডেট পপ-আপ ভেসে উঠবে",
                    icon = Icons.Default.RocketLaunch,
                    accentColor = Color(0xFFEF4444),
                    isSelected = app.status == "UPDATE_REQUIRED",
                    onClick = { onUpdateStatus("UPDATE_REQUIRED") }
                )

                StatusOptionRow(
                    title = "রক্ষণাবেক্ষণ মোড (Maintenance)",
                    subtitle = "রক্ষণাবেক্ষণ বার্তা শো করবে এবং সাময়িক বন্ধ থাকবে",
                    icon = Icons.Default.Build,
                    accentColor = Color(0xFFF59E0B),
                    isSelected = app.status == "MAINTENANCE",
                    onClick = { onUpdateStatus("MAINTENANCE") }
                )

                StatusOptionRow(
                    title = "ব্লক মোড (Blocked)",
                    subtitle = "অ্যাপে প্রবেশ সম্পূর্ণরূপে নিষিদ্ধ করা থাকবে",
                    icon = Icons.Default.Block,
                    accentColor = Color(0xFF6B7280),
                    isSelected = app.status == "BLOCKED",
                    onClick = { onUpdateStatus("BLOCKED") }
                )
            }
        }

        // Secondary App Live Test Runner Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "সেকেন্ডারি অ্যাপ টেস্ট ও রানার",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "বর্তমান পলিসি (অফলাইন চেক, এন্টি-টেম্পার সিল, কাস্টম বাটন ও পপ-আপ) সহ সেকেন্ডারি অ্যাপটি পরীক্ষা করুন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onLaunchSecondaryApp,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("test_run_secondary_app_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সেকেন্ডারি অ্যাপ চালু করুন", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onSwitchToDesigner,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("open_designer_tab_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ColorLens, contentDescription = null, tint = themeColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ডিজাইন কাস্টমাইজ", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isSelected) listOf(accentColor, accentColor.copy(alpha = 0.5f))
                else listOf(MaterialTheme.colorScheme.outlineVariant, Color.Transparent)
            ),
            width = if (isSelected) 1.5.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun PushUpdateDialog(
    app: ManagedAppEntity,
    currentConfig: PopupConfigEntity?,
    onDismiss: () -> Unit,
    onConfirmPush: (version: String, url: String, changelog: String, isScheduled: Boolean, scheduledTimestamp: Long, scheduleLabel: String) -> Unit
) {
    var versionInput by remember { mutableStateOf("2.0.0") }
    var urlInput by remember { mutableStateOf(currentConfig?.primaryBtnUrl ?: "https://play.google.com/store/apps/details?id=${app.packageName}") }
    var changelogInput by remember {
        mutableStateOf("✨ নতুন প্রিমিয়াম আপডেট প্রকাশ করা হয়েছে\n🚀 অ্যাপের গতি ও কার্যক্ষমতা বৃদ্ধি\n🔒 সিকিউরিটি ও টেম্পার গার্ড আপডেট")
    }

    // Scheduling Option
    var selectedTimingMode by remember { mutableStateOf("INSTANT") } // INSTANT, 5MIN, 15MIN, 1HOUR, CUSTOM
    var customDelayMinutes by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(modifier = Modifier.width(8.dp))
                Text("অনলাইন আপডেট পুশ ও শিডিউল", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "অনলাইন টাইমিং সিলেক্ট করুন। নির্ধারিত সময়ে সকল ব্যবহারকারীর ফোনে আপডেট ভেসে উঠবে:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Timing Selector Chips
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTimingMode == "INSTANT",
                        onClick = { selectedTimingMode = "INSTANT" },
                        label = { Text("⚡ এখনই (Instant)", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedTimingMode == "5MIN",
                        onClick = { selectedTimingMode = "5MIN" },
                        label = { Text("⏱️ ৫ মিনিট পর", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedTimingMode == "15MIN",
                        onClick = { selectedTimingMode = "15MIN" },
                        label = { Text("⏱️ ১৫ মিনিট পর", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedTimingMode == "1HOUR",
                        onClick = { selectedTimingMode = "1HOUR" },
                        label = { Text("⏳ ১ ঘণ্টা পর", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedTimingMode == "CUSTOM",
                        onClick = { selectedTimingMode = "CUSTOM" },
                        label = { Text("⚙️ কাস্টম মিনিট", fontSize = 12.sp) }
                    )
                }

                if (selectedTimingMode == "CUSTOM") {
                    OutlinedTextField(
                        value = customDelayMinutes,
                        onValueChange = { customDelayMinutes = it.filter { ch -> ch.isDigit() } },
                        label = { Text("কত মিনিট পর লাইভ হবে?") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = versionInput,
                    onValueChange = { versionInput = it },
                    label = { Text("নতুন সংস্করণ (Version)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("push_version_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("আপডেট ডাউনলোড বা প্লেস্টোর লিঙ্ক") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("push_url_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = changelogInput,
                    onValueChange = { changelogInput = it },
                    label = { Text("নতুন ফিচার / চেঞ্জলগ (প্রতি লাইনে ১টি)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("push_changelog_input"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val now = System.currentTimeMillis()
                    val (isScheduled, timestamp, label) = when (selectedTimingMode) {
                        "5MIN" -> Triple(true, now + 5 * 60 * 1000L, "৫ মিনিট পর")
                        "15MIN" -> Triple(true, now + 15 * 60 * 1000L, "১৫ মিনিট পর")
                        "1HOUR" -> Triple(true, now + 60 * 60 * 1000L, "১ ঘণ্টা পর")
                        "CUSTOM" -> {
                            val mins = customDelayMinutes.toLongOrNull() ?: 10L
                            Triple(true, now + mins * 60 * 1000L, "$mins মিনিট পর")
                        }
                        else -> Triple(false, now, "তাৎক্ষণিক (Instant)")
                    }
                    onConfirmPush(versionInput, urlInput, changelogInput, isScheduled, timestamp, label)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier.testTag("confirm_push_update_btn")
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (selectedTimingMode == "INSTANT") "এখনই পুশ করুন" else "শিডিউল সেট করুন",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
