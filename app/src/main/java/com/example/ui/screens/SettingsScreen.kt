package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.SpeechEngineType
import com.example.model.TriggerPhraseOption
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.FridayDarkBg
import com.example.ui.theme.FridayDarkCard
import com.example.ui.theme.FridayDarkCardBorder
import com.example.ui.theme.FridayDarkSurface
import com.example.ui.theme.FridayTextPrimary
import com.example.ui.theme.FridayTextSecondary
import com.example.ui.theme.NeonEmerald
import com.example.viewmodel.FridayViewModel
import com.example.voice.VoskModelStatus

@Composable
fun SettingsScreen(
    viewModel: FridayViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val triggerPhrase by viewModel.triggerPhrase.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val userTitle by viewModel.userTitle.collectAsState()
    val autoSpeak by viewModel.autoSpeakResponse.collectAsState()

    val currentEngineType by viewModel.voiceManager.engineType.collectAsState()
    val voskStatusMessage by viewModel.voiceManager.voskEngine.statusMessage.collectAsState()
    val voskModelStatus by viewModel.voiceManager.voskEngine.modelStatus.collectAsState()
    val currentModelPath by viewModel.voiceManager.voskEngine.currentModelPath.collectAsState()

    var customTitleInput by remember { mutableStateOf(userTitle) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FridayDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_header"),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ArcCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = ArcCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "VOICE & SYSTEM PREFERENCES",
                            color = ArcCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Vosk STT, offline models, GitHub APK install & wake words",
                            color = FridayTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section 1: Offline Speech Recognition Engine (Vosk vs Native)
        item {
            Text(
                text = "OFFLINE SPEECH-TO-TEXT ENGINE (STT)",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Choose local voice decoder for zero-internet recognition:",
                        color = FridayTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    SpeechEngineType.values().forEach { engine ->
                        val isSelected = currentEngineType == engine
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ArcCyan.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    viewModel.voiceManager.setEngineType(engine)
                                    viewModel.speak("Switched to ${engine.label}.")
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("engine_option_${engine.name}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = engine.engineName,
                                        color = if (isSelected) ArcCyan else FridayTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                    if (engine == SpeechEngineType.VOSK_OFFLINE) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonEmerald.copy(alpha = 0.15f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text("AAR v0.3.47", color = NeonEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = engine.details,
                                    color = FridayTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = ArcCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vosk Status & Diagnostics Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D1117))
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (voskModelStatus == VoskModelStatus.READY) NeonEmerald else ArcCyan,
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VOSK ASR STATUS",
                                    color = ArcCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = voskStatusMessage,
                                color = FridayTextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (currentModelPath.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Model Path: $currentModelPath",
                                    color = FridayTextSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.voiceManager.voskEngine.detectAndLoadModel()
                                Toast.makeText(context, "Scanning local offline model files...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FridayDarkSurface,
                                contentColor = ArcCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Hearing, contentDescription = "Scan", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan Model", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.voiceManager.simulateVoiceInput("hey friday turn on the light")
                                Toast.makeText(context, "Tested simulated Vosk command!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ArcCyan.copy(alpha = 0.15f),
                                contentColor = ArcCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Test", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Vosk Input", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 2: Install APK On Phone & GitHub (Detailed Guide for User)
        item {
            Text(
                text = "APK VERSION & PHONE INSTALLATION (GITHUB)",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("github_apk_install_card"),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(NeonEmerald.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Smartphone, contentDescription = "Phone", tint = NeonEmerald, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "INSTALL ON YOUR PHONE",
                                    color = NeonEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "మీ ఫోన్ లో ఇన్స్టాల్ చేయడానికి (Telugu & English)",
                                    color = FridayTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("v1.0.0 (Debug APK)", color = NeonEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. AI Studio & GitHub నుండి APK ఎలా పొందాలి (How to get APK):",
                        color = ArcCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• In AI Studio: Click the top-right Settings/Export menu ➔ Select 'Download APK' or 'Push to GitHub'.\n• Built Debug APK Path in Project: \n  app/build/outputs/apk/debug/app-debug.apk\n• In GitHub: Go to your repo Actions / Releases or clone repo and build with Gradle command below.",
                        color = FridayTextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gradle Command Copy Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D1117))
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "gradle assembleDebug",
                                color = Color(0xFF58A6FF),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("gradle assembleDebug"))
                                    Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ArcCyan, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "2. ఫోన్ లో ఇన్స్టాలేషన్ విధానం (Install on Android Device):",
                        color = ArcCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Transfer 'app-debug.apk' to your phone via USB, WhatsApp, Telegram, or Google Drive.\n2. Open 'Files' or 'Downloads' on your phone and tap 'app-debug.apk'.\n3. If prompted 'Install unknown apps', tap Settings ➔ Turn ON permission for Files/Chrome.\n4. Tap 'Install' ➔ Open FRIDAY.\n5. Allow Microphone permission ➔ Ready to use 100% offline without internet or WiFi!",
                        color = FridayTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section 3: Customizable Trigger Phrase
        item {
            Text(
                text = "CUSTOMIZABLE TRIGGER PHRASE",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Select wake word for hands-free offline listening:",
                        color = FridayTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    TriggerPhraseOption.values().forEach { option ->
                        val isSelected = triggerPhrase == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ArcCyan.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    viewModel.setTriggerPhrase(option)
                                    viewModel.speak("Wake word updated to ${option.label}.")
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("trigger_option_${option.name}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (isSelected) ArcCyan else FridayTextSecondary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "\"${option.label}\"",
                                    color = if (isSelected) ArcCyan else FridayTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = ArcCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: User Title / Name
        item {
            Text(
                text = "USER TITLE & GREETING NAME",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "How FRIDAY should address you in spoken responses:",
                        color = FridayTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customTitleInput,
                            onValueChange = { customTitleInput = it },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_title_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArcCyan,
                                unfocusedBorderColor = FridayDarkCardBorder,
                                focusedTextColor = FridayTextPrimary,
                                unfocusedTextColor = FridayTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customTitleInput.isNotBlank()) {
                                    viewModel.setUserTitle(customTitleInput)
                                    viewModel.speak("Greeting title set to $customTitleInput.")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ArcCyan,
                                contentColor = FridayDarkBg
                            )
                        ) {
                            Text("SAVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section 5: Multi-Language Support
        item {
            Text(
                text = "MULTI-LANGUAGE VOICE INTERACTION",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = selectedLanguage == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonEmerald.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    viewModel.setLanguage(lang)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("lang_option_${lang.code}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = lang.label,
                                    color = if (isSelected) NeonEmerald else FridayTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = lang.greetingText,
                                    color = FridayTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 6: Voice Synthesis & Audio Settings
        item {
            Text(
                text = "VOICE SYNTHESIS SETTINGS",
                color = FridayTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-Speak FRIDAY Responses",
                                color = FridayTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Reads responses aloud via offline TTS engine",
                                color = FridayTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = autoSpeak,
                            onCheckedChange = { viewModel.toggleAutoSpeak() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FridayDarkBg,
                                checkedTrackColor = ArcCyan,
                                uncheckedThumbColor = FridayTextSecondary,
                                uncheckedTrackColor = FridayDarkSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.speak("Testing FRIDAY offline voice synthesis. Pitch 1.05, speed 1.0. All local systems operational.")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FridayDarkSurface,
                            contentColor = ArcCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Test", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Voice Audio Sample", fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 7: Security & Protocol Architecture Info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090E1A)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B263B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURITY & PROTOCOL COMPLIANCE",
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Vosk Offline Decoder: v0.3.47 (Local CPU Inference)\n• Symmetric Cipher: AES-256-GCM (128-bit Auth Tag, 12-byte IV)\n• Transport: Local UDP/CoAP over Isolated Subnet (192.168.x.x)\n• Telemetry & Analytics: Disabled (100% Offline Air-Gapped)\n• Storage: Local Sandboxed Storage Only",
                        color = FridayTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
