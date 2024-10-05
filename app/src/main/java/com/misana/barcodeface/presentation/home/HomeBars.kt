package com.misana.barcodeface.presentation.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.R
import com.misana.barcodeface.presentation.settings.SettingsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    settingsVm: SettingsViewModel,
    snackbarState: SnackbarHostState,
    onDrawerClick: () -> Unit,
    onFabClick: () -> Unit,
    onShareClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = { AppTopBar(scrollBehavior, onDrawerClick, settingsVm) },
        bottomBar = { AppBottomBar(onFabClick, onShareClick) },
        snackbarHost = { SwipeableSnackbar(snackbarState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableSnackbar(
    snackbarState: SnackbarHostState
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                snackbarState.currentSnackbarData?.dismiss()
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {}
    ) {
        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
        ) {
            Snackbar(
                snackbarData = it,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onDrawerClick: () -> Unit,
    settingsVm: SettingsViewModel
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            Text(
                text = "BarcodeFace",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = { AppTopBarActions(settingsVm) },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AppTopBarActions(
    settingsVm: SettingsViewModel
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    IconButton(onClick = { menuOpen = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More options"
        )
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false }
        ) {
            ListViewMenuItem.items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.listViewType.toString()) },
                    onClick = {
                        settingsVm.setListViewType(item.listViewType)
                        menuOpen = false
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.listViewType.name
                        )
                    },
                    trailingIcon = {
                        if (settingsVm.settings.listViewType == item.listViewType) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected"
                            )
                        }
                    },
                    modifier = Modifier.widthIn(150.dp)
                )
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    onFabClick: () -> Unit,
    onShareClick: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(R.drawable.package_2),
                    contentDescription = "Send",
                    modifier = Modifier.size(42.dp)
                )
            }
        },
        floatingActionButton = { AppFab(onFabClick) },
    )
}

@Composable
private fun AppFab(
    onFabClick: () -> Unit
) {
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var fabExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var fabAnimationDone by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(400)
        fabExpanded = isLandscape
        if (!fabAnimationDone) {
            if (!isLandscape) {
                fabExpanded = true
                delay(1200)
                fabExpanded = false
            }
            fabAnimationDone = true
        }
    }

    ExtendedFloatingActionButton(
        expanded = fabExpanded,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        onClick = onFabClick,
        icon = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        },
        text = {
            Text("New Picture")
        }
    )
}
