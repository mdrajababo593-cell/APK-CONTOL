package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CustomPopupButton
import com.example.data.model.PopupConfigEntity

@Composable
fun CustomPopupDialog(
    config: PopupConfigEntity,
    onPrimaryAction: (String) -> Unit,
    onSecondaryAction: () -> Unit,
    onCustomButtonAction: (CustomPopupButton) -> Unit = {}
) {
    Dialog(
        onDismissRequest = {
            if (config.isDismissible) {
                onSecondaryAction()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = config.isDismissible,
            dismissOnClickOutside = config.isDismissible
        )
    ) {
        PopupDialogContent(
            config = config,
            onPrimaryClick = { onPrimaryAction(config.primaryBtnUrl) },
            onSecondaryClick = onSecondaryAction,
            onCustomButtonClick = onCustomButtonAction,
            isPreview = false
        )
    }
}

@Composable
fun PopupDialogContent(
    config: PopupConfigEntity,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onCustomButtonClick: (CustomPopupButton) -> Unit = {},
    isPreview: Boolean = false,
    modifier: Modifier = Modifier
) {
    val themeColor = remember(config.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(config.themeColorHex))
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
    }

    val iconData = getPopupIcon(config.bannerIcon, config.popupType)
    val customButtons = remember(config.extraButtonsJson) {
        CustomPopupButton.listFromJsonString(config.extraButtonsJson).filter { it.isEnabled }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (isPreview) 0.dp else 8.dp)
            .testTag("popup_dialog_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPreview) 0.dp else 12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                colors = listOf(themeColor.copy(alpha = 0.6f), MaterialTheme.colorScheme.outlineVariant)
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge / Icon Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(themeColor.copy(alpha = 0.25f), themeColor.copy(alpha = 0.08f))
                        )
                    )
                    .border(2.dp, themeColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconData.icon,
                    contentDescription = "Popup Header Icon",
                    tint = themeColor,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mandatory / Version / Schedule Tag
            if (!config.isDismissible) {
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626))),
                        width = 1.dp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "বাধ্যতামূলক আপডেট (Mandatory)",
                        color = Color(0xFFF87171),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            } else {
                Surface(
                    color = themeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "সংস্করণ: v${config.minRequiredVersion}",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Scheduled rollout indicator
            if (config.isScheduled && config.scheduledTimestamp > System.currentTimeMillis()) {
                Surface(
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "অনলাইন শিডিউল্ড রোলআউট",
                            color = Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Title
            Text(
                text = config.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Message
            Text(
                text = config.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // Changelogs List (if enabled)
            if (config.showChangelogList && config.changelogItems.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.outlineVariant, Color.Transparent)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "নতুন পরিবর্তন ও ফিচারসমূহ:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        val items = config.changelogItems.split("\n").filter { it.isNotBlank() }
                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Custom Branded Action Buttons (WhatsApp, Telegram, YouTube, etc.)
            if (customButtons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "সহায়তা ও অফিসিয়াল চ্যানেল:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    customButtons.forEach { btn ->
                        CustomActionButtonItem(
                            button = btn,
                            onClick = { onCustomButtonClick(btn) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary & Secondary Buttons Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPrimaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("popup_primary_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (config.popupType == "FORCE_UPDATE" || config.popupType == "FLEXIBLE_UPDATE")
                            Icons.Default.Download else Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = config.primaryBtnText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                if (config.isDismissible && !config.secondaryBtnText.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = onSecondaryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("popup_secondary_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = config.secondaryBtnText,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomActionButtonItem(
    button: CustomPopupButton,
    onClick: () -> Unit
) {
    val btnColor = remember(button.colorHex, button.type) {
        try {
            when (button.type.uppercase()) {
                "WHATSAPP" -> Color(0xFF25D366)
                "TELEGRAM" -> Color(0xFF229ED9)
                "YOUTUBE" -> Color(0xFFEF4444)
                else -> Color(android.graphics.Color.parseColor(button.colorHex))
            }
        } catch (e: Exception) {
            Color(0xFF25D366)
        }
    }

    val iconVector = when (button.type.uppercase()) {
        "WHATSAPP" -> Icons.Default.Chat
        "TELEGRAM" -> Icons.Default.Send
        "YOUTUBE" -> Icons.Default.PlayArrow
        "WEBSITE" -> Icons.Default.Language
        "PHONE" -> Icons.Default.Phone
        else -> when (button.iconKey.uppercase()) {
            "WHATSAPP" -> Icons.Default.Chat
            "TELEGRAM" -> Icons.Default.Send
            "YOUTUBE" -> Icons.Default.PlayArrow
            "GLOBE", "WEBSITE" -> Icons.Default.Language
            "PHONE" -> Icons.Default.Phone
            "SUPPORT" -> Icons.Default.SupportAgent
            else -> Icons.Default.Link
        }
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = btnColor.copy(alpha = 0.12f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(btnColor.copy(alpha = 0.6f), btnColor.copy(alpha = 0.2f))
            ),
            width = 1.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_btn_${button.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(btnColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = btnColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = button.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = button.url.take(35) + if (button.url.length > 35) "..." else "",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = btnColor
            ) {
                Text(
                    text = "যুক্ত হন",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

data class PopupIconInfo(val icon: ImageVector, val label: String)

fun getPopupIcon(iconKey: String, popupType: String): PopupIconInfo {
    return when (iconKey.uppercase()) {
        "ROCKET" -> PopupIconInfo(Icons.Default.RocketLaunch, "রকেট")
        "ALERT", "WARNING" -> PopupIconInfo(Icons.Default.Warning, "সতর্কবার্তা")
        "MAINTENANCE", "BUILD" -> PopupIconInfo(Icons.Default.Build, "রক্ষণাবেক্ষণ")
        "SECURITY" -> PopupIconInfo(Icons.Default.Security, "নিরাপত্তা")
        "GIFT" -> PopupIconInfo(Icons.Default.CardGiftcard, "উপহার / অফার")
        "LOCK" -> PopupIconInfo(Icons.Default.Lock, "লক")
        "WIFI_OFF" -> PopupIconInfo(Icons.Default.WifiOff, "অফলাইন")
        else -> when (popupType) {
            "MAINTENANCE" -> PopupIconInfo(Icons.Default.Build, "রক্ষণাবেক্ষণ")
            "ANNOUNCEMENT" -> PopupIconInfo(Icons.Default.Info, "বিজ্ঞপ্তি")
            "OFFLINE_GUARD" -> PopupIconInfo(Icons.Default.WifiOff, "অফলাইন")
            else -> PopupIconInfo(Icons.Default.RocketLaunch, "আপডেট")
        }
    }
}
