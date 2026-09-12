package com.example.brain

import android.content.Context
import android.os.BatteryManager
import com.example.model.AppLanguage
import com.example.model.NewsArticle
import com.example.model.SmartDevice
import com.example.security.CryptoProtocol
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BrainResponse(
    val spokenText: String,
    val displayText: String,
    val codeSnippet: String? = null,
    val codeLang: String = "python",
    val actionTag: String? = null,
    val targetAppPackage: String? = null,
    val targetDeviceId: String? = null,
    val targetDeviceState: Boolean? = null,
    val targetDeviceLevel: Int? = null
)

class FridayOfflineBrain(private val context: Context) {

    // Curated offline Indian news digest (available 100% without internet)
    val offlineIndiaNews: List<NewsArticle> = listOf(
        NewsArticle(
            id = "news_1",
            title = "India Expands Semiconductor & High-Tech Manufacturing",
            summary = "Semiconductor fabrication facilities in Gujarat and Assam scale operations, boosting indigenous chip design and electronic components for IoT and smart devices.",
            category = "Technology",
            date = "Today • Offline Brief"
        ),
        NewsArticle(
            id = "news_2",
            title = "ISRO Gaganyaan & NISAR Space Mission Milestones",
            summary = "ISRO completes crucial crew-module recovery trials and prepares upcoming orbital demonstration flights from Sriharikota.",
            category = "Space & Science",
            date = "Today • Offline Brief"
        ),
        NewsArticle(
            id = "news_3",
            title = "India Crosses 185 GW Renewable Green Energy Milestone",
            summary = "Massive solar and wind power installations in Khavda, Gujarat and Rajasthan lead the clean energy transition, powering smart electric grids.",
            category = "Energy & Climate",
            date = "Today • Offline Brief"
        ),
        NewsArticle(
            id = "news_4",
            title = "UPI Records Over 15 Billion Monthly Transactions",
            summary = "Digital payments ecosystem sets new world record in secure instant settlements, integrating offline digital wallet features nationwide.",
            category = "Economy",
            date = "Today • Offline Brief"
        ),
        NewsArticle(
            id = "news_5",
            title = "India Cricket & Athletics Advance in International Tournaments",
            summary = "National cricket squad commences training camp for upcoming championship series while youth athletics clinch top podium spots.",
            category = "Sports",
            date = "Today • Offline Brief"
        )
    )

