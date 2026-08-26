package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popup_configs")
data class PopupConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appId: Long, // foreign key to ManagedAppEntity.id
    val isEnabled: Boolean = true,
    val popupType: String = "FORCE_UPDATE", // FORCE_UPDATE, FLEXIBLE_UPDATE, MAINTENANCE, ANNOUNCEMENT, OFFLINE_GUARD
    val title: String = "নতুন আপডেট উপলব্ধ!",
    val message: String = "অ্যাপটির একটি নতুন ও উন্নত সংস্করণ চলে এসেছে। সব নতুন ফিচার পেতে এখনই আপডেট করে নিন।",
    val primaryBtnText: String = "এখনই আপডেট করুন",
    val primaryBtnUrl: String = "https://play.google.com/store/apps",
    val secondaryBtnText: String? = null, // null if non-dismissible/forced
    val isDismissible: Boolean = false, // false = Cannot cancel or dismiss
    val themeColorHex: String = "#6366F1",
    val bannerIcon: String = "ROCKET", // ROCKET, ALERT, MAINTENANCE, SECURITY, GIFT, LOCK, WIFI_OFF
    val bannerImageUrl: String? = null,
    val showChangelogList: Boolean = true,
    val changelogItems: String = "✨ নতুন প্রিমিয়াম ডিজাইন যুক্ত করা হয়েছে\n🚀 অ্যাপের গতি ও কার্যক্ষমতা বৃদ্ধি\n🔒 নতুন সিকিউরিটি কন্ট্রোল ফিচার\n🐞 বাগ ও ক্র্যাশ ফিক্স করা হয়েছে",
    val countdownSeconds: Int = 0, // 0 = no countdown, >0 = countdown timer
    val minRequiredVersion: String = "2.0.0",
    val extraButtonsJson: String = "", // JSON list of CustomPopupButton (WhatsApp, Telegram, YouTube, etc.)
    val isScheduled: Boolean = false, // Scheduled online rollout
    val scheduledTimestamp: Long = 0L, // Milliseconds timestamp for scheduled release
    val scheduleLabel: String = "তাৎক্ষণিক (Instant)",
    val updatedAt: Long = System.currentTimeMillis()
)
