package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CustomPopupButton(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "CUSTOM", // WHATSAPP, TELEGRAM, YOUTUBE, WEBSITE, CUSTOM
    val label: String = "যোগাযোগ করুন",
    val url: String = "https://wa.me/",
    val iconKey: String = "WHATSAPP", // WHATSAPP, TELEGRAM, YOUTUBE, GLOBE, PHONE, CHAT, LINK
    val colorHex: String = "#25D366",
    val isEnabled: Boolean = true
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("type", type)
        obj.put("label", label)
        obj.put("url", url)
        obj.put("iconKey", iconKey)
        obj.put("colorHex", colorHex)
        obj.put("isEnabled", isEnabled)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): CustomPopupButton {
            return CustomPopupButton(
                id = obj.optString("id", UUID.randomUUID().toString()),
                type = obj.optString("type", "CUSTOM"),
                label = obj.optString("label", "বাটন"),
                url = obj.optString("url", "https://"),
                iconKey = obj.optString("iconKey", "CUSTOM"),
                colorHex = obj.optString("colorHex", "#25D366"),
                isEnabled = obj.optBoolean("isEnabled", true)
            )
        }

        fun listToJsonString(list: List<CustomPopupButton>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJsonString(json: String?): List<CustomPopupButton> {
            if (json.isNullOrBlank()) return defaultButtons()
            return try {
                val array = JSONArray(json)
                val result = mutableListOf<CustomPopupButton>()
                for (i in 0 until array.length()) {
                    result.add(fromJson(array.getJSONObject(i)))
                }
                if (result.isEmpty()) defaultButtons() else result
            } catch (e: Exception) {
                defaultButtons()
            }
        }

        fun defaultButtons(): List<CustomPopupButton> {
            return listOf(
                CustomPopupButton(
                    id = "btn_wa_default",
                    type = "WHATSAPP",
                    label = "হোয়াটসঅ্যাপ সাপোর্ট",
                    url = "https://wa.me/",
                    iconKey = "WHATSAPP",
                    colorHex = "#25D366",
                    isEnabled = false
                ),
                CustomPopupButton(
                    id = "btn_tg_default",
                    type = "TELEGRAM",
                    label = "টেলিগ্রাম চ্যানেল",
                    url = "https://t.me/",
                    iconKey = "TELEGRAM",
                    colorHex = "#229ED9",
                    isEnabled = false
                ),
                CustomPopupButton(
                    id = "btn_yt_default",
                    type = "YOUTUBE",
                    label = "ইউটিউব চ্যানেল",
                    url = "https://youtube.com/",
                    iconKey = "YOUTUBE",
                    colorHex = "#EF4444",
                    isEnabled = false
                )
            )
        }
    }
}
