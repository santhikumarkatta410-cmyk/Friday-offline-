package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FridayAiOrb
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.FridayDarkBg
import com.example.ui.theme.FridayDarkCard
import com.example.ui.theme.FridayDarkCardBorder
import com.example.ui.theme.FridayDarkSurface
import com.example.ui.theme.FridayTextPrimary
import com.example.ui.theme.FridayTextSecondary
import com.example.ui.theme.NeonEmerald
import com.example.viewmodel.FridayViewModel

@Composable
fun VoiceAssistantScreen(
    viewModel: FridayViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val amplitude by viewModel.voiceManager.amplitude.collectAsState()
    val partialVoiceText by viewModel.voiceManager.partialText.collectAsState()
    val currentEngineType by viewModel.voiceManager.engineType.collectAsState()

    val currentSpeech by viewModel.currentFridaySpeech.collectAsState()
    val lastQuery by viewModel.lastQuery.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val userTitle by viewModel.userTitle.collectAsState()
    val triggerPhrase by viewModel.triggerPhrase.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val lastMessage = messages.lastOrNull { !it.isUser }

    val quickActionChips = listOf(
        "🇮🇳 What is today news in India?",
        "💡 Turn on Living Room Light",
        "❄️ Set AC to 22",
        "🔒 Lock front door",
        "💻 Hey Friday create on some code",
        "🛡️ WiFi password security",
        "📱 Open Camera",
        "🔋 Battery status",
        "⏰ What time is it?",
        "✨ Anta reddy"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FridayDarkBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. Offline Minimalist Badge & Status Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonEmerald.copy(alpha = 0.15f))
                    .border(1.dp, NeonEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(NeonEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% OFFLINE • AIR-GAPPED",
                        color = NeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArcCyan.copy(alpha = 0.12f))
                    .border(1.dp, ArcCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable {
                        val nextEngine = if (currentEngineType == com.example.model.SpeechEngineType.VOSK_OFFLINE) {
                            com.example.model.SpeechEngineType.SYSTEM_OFFLINE
                        } else {
                            com.example.model.SpeechEngineType.VOSK_OFFLINE
                        }
                        viewModel.voiceManager.setEngineType(nextEngine)
                        Toast.makeText(context, "STT Engine: ${nextEngine.label}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(ArcCyan, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentEngineType.label,
                        color = ArcCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Central Glowing FRIDAY AI Orb
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            FridayAiOrb(
                isListening = isListening,
                isSpeaking = isSpeaking,
                amplitude = amplitude,
                onClick = {
                    if (isListening) {
                        viewModel.voiceManager.stopListening()
                    } else {
                        viewModel.voiceManager.startListening(selectedLanguage)
                    }
                }
            )
        }

        // 3. Orb State Label
        Text(
            text = when {
                isListening -> "LISTENING TO $userTitle..."
                isSpeaking -> "FRIDAY IS SPEAKING..."
                else -> "TAP ORB OR SAY \"${triggerPhrase.label}\""
            },
            color = if (isListening) ArcCyan else if (isSpeaking) NeonEmerald else FridayTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        // 4. Live Heard Speech Transcript Banner
        AnimatedVisibility(
            visible = isListening && partialVoiceText.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArcCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Live voice",
                        tint = ArcCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\"$partialVoiceText\"",
                        color = FridayTextPrimary,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. FRIDAY Response Card (Minimalist HUD)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("friday_response_card"),
            colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FridayDarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FRIDAY BRAIN",
                            color = ArcCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Encrypted",
                            tint = NeonEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.voiceManager.stopSpeaking()
                                } else {
                                    viewModel.speak(currentSpeech)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Speak response",
                                tint = if (isSpeaking) NeonEmerald else ArcCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(currentSpeech))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = FridayTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (lastQuery.isNotBlank()) {
                    Text(
                        text = "Query: \"$lastQuery\"",
                        color = FridayTextSecondary,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = currentSpeech,
                    color = FridayTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Render Offline Generated Code Block if present
                if (lastMessage?.codeSnippet != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D1117))
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lastMessage.codeLang.uppercase(),
                                    color = ArcCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(lastMessage.codeSnippet))
                                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = ArcCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = lastMessage.codeSnippet,
                                color = Color(0xFF58A6FF),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Quick Action Suggestion Chips (Minimalist, easy for household members)
        Text(
            text = "HOUSEHOLD VOICE SHORTCUTS",
            color = FridayTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(quickActionChips) { chipText ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(FridayDarkSurface)
                        .border(1.dp, FridayDarkCardBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            val cleanQuery = chipText.substringAfter(" ").trim()
                            viewModel.processQuery(cleanQuery)
                        }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = chipText,
                        color = FridayTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 7. Manual Text Input Bar (Alternative for quiet or offline use)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text("Type command (e.g., news india, open camera)...", color = FridayTextSecondary, fontSize = 13.sp)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("voice_text_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FridayDarkCard,
                    unfocusedContainerColor = FridayDarkCard,
                    focusedBorderColor = ArcCyan,
                    unfocusedBorderColor = FridayDarkCardBorder,
                    focusedTextColor = FridayTextPrimary,
                    unfocusedTextColor = FridayTextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.processQuery(textInput)
                            textInput = ""
                        }
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.processQuery(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(ArcCyan, CircleShape)
                    .testTag("send_query_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Command",
                    tint = FridayDarkBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