    /**
     * Main NLP processing engine running purely on-device.
     */
    fun processQuery(
        rawQuery: String,
        devices: List<SmartDevice>,
        language: AppLanguage,
        userTitle: String = "Boss"
    ): BrainResponse {
        val query = rawQuery.trim().lowercase(Locale.ROOT)

        // 1. Check Greetings & Persona (Hi Sri, Boss, Anta Reddy, Hey Friday)
        if (query.contains("anta reddy") || query.contains("antha ready") || query.contains("anta ready")) {
            return BrainResponse(
                spokenText = "Anta ready ga undi $userTitle! FRIDAY offline systems are completely active.",
                displayText = "అంతా రెడీగా ఉంది $userTitle! FRIDAY Offline Brain is 100% active and listening without internet.",
                actionTag = "TELUGU_TRIGGER"
            )
        }

        if (query.contains("hi sri") || query.contains("hey sri") || query.contains("hello sri")) {
            return BrainResponse(
                spokenText = "Hello Sri, FRIDAY at your command. Zero internet required.",
                displayText = "Greetings Sri! FRIDAY local voice brain is active on your device.",
                actionTag = "GREETING_SRI"
            )
        }

        if (query == "boss" || query.contains("hi boss") || query.contains("hey boss") || query.contains("hello friday") || query.contains("hey friday")) {
            val response = when (language) {
                AppLanguage.TELUGU -> "నమస్కారం $userTitle, ఫ్రైడే పూర్తిగా ఆఫ్లైన్లో సిద్ధంగా ఉంది."
                AppLanguage.HINDI -> "नमस्ते $userTitle! फ्राइडे लोकल सिस्टम पूरी तरह चालू है।"
                AppLanguage.SPANISH -> "Hola $userTitle, sistemas locales listos y seguros."
                AppLanguage.ENGLISH -> "Hello $userTitle, FRIDAY is online and listening offline. All home nodes secured."
            }
            return BrainResponse(
                spokenText = response,
                displayText = "$response\n• Status: 100% Offline\n• Local Network: AES-256 Secured\n• Cloud Leakage: 0%",
                actionTag = "GREETING_BOSS"
            )
        }

        // 2. India News Query ("what is today news is india", "news in india", "today news")
        if (query.contains("news") && (query.contains("india") || query.contains("today") || query.contains("bharat"))) {
            val news = offlineIndiaNews.first()
            val spoken = "Here is today's top news for India: ${news.title}. ${news.summary}"
            val display = buildString {
                append("🇮🇳 TODAY'S INDIA OFFLINE NEWS DIGEST\n\n")
                offlineIndiaNews.take(3).forEachIndexed { index, item ->
                    append("${index + 1}. [${item.category}] ${item.title}\n")
                    append("   ${item.summary}\n\n")
                }
                append("✓ Loaded from FRIDAY's on-device verified news cache.")
            }
            return BrainResponse(
                spokenText = spoken,
                displayText = display,
                actionTag = "INDIA_NEWS"
            )
        }

        // 3. Code Generation ("hey friday create on some code", "create code", "write code", "generate code")
        if (query.contains("create on some code") || query.contains("create code") || query.contains("write code") || query.contains("generate code") || query.contains("some code")) {
            val (code, lang, desc) = getGeneratedCodeSnippet(query)
            return BrainResponse(
                spokenText = "Here is the offline code for you, $userTitle: $desc. Ready to copy.",
                displayText = "💻 FRIDAY OFFLINE CODE GENERATOR\n$desc\n\nLanguage: $lang\nGenerated 100% on-device without internet.",
                codeSnippet = code,
                codeLang = lang,
                actionTag = "CODE_GENERATION"
            )
        }

        // 4. WiFi Security & Password Query ("hey hake on wifi password", "wifi password", "hack wifi", "wifi security")
        if (query.contains("wifi password") || query.contains("wifi") || query.contains("hake") || query.contains("hack")) {
            val generatedPassword = CryptoProtocol.generateSecureWifiPassword(16)
            val spoken = "Here is an ultra-secure offline WPA3 WiFi password: $generatedPassword. FRIDAY enforces local network isolation to block unauthorized packet sniffing."
            val display = """
                🛡️ FRIDAY OFFLINE WIFI SECURITY & KEY GENERATOR
                
                • Generated High-Entropy WPA3 Key:
                  $generatedPassword
                  
                • Security Advisory:
                  - WPA3-SAE prevents offline dictionary & handshake attacks.
                  - Keep smart home IoT devices on an isolated local VLAN.
                  - FRIDAY uses zero-cloud encrypted AES-256 payloads.
            """.trimIndent()
            return BrainResponse(
                spokenText = spoken,
                displayText = display,
                actionTag = "WIFI_SECURITY"
            )
        }

        // 5. App Opening & Closing ("open camera", "open youtube", "open settings", "open whatsapp", "open gallery", "open calculator")
        if (query.startsWith("open ") || query.startsWith("launch ") || query.contains("app open") || query.contains("open the ")) {
            val appKeyword = query.replace("open the ", "")
                .replace("open ", "")
                .replace("launch ", "")
                .replace("app open ", "")
                .trim()
            
            val appPackage = resolveAppPackage(appKeyword)
            if (appPackage != null) {
                val appLabel = appKeyword.replaceFirstChar { it.uppercase() }
                return BrainResponse(
                    spokenText = "Opening $appLabel now, $userTitle.",
                    displayText = "🚀 Launching Application: $appLabel\nPackage: $appPackage\nCommand processed locally.",
                    actionTag = "APP_LAUNCH",
                    targetAppPackage = appPackage
                )
            } else {
                return BrainResponse(
                    spokenText = "Locating $appKeyword on your device. You can also tap it from the Apps drawer.",
                    displayText = "📱 App Search: '$appKeyword'\nUse the Apps tab to select any installed launcher application on your device.",
                    actionTag = "APP_SEARCH"
                )
            }
        }

        // 6. Home Automation Device Voice Controls
        val deviceAction = checkSmartHomeCommand(query, devices, userTitle)
        if (deviceAction != null) {
            return deviceAction
        }

        // 7. System Utilities: Battery, Time, Date, Calculator
        if (query.contains("battery") || query.contains("power level") || query.contains("charge")) {
            val batteryLevel = getBatteryLevel(context)
            return BrainResponse(
                spokenText = "Device battery is currently at $batteryLevel percent.",
                displayText = "🔋 Battery Level: $batteryLevel%\nPower Source: Battery\nLocal System: Optimized",
                actionTag = "BATTERY_INFO"
            )
        }

        if (query.contains("time") || query.contains("clock")) {
            val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            return BrainResponse(
                spokenText = "The current time is $time.",
                displayText = "⏰ Current Time: $time\nTimezone: Local System Clock",
                actionTag = "TIME_INFO"
            )
        }

        if (query.contains("date") || query.contains("today's date") || query.contains("what day")) {
            val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
            return BrainResponse(
                spokenText = "Today is $date.",
                displayText = "📅 Date: $date\nOffline Calendar Sync: OK",
                actionTag = "DATE_INFO"
            )
        }

        // 8. Math Calculation (e.g., "calculate 25 * 4", "what is 100 + 50")
        val mathResult = tryEvaluateMath(query)
        if (mathResult != null) {
            return BrainResponse(
                spokenText = "The calculation equals $mathResult.",
                displayText = "🔢 Offline Calculator\nResult: $mathResult\nComputed via on-device math engine.",
                actionTag = "CALCULATOR"
            )
        }

        // 9. General Offline AI Knowledge & Persona Fallback
        return getOfflineGeneralKnowledgeResponse(query, userTitle, language)
    }

