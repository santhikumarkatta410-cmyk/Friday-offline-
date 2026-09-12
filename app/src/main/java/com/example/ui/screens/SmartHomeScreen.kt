package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SmartDevice
import com.example.model.SmartDeviceType
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.CrimsonLock
import com.example.ui.theme.FridayDarkBg
import com.example.ui.theme.FridayDarkCard
import com.example.ui.theme.FridayDarkCardBorder
import com.example.ui.theme.FridayDarkSurface
import com.example.ui.theme.FridayTextPrimary
import com.example.ui.theme.FridayTextSecondary
import com.example.ui.theme.NeonEmerald
import com.example.viewmodel.FridayViewModel

@Composable
fun SmartHomeScreen(
    viewModel: FridayViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsState()
    val networkLogs by viewModel.networkLogs.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FridayDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Security & Encrypted IoT Protocol Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("encrypted_iot_banner"),
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(NeonEmerald.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Encrypted Local Network",
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ENCRYPTED LOCAL IOT",
                                    color = NeonEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "AES-256-GCM • Zero Cloud Leakage",
                                    color = FridayTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FridayDarkSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "192.168.1.0/24",
                                color = ArcCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Master Control Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.processQuery("turn on all lights") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("all_lights_on_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ArcCyan.copy(alpha = 0.18f),
                                contentColor = ArcCyan
                            )
                        ) {
                            Text("All Lights ON", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { viewModel.processQuery("turn off all lights") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("all_lights_off_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FridayDarkSurface,
                                contentColor = FridayTextSecondary
                            )
                        ) {
                            Text("All Lights OFF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "HOUSEHOLD APPLIANCES (${devices.size})",
                color = FridayTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        items(devices, key = { it.id }) { device ->
            DeviceControlCard(
                device = device,
                onToggle = { viewModel.toggleDevice(device.id) },
                onLevelChange = { newLevel -> viewModel.setDeviceLevel(device.id, newLevel) }
            )
        }

        item {
            // Live Encrypted Packet Dispatch Stream Log
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B101C)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B283E))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURE PACKET DISPATCH MONITOR",
                            color = ArcCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "LOCAL UDP",
                            color = FridayTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (networkLogs.isEmpty()) {
                        Text(
                            text = "Waiting for device commands...",
                            color = FridayTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        networkLogs.take(4).forEach { logLine ->
                            Text(
                                text = logLine,
                                color = if (logLine.contains("AES-256")) NeonEmerald else FridayTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DeviceControlCard(
    device: SmartDevice,
    onToggle: () -> Unit,
    onLevelChange: (Int) -> Unit
) {
    val icon: ImageVector = when (device.type) {
        SmartDeviceType.LIGHT -> Icons.Default.Lightbulb
        SmartDeviceType.FAN -> Icons.Default.WindPower
        SmartDeviceType.THERMOSTAT -> Icons.Default.AcUnit
        SmartDeviceType.LOCK -> if (device.isOn) Icons.Default.Lock else Icons.Default.LockOpen
        SmartDeviceType.PLUG -> Icons.Default.PowerSettingsNew
        SmartDeviceType.TELEVISION -> Icons.Default.Tv
    }

    val activeColor = when (device.type) {
        SmartDeviceType.LOCK -> if (device.isOn) CrimsonLock else NeonEmerald
        else -> if (device.isOn) ArcCyan else FridayTextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("device_card_${device.id}"),
        colors = CardDefaults.cardColors(containerColor = FridayDarkCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (device.isOn) activeColor.copy(alpha = 0.4f) else FridayDarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(activeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = device.name,
                            tint = activeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = device.name,
                            color = FridayTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device.room,
                                color = FridayTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(text = " • ", color = FridayTextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${device.localIp}:${device.port}",
                                color = FridayTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Switch or Toggle
                if (device.type == SmartDeviceType.LOCK) {
                    Button(
                        onClick = onToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (device.isOn) CrimsonLock.copy(alpha = 0.2f) else NeonEmerald.copy(alpha = 0.2f),
                            contentColor = if (device.isOn) CrimsonLock else NeonEmerald
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("lock_toggle_${device.id}")
                    ) {
                        Text(
                            text = if (device.isOn) "LOCKED" else "UNLOCKED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Switch(
                        checked = device.isOn,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = FridayDarkBg,
                            checkedTrackColor = activeColor,
                            uncheckedThumbColor = FridayTextSecondary,
                            uncheckedTrackColor = FridayDarkSurface
                        ),
                        modifier = Modifier.testTag("switch_${device.id}")
                    )
                }
            }

            // Secondary sliders for dimming, temperature, fan speed
            if (device.isOn) {
                Spacer(modifier = Modifier.height(12.dp))

                when (device.type) {
                    SmartDeviceType.LIGHT -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Brightness: ${device.level}%",
                                color = FridayTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.width(100.dp)
                            )
                            Slider(
                                value = device.level.toFloat(),
                                onValueChange = { onLevelChange(it.toInt()) },
                                valueRange = 10f..100f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = ArcCyan,
                                    activeTrackColor = ArcCyan,
                                    inactiveTrackColor = FridayDarkSurface
                                )
                            )
                        }
                    }
                    SmartDeviceType.FAN -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Speed Level: ${device.level}",
                                color = FridayTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.width(100.dp)
                            )
                            Slider(
                                value = device.level.toFloat(),
                                onValueChange = { onLevelChange(it.toInt()) },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = ArcCyan,
                                    activeTrackColor = ArcCyan,
                                    inactiveTrackColor = FridayDarkSurface
                                )
                            )
                        }
                    }
                    SmartDeviceType.THERMOSTAT -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cooling: ${device.level}°C",
                                color = ArcCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (device.level > 16) onLevelChange(device.level - 1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(FridayDarkSurface, RoundedCornerShape(6.dp))
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Temp Down", tint = FridayTextPrimary, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { if (device.level < 30) onLevelChange(device.level + 1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(FridayDarkSurface, RoundedCornerShape(6.dp))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Temp Up", tint = FridayTextPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }

            if (device.lastEncryptedPacket.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Packet: ${device.lastEncryptedPacket}",
                    color = FridayTextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
