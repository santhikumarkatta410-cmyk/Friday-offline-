package com.example.model

import java.util.UUID

enum class SmartDeviceType(val displayName: String, val defaultRoom: String) {
    LIGHT("Smart Light", "Living Room"),
    FAN("Ceiling Fan", "Living Room"),
    THERMOSTAT("Thermostat AC", "Master Bedroom"),
    LOCK("Smart Deadbolt", "Main Entrance"),
    PLUG("Smart Power Plug", "Kitchen"),
    TELEVISION("Smart TV Hub", "Living Room")
}

data class SmartDevice(
    val id: String,
    val name: String,
    val room: String,
    val type: SmartDeviceType,
    val isOn: Boolean,
    val level: Int = 100, // brightness % or speed (1-5) or temperature (°C)
    val localIp: String,
    val port: Int = 8883,
    val isEncrypted: Boolean = true,
    val lastEncryptedPacket: String = "",
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

data class VoiceMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val codeSnippet: String? = null,
    val codeLang: String = "python",
    val actionTag: String? = null
)

enum class TriggerPhraseOption(val label: String, val matchPrefix: String) {
    HEY_FRIDAY("Hey Friday", "hey friday"),
    FRIDAY("Friday", "friday"),
    HI_SRI("Hi Sri", "hi sri"),
    BOSS("Boss", "boss"),
    ANTA_REDDY("Anta Reddy", "anta reddy")
}

enum class AppLanguage(val code: String, val label: String, val greetingText: String) {
    ENGLISH("en", "English (US/IN)", "Online and ready, Boss. All local systems operational."),
    TELUGU("te", "తెలుగు (Telugu)", "అంతా రెడీగా ఉంది బాస్. మీ ఆదేశం ఏమిటి?"),
    HINDI("hi", "हिन्दी (Hindi)", "नमस्ते बॉस! फ्राइडे पूरी तरह ऑफलाइन तैयार है।"),
    SPANISH("es", "Español", "Sistemas listos, Jefe. Procesamiento local activo.")
}

enum class SpeechEngineType(val label: String, val engineName: String, val details: String) {
    VOSK_OFFLINE(
        label = "Vosk Offline STT",
        engineName = "Vosk Kaldi Engine v0.3.47",
        details = "Independent, 100% offline acoustic speech recognizer with local Kaldi decoder"
    ),
    SYSTEM_OFFLINE(
        label = "Android Built-In",
        engineName = "Android On-Device Engine",
        details = "Native Android offline speech recognition engine with EXTRA_PREFER_OFFLINE"
    )
}

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val date: String
)

data class InstalledAppItem(
    val appName: String,
    val packageName: String,
    val isSystem: Boolean = false
)

enum class AssistantTab {
    VOICE,
    SMART_HOME,
    OFFLINE_BRAIN,
    APPS,
    SETTINGS
}
