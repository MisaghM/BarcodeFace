package com.misana.barcodeface.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.R
import com.misana.barcodeface.domain.model.ThemeSelect
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsVm: SettingsViewModel,
    onScreenClose: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = { SettingsTopBar(scrollBehavior, onScreenClose) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
        ) {
            SettingsContent(settingsVm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onScreenClose: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = { Text("Settings") },
        navigationIcon = {
            IconButton(onClick = onScreenClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Menu"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun SettingsContent(
    settingsVm: SettingsViewModel
) {
    var dialogOpen by remember {
        mutableStateOf(false)
    }

    if (dialogOpen) {
        AppAlertDialog(
            title = "Delete all data?",
            text = "This action is not revertible and all user data will be lost.",
            icon = Icons.Filled.Warning,
            onDismiss = {
                dialogOpen = false
            },
            onConfirm = {
                dialogOpen = false
                settingsVm.sendResetRequest()
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        ProvidePreferenceTheme {
            SwitchPreference(
                value = settingsVm.settings.enableNotifications,
                onValueChange = { settingsVm.setNotifications(it) },
                enabled = true,
                title = { Text("Enable Notifications") },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Enable Notifications"
                    )
                },
                summary = { Text(if (settingsVm.settings.enableNotifications) "On" else "Off") }
            )
            ListPreference(
                value = settingsVm.settings.theme,
                onValueChange = { settingsVm.setThemeScheme(it) },
                values = ThemeSelect.entries,
                title = { Text("Select Theme") },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.palette),
                        contentDescription = "Select Theme"
                    )
                },
                summary = {
                    Text(settingsVm.settings.theme.toString())
                }
            )
            Preference(
                title = { Text("Clear Data") },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Clear Data"
                    )
                },
                summary = {
                    Text("Delete all user data")
                },
                onClick = {
                    dialogOpen = true
                }
            )
            HealthCheck(settingsVm)
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
                    .alpha(0.6f)
            ) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = "About")
                Text(
                    text = "Made by MisaghM",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
private fun HealthCheck(
    settingsVm: SettingsViewModel
) {
    val uiScope = rememberCoroutineScope()
    val context = LocalContext.current

    var resultArrived by remember {
        mutableStateOf(true)
    }

    Preference(
        title = { Text("Check Server Connection") },
        icon = {
            Icon(
                painter = painterResource(R.drawable.monitor_heart),
                contentDescription = "Server Health"
            )
        },
        summary = {
            Text("Run health check on server")
        },
        onClick = {
            uiScope.launch {
                resultArrived = false
                val result = settingsVm.checkServerConnectivity()
                val message = "Server connection " + if (result) "successful." else "failed."
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                resultArrived = true
            }
        },
        enabled = resultArrived
    )
}

@Composable
private fun AppAlertDialog(
    title: String,
    text: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        icon = { Icon(imageVector = icon, contentDescription = null) },
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
        modifier = modifier
    )
}