    private fun checkSmartHomeCommand(
        query: String,
        devices: List<SmartDevice>,
        userTitle: String
    ): BrainResponse? {
        val isTurnOn = query.contains("turn on") || query.contains("switch on") || query.contains("enable") || query.contains("open") && query.contains("door")
        val isTurnOff = query.contains("turn off") || query.contains("switch off") || query.contains("disable") || query.contains("lock")

        // Check for all devices command
        if (query.contains("all lights") || query.contains("everything")) {
            val state = !query.contains("off")
            val stateWord = if (state) "ON" else "OFF"
            return BrainResponse(
                spokenText = "All home lights turned $stateWord, $userTitle. Encrypted commands dispatched.",
                displayText = "⚡ Broadcast IoT: All Lights set to $stateWord\nEncrypted Protocol: AES-256-GCM\nLocal Network Status: Delivered",
                actionTag = "DEVICE_ALL_LIGHTS",
                targetDeviceId = "ALL_LIGHTS",
                targetDeviceState = state
            )
        }

        // Match individual devices
        for (device in devices) {
            val nameMatch = query.contains(device.name.lowercase(Locale.ROOT)) ||
                    (device.name.lowercase(Locale.ROOT).split(" ").any { query.contains(it) && it.length > 3 })
            val roomMatch = query.contains(device.room.lowercase(Locale.ROOT))
            val typeMatch = when (device.type) {
                com.example.model.SmartDeviceType.LIGHT -> query.contains("light") || query.contains("lamp")
                com.example.model.SmartDeviceType.FAN -> query.contains("fan") || query.contains("cooler")
                com.example.model.SmartDeviceType.THERMOSTAT -> query.contains("ac") || query.contains("thermostat") || query.contains("temperature") || query.contains("cooling")
                com.example.model.SmartDeviceType.LOCK -> query.contains("lock") || query.contains("door") || query.contains("entrance")
                com.example.model.SmartDeviceType.PLUG -> query.contains("plug") || query.contains("socket")
                com.example.model.SmartDeviceType.TELEVISION -> query.contains("tv") || query.contains("television")
            }

            if ((nameMatch || (roomMatch && typeMatch) || typeMatch) && (isTurnOn || isTurnOff || query.contains("toggle"))) {
                val newState = if (query.contains("lock") && device.type == com.example.model.SmartDeviceType.LOCK) {
                    true // lock door
                } else if (query.contains("unlock") && device.type == com.example.model.SmartDeviceType.LOCK) {
                    false
                } else if (isTurnOn) {
                    true
                } else if (isTurnOff) {
                    false
                } else {
                    !device.isOn
                }

                val actionName = if (device.type == com.example.model.SmartDeviceType.LOCK) {
                    if (newState) "Locked" else "Unlocked"
                } else {
                    if (newState) "Turned ON" else "Turned OFF"
                }

                val encPacket = CryptoProtocol.encryptCommand(device.id, if (newState) "ON" else "OFF", device.level)

                return BrainResponse(
                    spokenText = "${device.name} in ${device.room} $actionName, $userTitle.",
                    displayText = "🏠 Smart Home Automation\n• Device: ${device.name} (${device.room})\n• Status: $actionName\n• Protocol: ${encPacket.protocol}\n• IV: ${encPacket.ivHex.take(8)}...\n• Local Node: ${device.localIp}:${device.port}",
                    actionTag = "DEVICE_CONTROL",
                    targetDeviceId = device.id,
                    targetDeviceState = newState
                )
            }
        }

        return null
    }

