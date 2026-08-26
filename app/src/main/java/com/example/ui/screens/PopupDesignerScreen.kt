package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomPopupButton
import com.example.data.model.ManagedAppEntity
import com.example.data.model.PopupConfigEntity
import com.example.ui.components.PopupDialogContent
import com.example.ui.theme.ElectricIndigo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PopupDesignerScreen(
    initialConfig: PopupConfigEntity,
    app: ManagedAppEntity,
    onSaveConfig: (PopupConfigEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialConfig.title) }
    var message by remember { mutableStateOf(initialConfig.message) }
    var primaryBtnText by remember { mutableStateOf(initialConfig.primaryBtnText) }
    var primaryBtnUrl by remember { mutableStateOf(initialConfig.primaryBtnUrl) }
    var secondaryBtnText by remember { mutableStateOf(initialConfig.secondaryBtnText ?: "পরে করব") }
    var isDismissible by remember { mutableStateOf(initialConfig.isDismissible) }
    var popupType by remember { mutableStateOf(initialConfig.popupType) }
    var bannerIcon by remember { mutableStateOf(initialConfig.bannerIcon) }
    var themeColorHex by remember { mutableStateOf(initialConfig.themeColorHex) }
    var changelogItems by remember { mutableStateOf(initialConfig.changelogItems) }
    var showChangelog by remember { mutableStateOf(initialConfig.showChangelogList) }
    var minVersion by remember { mutableStateOf(initialConfig.minRequiredVersion) }

    // Scheduling states
    var isScheduled by remember { mutableStateOf(initialConfig.isScheduled) }
    var scheduledTimestamp by remember { mutableStateOf(initialConfig.scheduledTimestamp) }
    var scheduleLabel by remember { mutableStateOf(initialConfig.scheduleLabel) }

    // Custom Extra Action Buttons (WhatsApp, Telegram, YouTube, etc.)
    val initialButtons = remember(initialConfig.extraButtonsJson) {
        CustomPopupButton.listFromJsonString(initialConfig.extraButtonsJson)
    }
    var customButtonsList by remember { mutableStateOf(initialButtons) }

    // Live preview configuration object
    val liveConfig = remember(
        title, message, primaryBtnText, primaryBtnUrl, secondaryBtnText,
        isDismissible, popupType, bannerIcon, themeColorHex, changelogItems, showChangelog, minVersion,
        isScheduled, scheduledTimestamp, scheduleLabel, customButtonsList
    ) {
        initialConfig.copy(
            title = title,
            message = message,
            primaryBtnText = primaryBtnText,
            primaryBtnUrl = primaryBtnUrl,
            secondaryBtnText = if (isDismissible) secondaryBtnText else null,
            isDismissible = isDismissible,
            popupType = popupType,
            bannerIcon = bannerIcon,
            themeColorHex = themeColorHex,
            changelogItems = changelogItems,
            showChangelogList = showChangelog,
            minRequiredVersion = minVersion,
            isScheduled = isScheduled,
            scheduledTimestamp = scheduledTimestamp,
            scheduleLabel = scheduleLabel,
            extraButtonsJson = CustomPopupButton.listToJsonString(customButtonsList),
            updatedAt = System.currentTimeMillis()
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Section: Live WYSIWYG Preview Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "লাইভ পপ-আপ প্রিভিউ (User View)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Section: Live WYSIWYG Rendered Component
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("popup_live_preview_container"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    PopupDialogContent(
                        config = liveConfig,
                        onPrimaryClick = {},
                        onSecondaryClick = {},
                        onCustomButtonClick = {},
                        isPreview = true
                    )
                }
            }
        }

        // Section: Custom Buttons Section (WhatsApp, Telegram, YouTube)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কাস্টম বাটন (WhatsApp, Telegram, YouTube)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = "পপ-আপে গ্রাহক সহায়তা বা অফিসিয়াল চ্যানেলের সরাসরি লিংক বাটন যুক্ত করুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    customButtonsList.forEachIndexed { index, btn ->
                        CustomButtonEditorRow(
                            button = btn,
                            onUpdate = { updated ->
                                val list = customButtonsList.toMutableList()
                                list[index] = updated
                                customButtonsList = list
                            },
                            onDelete = {
                                val list = customButtonsList.toMutableList()
                                list.removeAt(index)
                                customButtonsList = list
                            }
                        )
                    }

                    // Add Custom Button
                    OutlinedButton(
                        onClick = {
                            val newBtn = CustomPopupButton(
                                type = "CUSTOM",
                                label = "ওয়েবসাইট দেখুন",
                                url = "https://example.com",
                                iconKey = "WEBSITE",
                                colorHex = "#6366F1",
                                isEnabled = true
                            )
                            customButtonsList = customButtonsList + newBtn
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("নতুন কাস্টম বাটন যোগ করুন", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Section: Preset Templates Quick Picker
        item {
            Text(
                text = "রেডিমেড প্রিসেট সিলেক্ট করুন:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(
                    title = "🚀 ফোর্স আপডেট",
                    isSelected = popupType == "FORCE_UPDATE",
                    onClick = {
                        popupType = "FORCE_UPDATE"
                        bannerIcon = "ROCKET"
                        title = "নতুন ভার্সন আপডেট করুন!"
                        message = "অ্যাপটির নতুন ও দ্রুতগতির সংস্করণ চলে এসেছে। এখনই আপডেট করে নিন।"
                        primaryBtnText = "এখনই আপডেট করুন"
                        isDismissible = false
                        themeColorHex = "#EF4444"
                    }
                )

                PresetChip(
                    title = "⚡ ঐচ্ছিক আপডেট",
                    isSelected = popupType == "FLEXIBLE_UPDATE",
                    onClick = {
                        popupType = "FLEXIBLE_UPDATE"
                        bannerIcon = "ROCKET"
                        title = "নতুন আপডেট উপলব্ধ"
                        message = "নতুন ফিচার ও বাগ ফিক্স যুক্ত করা হয়েছে। আপডেট করতে পারেন।"
                        primaryBtnText = "আপডেট করুন"
                        secondaryBtnText = "পরে করব"
                        isDismissible = true
                        themeColorHex = "#6366F1"
                    }
                )

                PresetChip(
                    title = "🛠️ মেইনটেন্যান্স",
                    isSelected = popupType == "MAINTENANCE",
                    onClick = {
                        popupType = "MAINTENANCE"
                        bannerIcon = "MAINTENANCE"
                        title = "সার্ভার রক্ষণাবেক্ষণ চলছে"
                        message = "সাময়িক উন্নয়নের জন্য অ্যাপ সার্ভিস বন্ধ আছে। শীঘ্রই ফিরে আসছি।"
                        primaryBtnText = "হেল্প ও সাপোর্ট"
                        isDismissible = false
                        themeColorHex = "#F59E0B"
                    }
                )

                PresetChip(
                    title = "🎁 স্পেশাল নোটিশ",
                    isSelected = popupType == "ANNOUNCEMENT",
                    onClick = {
                        popupType = "ANNOUNCEMENT"
                        bannerIcon = "GIFT"
                        title = "বিশেষ অফার ও ঘোষণা!"
                        message = "আজকের সকল স্পেশাল ফিচার ও বোনাস উপভোগ করুন।"
                        primaryBtnText = "বিস্তারিত দেখুন"
                        secondaryBtnText = "বন্ধ করুন"
                        isDismissible = true
                        themeColorHex = "#10B981"
                    }
                )
            }
        }

        // Section: Color Palette Selector
        item {
            Text(
                text = "থিম কালার এক্সেন্ট:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val colorOptions = listOf(
                Pair("#6366F1", "Indigo"),
                Pair("#EF4444", "Crimson"),
                Pair("#10B981", "Emerald"),
                Pair("#06B6D4", "Cyan"),
                Pair("#F59E0B", "Amber"),
                Pair("#8B5CF6", "Purple"),
                Pair("#EC4899", "Pink")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorOptions.forEach { (hex, _) ->
                    val isSelected = themeColorHex.equals(hex, ignoreCase = true)
                    val parsedColor = remember(hex) {
                        try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { ElectricIndigo }
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { themeColorHex = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Section: Icon Style Selector
        item {
            Text(
                text = "পপ-আপ আইকন স্টাইল:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val iconsList = listOf(
                Pair("ROCKET", Icons.Default.RocketLaunch),
                Pair("ALERT", Icons.Default.Warning),
                Pair("MAINTENANCE", Icons.Default.Build),
                Pair("SECURITY", Icons.Default.Security),
                Pair("GIFT", Icons.Default.CardGiftcard),
                Pair("LOCK", Icons.Default.Lock)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                iconsList.forEach { (key, icon) ->
                    val isSelected = bannerIcon.equals(key, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { bannerIcon = key },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = key,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Section: Mandatory Force Update Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "বাধ্যতামূলক মোড (Mandatory / Non-Dismissible)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (!isDismissible) "ব্যবহারকারী আপডেট না করে পপ-আপ কাটতে পারবে না।"
                            else "ব্যবহারকারী চাইলে পরে করতে পারবে (ক্যান্সেল বাটন থাকবে)।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = !isDismissible,
                        onCheckedChange = { isDismissible = !it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier.testTag("mandatory_popup_switch")
                    )
                }
            }
        }

        // Section: Text Inputs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("পপ-আপের শিরোনাম (Title)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("popup_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("পপ-আপের বিস্তারিত মেসেজ (Message)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("popup_message_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = primaryBtnText,
                    onValueChange = { primaryBtnText = it },
                    label = { Text("মেইন বাটন টেক্সট") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("popup_primary_btn_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = primaryBtnUrl,
                    onValueChange = { primaryBtnUrl = it },
                    label = { Text("অ্যাকশন / ডাউনলোড লিংক URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("popup_url_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                if (isDismissible) {
                    OutlinedTextField(
                        value = secondaryBtnText,
                        onValueChange = { secondaryBtnText = it },
                        label = { Text("সেকেন্ডারি / ক্যান্সেল বাটন টেক্সট") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("popup_secondary_btn_text_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                // Changelogs Textarea
                OutlinedTextField(
                    value = changelogItems,
                    onValueChange = { changelogItems = it },
                    label = { Text("চেঞ্জলগ ও নতুন ফিচারের তালিকা (প্রতি লাইনে ১টি)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("popup_changelog_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3,
                    maxLines = 6
                )
            }
        }

        // Section: Save Button
        item {
            Button(
                onClick = { onSaveConfig(liveConfig) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_popup_design_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "পপ-আপ ডিজাইন ও সেটিংস সেভ করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun CustomButtonEditorRow(
    button: CustomPopupButton,
    onUpdate: (CustomPopupButton) -> Unit,
    onDelete: () -> Unit
) {
    val brandColor = when (button.type.uppercase()) {
        "WHATSAPP" -> Color(0xFF25D366)
        "TELEGRAM" -> Color(0xFF229ED9)
        "YOUTUBE" -> Color(0xFFEF4444)
        else -> MaterialTheme.colorScheme.primary
    }

    val iconVector = when (button.type.uppercase()) {
        "WHATSAPP" -> Icons.Default.Chat
        "TELEGRAM" -> Icons.Default.Send
        "YOUTUBE" -> Icons.Default.PlayArrow
        else -> Icons.Default.Language
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(brandColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(iconVector, contentDescription = null, tint = brandColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (button.type.uppercase()) {
                            "WHATSAPP" -> "WhatsApp বাটন"
                            "TELEGRAM" -> "Telegram বাটন"
                            "YOUTUBE" -> "YouTube বাটন"
                            else -> "কাস্টম বাটন"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = button.isEnabled,
                        onCheckedChange = { onUpdate(button.copy(isEnabled = it)) },
                        modifier = Modifier.size(36.dp)
                    )
                    if (button.type == "CUSTOM") {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (button.isEnabled) {
                OutlinedTextField(
                    value = button.label,
                    onValueChange = { onUpdate(button.copy(label = it)) },
                    label = { Text("বাটন লেবেল (Label)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = button.url,
                    onValueChange = { onUpdate(button.copy(url = it)) },
                    label = { Text("লিংক / URL / নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun PresetChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
