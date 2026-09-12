package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.brain.FridayOfflineBrain
import com.example.model.AppLanguage
import com.example.model.AssistantTab
import com.example.model.InstalledAppItem
import com.example.model.SmartDevice
import com.example.model.SmartDeviceType
import com.example.model.TriggerPhraseOption
import com.example.model.VoiceMessage
import com.example.security.CryptoProtocol
import com.example.voice.VoiceManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FridayViewModel(application: Application) : AndroidViewModel(application) {

    private val brain = FridayOfflineBrain(application)

    private val _activeTab = MutableStateFlow(AssistantTab.VOICE)
    val activeTab: StateFlow<AssistantTab> = _activeTab.asStateFlow()

    private val _triggerPhrase = MutableStateFlow(TriggerPhraseOption.HEY_FRIDAY)
    val triggerPhrase: StateFlow<TriggerPhraseOption> = _triggerPhrase.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _userTitle = MutableStateFlow("Boss")
    val userTitle: StateFlow<String> = _userTitle.asStateFlow()

    private val _autoSpeakResponse = MutableStateFlow(true)
    val autoSpeakResponse: StateFlow<Boolean> = _autoSpeakResponse.asStateFlow()

    private val _messages = MutableStateFlow<List<VoiceMessage>>(emptyList())
    val messages: StateFlow<List<VoiceMessage>> = _messages.asStateFlow()

    private val _lastQuery = MutableStateFlow("")
    val lastQuery: StateFlow<String> = _lastQuery.asStateFlow()

    private val _currentFridaySpeech = MutableStateFlow("FRIDAY online. Offline intelligence active. How can I assist you, Boss?")
    val currentFridaySpeech: StateFlow<String> = _currentFridaySpeech.asStateFlow()

    private val _appLaunchEvent = MutableSharedFlow<String>()
    val appLaunchEvent: SharedFlow<String> = _appLaunchEvent.asSharedFlow()

    private val _networkLogs = MutableStateFlow<List<String>>(emptyList())
    val networkLogs: StateFlow<List<String>> = _networkLogs.asStateFlow()

    private val _wifiPassword = MutableStateFlow(CryptoProtocol.generateSecureWifiPassword(16))
    val wifiPassword: StateFlow<String> = _wifiPassword.asStateFlow()

    // Smart Home Devices
    private val _devices = MutableStateFlow<List<SmartDevice>>(
        listOf(
            SmartDevice(
                id = "dev_light_living",
                name = "Living Room Light",
                room = "Living Room",
                type = SmartDeviceType.LIGHT,
                isOn = true,
                level = 80,
                localIp = "192.168.1.101"
            ),
            SmartDevice(
                id = "dev_fan_living",
                name = "Ceiling Fan",
                room = "Living Room",
                type = SmartDeviceType.FAN,
                isOn = false,
                level = 3,
                localIp = "192.168.1.102"
            ),
            SmartDevice(
                id = "dev_ac_bedroom",
                name = "Thermostat AC",
                room = "Master Bedroom",
                type = SmartDeviceType.THERMOSTAT,
                isOn = true,
                level = 22,
                localIp = "192.168.1.103"
            ),
            SmartDevice(
                id = "dev_lock_front",
                name = "Front Entrance Deadbolt",
                room = "Main Door",
                type = SmartDeviceType.LOCK,
                isOn = true, // locked
                level = 100,
                localIp = "192.168.1.104"
            ),
            SmartDevice(
                id = "dev_plug_kitchen",
                name = "Kitchen Coffee Plug",
                room = "Kitchen",
                type = SmartDeviceType.PLUG,
                isOn = false,
                level = 100,
                localIp = "192.168.1.105"
            ),
            SmartDevice(
                id = "dev_tv_living",
                name = "Smart TV Entertainment",
                room = "Living Room",
                type = SmartDeviceType.TELEVISION,
                isOn = false,
                level = 50,
                localIp = "192.168.1.106"
            )
        )
    )
    val devices: StateFlow<List<SmartDevice>> = _devices.asStateFlow()

    // Installed Apps
    private val _installedApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppItem>> = _installedApps.asStateFlow()

    val voiceManager: VoiceManager by lazy {
        VoiceManager(
            context = application,
            coroutineScope = viewModelScope,
            onSpeechRecognized = { text ->
                handleVoiceInput(text)
            },
            onError = { err ->
                logNetwork("Voice Engine: $err")
            }
        )
    }

    init {
        loadInstalledApps()
        // Welcome message
        val welcomeMsg = VoiceMessage(
            text = "FRIDAY Offline Systems Active. Offline Brain, Local IoT Controls & Apps Ready. Zero internet required.",
            isUser = false,
            actionTag = "INIT"
        )
        _messages.value = listOf(welcomeMsg)
        logNetwork("INIT: AES-256 local broadcast initialized on 192.168.1.0/24")
    }

    fun setTab(tab: AssistantTab) {
        _activeTab.value = tab
    }

    fun setTriggerPhrase(phrase: TriggerPhraseOption) {
        _triggerPhrase.value = phrase
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        speak(language.greetingText)
    }

    fun setUserTitle(title: String) {
        _userTitle.value = title
    }

    fun toggleAutoSpeak() {
        _autoSpeakResponse.value = !_autoSpeakResponse.value
    }

    fun regenerateWifiPassword() {
        _wifiPassword.value = CryptoProtocol.generateSecureWifiPassword(16)
        speak("Generated a new secure 16-character WPA3 password.")
    }

    fun handleVoiceInput(rawText: String) {
        _lastQuery.value = rawText
        processQuery(rawText)
    }

    fun processQuery(queryText: String) {
        if (queryText.isBlank()) return

        val userMsg = VoiceMessage(text = queryText, isUser = true)
        _messages.value = _messages.value + userMsg

        // Run through Friday Offline Brain
        val response = brain.processQuery(
            rawQuery = queryText,
            devices = _devices.value,
            language = _selectedLanguage.value,
            userTitle = _userTitle.value
        )

        _currentFridaySpeech.value = response.displayText

        // Handle Device Actions if triggered
        if (response.targetDeviceId != null) {
            if (response.targetDeviceId == "ALL_LIGHTS") {
                val newState = response.targetDeviceState ?: true
                _devices.value = _devices.value.map { dev ->
                    if (dev.type == SmartDeviceType.LIGHT) {
                        val enc = CryptoProtocol.encryptCommand(dev.id, if (newState) "ON" else "OFF", dev.level)
                        logNetwork("TX [AES-256]: ${dev.name} -> ${if (newState) "ON" else "OFF"} (IV=${enc.ivHex.take(8)})")
                        dev.copy(isOn = newState, lastEncryptedPacket = enc.packetSummary)
                    } else dev
                }
            } else {
                val targetId = response.targetDeviceId
                _devices.value = _devices.value.map { dev ->
                    if (dev.id == targetId) {
                        val newState = response.targetDeviceState ?: !dev.isOn
                        val newLevel = response.targetDeviceLevel ?: dev.level
                        val enc = CryptoProtocol.encryptCommand(dev.id, if (newState) "ON" else "OFF", newLevel)
                        logNetwork("TX [AES-256]: ${dev.name} -> ${if (newState) "ON" else "OFF"} (IV=${enc.ivHex.take(8)})")
                        dev.copy(isOn = newState, level = newLevel, lastEncryptedPacket = enc.packetSummary)
                    } else dev
                }
            }
        }

        // Handle App Launch Action
        if (response.targetAppPackage != null) {
            viewModelScope.launch {
                _appLaunchEvent.emit(response.targetAppPackage)
            }
        }

        // Add Friday Message to history
        val fridayMsg = VoiceMessage(
            text = response.displayText,
            isUser = false,
            codeSnippet = response.codeSnippet,
            codeLang = response.codeLang,
            actionTag = response.actionTag
        )
        _messages.value = _messages.value + fridayMsg

        // Speak aloud if enabled
        if (_autoSpeakResponse.value) {
            speak(response.spokenText)
        }
    }

    fun speak(text: String) {
        voiceManager.speak(text, _selectedLanguage.value)
    }

    fun toggleDevice(deviceId: String) {
        _devices.value = _devices.value.map { dev ->
            if (dev.id == deviceId) {
                val newState = !dev.isOn
                val enc = CryptoProtocol.encryptCommand(dev.id, if (newState) "ON" else "OFF", dev.level)
                val actionName = if (dev.type == SmartDeviceType.LOCK) {
                    if (newState) "Locked" else "Unlocked"
                } else {
                    if (newState) "turned ON" else "turned OFF"
                }
                logNetwork("TX [AES-256]: ${dev.name} -> $actionName (IV=${enc.ivHex.take(8)})")
                if (_autoSpeakResponse.value) {
                    speak("${dev.name} $actionName.")
                }
                dev.copy(isOn = newState, lastEncryptedPacket = enc.packetSummary)
            } else dev
        }
    }

    fun setDeviceLevel(deviceId: String, newLevel: Int) {
        _devices.value = _devices.value.map { dev ->
            if (dev.id == deviceId) {
                val enc = CryptoProtocol.encryptCommand(dev.id, "SET_LEVEL", newLevel)
                logNetwork("TX [AES-256]: ${dev.name} level -> $newLevel")
                dev.copy(level = newLevel, lastEncryptedPacket = enc.packetSummary)
            } else dev
        }
    }

    fun launchAppPackage(pkg: String) {
        viewModelScope.launch {
            _appLaunchEvent.emit(pkg)
        }
    }

    private fun logNetwork(entry: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        _networkLogs.value = listOf("[$time] $entry") + _networkLogs.value.take(20)
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val pm = getApplication<Application>().packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val list = mutableListOf<InstalledAppItem>()

            for (ri in resolveInfos) {
                val label = ri.loadLabel(pm).toString()
                val pkg = ri.activityInfo.packageName
                if (pkg != getApplication<Application>().packageName) {
                    list.add(InstalledAppItem(appName = label, packageName = pkg))
                }
            }

            // If queryIntentActivities is empty (e.g. strict sandbox or no permission), provide popular fallback list
            if (list.isEmpty()) {
                val commonApps = listOf(
                    InstalledAppItem("Camera", "com.google.android.GoogleCamera", true),
                    InstalledAppItem("YouTube", "com.google.android.youtube", false),
                    InstalledAppItem("WhatsApp", "com.whatsapp", false),
                    InstalledAppItem("Settings", "com.android.settings", true),
                    InstalledAppItem("Calculator", "com.google.android.calculator", true),
                    InstalledAppItem("Clock & Alarms", "com.google.android.deskclock", true),
                    InstalledAppItem("Chrome Browser", "com.android.chrome", true),
                    InstalledAppItem("Gallery & Photos", "com.google.android.apps.photos", true),
                    InstalledAppItem("Files", "com.google.android.apps.nbu.files", true),
                    InstalledAppItem("Phone Dialer", "com.google.android.dialer", true)
                )
                _installedApps.value = commonApps
            } else {
                _installedApps.value = list.sortedBy { it.appName }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.cleanup()
    }
}