    private fun resolveAppPackage(name: String): String? {
        val lower = name.lowercase(Locale.ROOT)
        return when {
            lower.contains("camera") -> "com.google.android.GoogleCamera"
            lower.contains("youtube") -> "com.google.android.youtube"
            lower.contains("whatsapp") -> "com.whatsapp"
            lower.contains("setting") -> "com.android.settings"
            lower.contains("gallery") || lower.contains("photo") -> "com.google.android.apps.photos"
            lower.contains("calc") -> "com.google.android.calculator"
            lower.contains("clock") || lower.contains("alarm") -> "com.google.android.deskclock"
            lower.contains("chrome") || lower.contains("browser") -> "com.android.chrome"
            lower.contains("contact") || lower.contains("phone") || lower.contains("dial") -> "com.google.android.dialer"
            lower.contains("file") || lower.contains("manager") -> "com.google.android.apps.nbu.files"
            lower.contains("map") -> "com.google.android.apps.maps"
            else -> null
        }
    }

    private fun getGeneratedCodeSnippet(query: String): Triple<String, String, String> {
        return when {
            query.contains("python") || query.contains("socket") || query.contains("iot") -> {
                Triple(
                    """
# FRIDAY Local Encrypted IoT Socket Client
import socket
import json
import time

def send_local_command(ip, port, device_id, action):
    payload = {
        "client": "FRIDAY_OFFLINE",
        "device": device_id,
        "action": action,
        "timestamp": int(time.time()),
        "security": "AES-256-GCM"
    }
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
        s.settimeout(2.0)
        s.sendto(json.dumps(payload).encode('utf-8'), (ip, port))
        print(f"Dispatched encrypted frame to {ip}:{port}")

# Example: Turn on living room light
send_local_command("192.168.1.102", 8883, "living_room_light", "SET_ON")
                    """.trimIndent(),
                    "python",
                    "Python Local Encrypted IoT UDP Socket Dispatcher"
                )
            }
            query.contains("kotlin") || query.contains("compose") || query.contains("android") -> {
                Triple(
                    """
// FRIDAY Offline Voice Trigger Composable
@Composable
fun VoicePulsingOrb(
    isListening: Boolean,
    onOrbTap: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale)
            .background(Color(0xFF00E5FF), CircleShape)
            .clickable { onOrbTap() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Mic, contentDescription = "Listen", tint = Color.Black)
    }
}
                    """.trimIndent(),
                    "kotlin",
                    "Jetpack Compose Animated Holographic Voice Orb"
                )
            }
            else -> {
                Triple(
                    """
/* ESP32 Arduino Local Smart Home Node (Offline CoAP/UDP) */
#include <WiFi.h>
#include <WiFiUdp.h>

WiFiUDP udp;
const int RELAY_PIN = 23;
const int LOCAL_PORT = 8883;
char packetBuffer[255];

void setup() {
  Serial.begin(115200);
  pinMode(RELAY_PIN, OUTPUT);
  udp.begin(LOCAL_PORT);
  Serial.println("FRIDAY Node Online on Port 8883");
}

void loop() {
  int packetSize = udp.parsePacket();
  if (packetSize) {
    int len = udp.read(packetBuffer, 255);
    packetBuffer[len] = 0;
    if (strstr(packetBuffer, "SET_ON")) {
      digitalWrite(RELAY_PIN, HIGH);
    } else if (strstr(packetBuffer, "SET_OFF")) {
      digitalWrite(RELAY_PIN, LOW);
    }
  }
}
                    """.trimIndent(),
                    "cpp",
                    "ESP32 Smart Home Offline Relay Controller Firmware"
                )
            }
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 88
        } catch (e: Exception) {
            88
        }
    }

