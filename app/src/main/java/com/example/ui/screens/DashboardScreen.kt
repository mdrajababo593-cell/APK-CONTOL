package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedAppEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldActive

@Composable
fun DashboardScreen(
    managedApps: List<ManagedAppEntity>,
    isOnline: Boolean,
    onNavigateToClonePicker: () -> Unit,
    onSelectAppAdmin: (ManagedAppEntity) -> Unit,
    onLaunchSecondaryApp: (ManagedAppEntity) -> Unit,
    onToggleOfflineShield: (ManagedAppEntity, Boolean) -> Unit,
    onNavigateToLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredApps = remember(managedApps, selectedFilter) {
        when (selectedFilter) {
            "UPDATE_REQUIRED" -> managedApps.filter { it.status == "UPDATE_REQUIRED" }
            "OFFLINE_BLOCKED" -> managedApps.filter { it.isOfflineBlocked }
            "ACTIVE" -> managedApps.filter { it.status == "ACTIVE" }
            else -> managedApps
        }
    }

    val activeUpdatesCount = managedApps.count { it.status == "UPDATE_REQUIRED" }
    val offlineBlockedCount = managedApps.count { it.isOfflineBlocked }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title & Network Status
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "অ্যাডমিন কন্ট্রোল প্যানেল",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "সেকেন্ডারি অ্যাপ ক্লোন ও লাইভ রিমোট কন্ট্রোলার",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Logs Action Button
                    IconButton(
                        onClick = onNavigateToLogs,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .testTag("activity_logs_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Activity Logs",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Overview Metric Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "মোট ক্লোন অ্যাপ",
                        value = "${managedApps.size}",
                        icon = Icons.Default.ContentCopy,
                        accentColor = ElectricIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "লাইভ আপডেট",
                        value = "$activeUpdatesCount",
                        icon = Icons.Default.SystemUpdate,
                        accentColor = if (activeUpdatesCount > 0) Color(0xFFEF4444) else CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "অফলাইন শিল্ড",
                        value = "$offlineBlockedCount",
                        icon = Icons.Default.Shield,
                        accentColor = if (offlineBlockedCount > 0) Color(0xFFF59E0B) else EmeraldActive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Network Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isOnline) EmeraldActive.copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.12f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isOnline) listOf(EmeraldActive.copy(alpha = 0.4f), EmeraldActive.copy(alpha = 0.1f))
                            else listOf(Color(0xFFEF4444).copy(alpha = 0.5f), Color(0xFFEF4444).copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isOnline) EmeraldActive else Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isOnline) "ডিভাইস ইন্টারনেট সক্রিয়" else "ইন্টারনেট সংযোগ নেই (Offline)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isOnline) EmeraldActive else Color(0xFFF87171)
                            )
                            Text(
                                text = if (isOnline) "রিমোট আপডেট ও অফলাইন সুরক্ষা প্রস্তুত" else "অফলাইন শিল্ড কার্যকর থাকবে",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            label = { Text("সকল অ্যাপ (${managedApps.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == "UPDATE_REQUIRED",
                            onClick = { selectedFilter = "UPDATE_REQUIRED" },
                            label = { Text("আপডেট পুশ করা ($activeUpdatesCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEF4444).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFFF87171)
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == "OFFLINE_BLOCKED",
                            onClick = { selectedFilter = "OFFLINE_BLOCKED" },
                            label = { Text("অফলাইন লক ($offlineBlockedCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFFFBBF24)
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == "ACTIVE",
                            onClick = { selectedFilter = "ACTIVE" },
                            label = { Text("সক্রিয় অ্যাপ") }
                        )
                    }
                }
            }

            // Apps List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ম্যানেজড সেকেন্ডারি অ্যাপসমূহ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "${filteredApps.size} টি অ্যাপ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Empty State
            if (filteredApps.isEmpty()) {
                item {
                    EmptyAppsView(onNavigateToClonePicker = onNavigateToClonePicker)
                }
            } else {
                items(filteredApps, key = { it.id }) { app ->
                    ManagedAppCard(
                        app = app,
                        onSelectAdmin = { onSelectAppAdmin(app) },
                        onLaunch = { onLaunchSecondaryApp(app) },
                        onToggleOffline = { isBlocked -> onToggleOfflineShield(app, isBlocked) }
                    )
                }
            }
        }

        // Floating Action Button to Clone New App
        FloatingActionButton(
            onClick = onNavigateToClonePicker,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("clone_new_app_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "নতুন অ্যাপ ক্লোন করুন",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "অ্যাপ ক্লোন করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(accentColor.copy(alpha = 0.35f), MaterialTheme.colorScheme.outlineVariant)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ManagedAppCard(
    app: ManagedAppEntity,
    onSelectAdmin: () -> Unit,
    onLaunch: () -> Unit,
    onToggleOffline: (Boolean) -> Unit
) {
    val themeColor = remember(app.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(app.themeColorHex))
        } catch (e: Exception) {
            ElectricIndigo
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectAdmin() }
            .testTag("app_card_${app.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(themeColor.copy(alpha = 0.4f), MaterialTheme.colorScheme.outlineVariant)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: App Icon / Names / Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo Placeholder / Initial
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(themeColor, themeColor.copy(alpha = 0.7f))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.secondaryName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.secondaryName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "মূল অ্যাপ: ${app.appName} (v${app.versionName})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status Badge
                StatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Policy Highlights Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (app.isOfflineBlocked) Icons.Default.WifiOff else Icons.Default.Wifi,
                        contentDescription = null,
                        tint = if (app.isOfflineBlocked) Color(0xFFEF4444) else EmeraldActive,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (app.isOfflineBlocked) "অফলাইন ব্লক সক্রিয়" else "অফলাইন ওপেন হবে",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (app.isOfflineBlocked) Color(0xFFF87171) else EmeraldActive
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "লক: ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = app.isOfflineBlocked,
                        onCheckedChange = { onToggleOffline(it) },
                        modifier = Modifier.size(width = 38.dp, height = 24.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onLaunch,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("launch_secondary_app_${app.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সেকেন্ডারি ওপেন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = onSelectAdmin,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("admin_control_${app.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "অ্যাডমিন কন্ট্রোল",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, text) = when (status) {
        "UPDATE_REQUIRED" -> Triple(Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFF87171), "ফোর্স আপডেট")
        "MAINTENANCE" -> Triple(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFFBBF24), "মেইনটেন্যান্স")
        "BLOCKED" -> Triple(Color(0xFF6B7280).copy(alpha = 0.2f), Color(0xFF9CA3AF), "ব্লক্ড")
        else -> Triple(EmeraldActive.copy(alpha = 0.15f), EmeraldActive, "সক্রিয় (Active)")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyAppsView(onNavigateToClonePicker: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "এখনও কোনো অ্যাপ ক্লোন করা হয়নি",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "যেকোনো ইন্সটল্ড অ্যাপ সিলেক্ট করে ১-ক্লিকে সেকেন্ডারি ক্লোন অ্যাপ তৈরি করুন এবং রিমোট আপডেট কন্ট্রোল শুরু করুন।",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigateToClonePicker,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("প্রথম অ্যাপ ক্লোন করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}
