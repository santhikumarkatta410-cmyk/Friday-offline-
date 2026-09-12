package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AssistantTab
import com.example.ui.screens.AppLauncherScreen
import com.example.ui.screens.OfflineBrainScreen
import com.example.ui.screens.SmartHomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.FridayDarkBg
import com.example.ui.theme.FridayDarkCard
import com.example.ui.theme.FridayDarkCardBorder
import com.example.ui.theme.FridayDarkSurface
import com.example.ui.theme.FridayTextPrimary
import com.example.ui.theme.FridayTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonEmerald
import com.example.viewmodel.FridayViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: FridayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                FridayApp(
                    viewModel = viewModel,
                    onLaunchApp = { packageName ->
                        try {
                            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                            if (launchIntent != null) {
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(launchIntent)
                            } else {
                                Toast.makeText(this, "Application package not installed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Unable to launch app: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridayApp(
    viewModel: FridayViewModel,
    onLaunchApp: (String) -> Unit
) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission needed for offline voice recognition", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.appLaunchEvent.collectLatest { pkg ->
            onLaunchApp(pkg)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FridayDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ArcCyan.copy(alpha = 0.2f))
                                .border(1.5.dp, ArcCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(ArcCyan, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "F.R.I.D.A.Y.",
                                color = FridayTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "OFFLINE VOICE INTELLIGENCE",
                                color = ArcCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonEmerald.copy(alpha = 0.15f))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AIR-GAPPED",
                            color = NeonEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FridayDarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = FridayDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(width = 0.5.dp, color = FridayDarkCardBorder)
                    .testTag("friday_bottom_navigation")
            ) {
                val navItems = listOf(
                    Triple(AssistantTab.VOICE, Icons.Default.Mic, "Voice"),
                    Triple(AssistantTab.SMART_HOME, Icons.Default.Home, "Home"),
                    Triple(AssistantTab.OFFLINE_BRAIN, Icons.Default.Memory, "Brain"),
                    Triple(AssistantTab.APPS, Icons.Default.Apps, "Apps"),
                    Triple(AssistantTab.SETTINGS, Icons.Default.Settings, "Settings")
                )

                navItems.forEach { (tab, icon, label) ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ArcCyan,
                            selectedTextColor = ArcCyan,
                            indicatorColor = ArcCyan.copy(alpha = 0.15f),
                            unselectedIconColor = FridayTextSecondary,
                            unselectedTextColor = FridayTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                AssistantTab.VOICE -> VoiceAssistantScreen(viewModel = viewModel)
                AssistantTab.SMART_HOME -> SmartHomeScreen(viewModel = viewModel)
                AssistantTab.OFFLINE_BRAIN -> OfflineBrainScreen(viewModel = viewModel)
                AssistantTab.APPS -> AppLauncherScreen(viewModel = viewModel)
                AssistantTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Kept for Robolectric screenshot test backward compatibility
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! FRIDAY Offline Systems Active.",
        modifier = modifier,
        color = FridayTextPrimary
    )
}