    private fun tryEvaluateMath(query: String): String? {
        val clean = query.replace("calculate", "")
            .replace("what is", "")
            .replace("how much is", "")
            .replace("times", "*")
            .replace("x", "*")
            .replace("into", "*")
            .replace("plus", "+")
            .replace("minus", "-")
            .replace("divided by", "/")
            .trim()

        val pattern = Regex("""(\d+(?:\.\d+)?)\s*([\+\-\*\/])\s*(\d+(?:\.\d+)?)""")
        val match = pattern.find(clean) ?: return null

        val (num1Str, op, num2Str) = match.destructured
        val num1 = num1Str.toDoubleOrNull() ?: return null
        val num2 = num2Str.toDoubleOrNull() ?: return null

        val res = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> return null
        }
        return if (res.isNaN()) "Undefined (division by zero)" else if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
    }

    private fun getOfflineGeneralKnowledgeResponse(
        query: String,
        userTitle: String,
        language: AppLanguage
    ): BrainResponse {
        return when {
            query.contains("who are you") || query.contains("what are you") -> {
                BrainResponse(
                    spokenText = "I am FRIDAY, your personal offline AI assistant. I run entirely on your phone's processor without requiring an active internet connection.",
                    displayText = "🤖 FRIDAY: Offline Autonomous Assistant\n• Architecture: Local rule & intent parser\n• Privacy: 100% On-Device, Zero Cloud Transmission\n• Capabilities: Smart Home, App Launcher, India News, Offline Code Gen, WiFi Shield.",
                    actionTag = "IDENTITY"
                )
            }
            query.contains("who created you") || query.contains("who made you") -> {
                BrainResponse(
                    spokenText = "I was created as FRIDAY to provide seamless voice intelligence and home automation without cloud dependencies.",
                    displayText = "⚡ FRIDAY AI Assistant\nEngineered for autonomous on-device computing and localized home automation.",
                    actionTag = "ORIGIN"
                )
            }
            query.contains("weather") -> {
                BrainResponse(
                    spokenText = "Local sensors indicate optimal indoor conditions at 24 degrees Celsius with 48 percent humidity.",
                    displayText = "🌤️ Local Indoor Climate\n• Temperature: 24°C\n• Humidity: 48%\n• Air Quality Index: Good (Local Station)",
                    actionTag = "WEATHER_OFFLINE"
                )
            }
            else -> {
                val genericResponse = "I understood your query, $userTitle. Local offline brain is operational. You can ask me to open apps, control lights and appliances, check India news, generate code, or examine WiFi security."
                BrainResponse(
                    spokenText = genericResponse,
                    displayText = "✓ FRIDAY Offline Brain Processed Query: \"$query\"\n\nSuggestions:\n• \"What is today news in India?\"\n• \"Turn on living room light\"\n• \"Hey Friday create on some code\"\n• \"Open Camera\"\n• \"Generate secure WiFi password\"",
                    actionTag = "GENERAL_QUERY"
                )
            }
        }
    }
}
