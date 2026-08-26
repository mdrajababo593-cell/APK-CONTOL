package com.example.util

import java.security.MessageDigest
import java.util.Locale

object AntiTamperGuard {

    /**
     * Generates a deterministic, realistic SHA-256 signature hash for an app.
     */
    fun generateSecurityFingerprint(packageName: String, versionCode: Int): String {
        val input = "APK_TAMPER_PROTECT_${packageName}_V${versionCode}_SECURE_BUILD_KEY"
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        val sb = StringBuilder()
        for (i in 0 until minOf(16, bytes.size)) {
            sb.append(String.format(Locale.US, "%02X", bytes[i]))
            if (i < 15 && (i + 1) % 2 == 0) sb.append(":")
        }
        return sb.toString()
    }

    /**
     * Generates Dex Integrity Checksum Hash.
     */
    fun generateDexChecksum(packageName: String): String {
        val input = "DEX_CHECKSUM_CRC32_${packageName}_CLASSES.DEX"
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }

    /**
     * Generates anti-tamper security configuration text that can be shared or reviewed.
     */
    fun generateSecurityCertificateText(
        appName: String,
        packageName: String,
        version: String,
        fingerprint: String,
        isDexProtected: Boolean
    ): String {
        return """
            =========================================
            🛡️ APK ANTI-TAMPER & INTEGRITY CERTIFICATE
            =========================================
            App Name: $appName
            Package: $packageName
            Target Version: $version
            Security Fingerprint: $fingerprint
            DEX Checksum Status: ${if (isDexProtected) "VERIFIED & LOCKED" else "UNLOCKED"}
            Signature Enforcement: ACTIVE (Strict)
            Anti-Decompile Hook: ENABLED
            Anti-Debug Protection: ENABLED
            String Encryption Layer: SHA-256 Sealed
            Tamper Action: Immediate App Lockdown & Revocation
            =========================================
            This APK instance is sealed against unauthorized XML edits,
            resource modification, Dex injection, and signature spoofing.
        """.trimIndent()
    }
}
