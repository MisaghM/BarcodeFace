package com.misana.barcodeface.presentation.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.misana.barcodeface.R
import com.misana.barcodeface.presentation.navigation.Screen
import com.misana.barcodeface.ui.theme.HalfGrey

const val drawerWidth = 300

@Composable
fun AppNavDrawer(
    navController: NavController,
    drawerVm: DrawerViewModel,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerVm.drawerState,
        gesturesEnabled = true,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(drawerWidth.dp)
            ) {
                DrawerTop()
                HorizontalDivider(thickness = 4.dp)
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AppDrawerItem(
                        item = DrawerItem.homeItem,
                        selected = drawerVm.drawerSelected == DrawerItem.Index.HOME,
                        onClick = {
                            drawerVm.setDrawerSelect(DrawerItem.Index.HOME)
                            drawerVm.switchDrawer(uiScope)
                        }
                    )
                    AppDrawerItem(
                        item = DrawerItem.settingsItem,
                        selected = drawerVm.drawerSelected == DrawerItem.Index.SETTINGS,
                        onClick = {
                            drawerVm.setDrawerSelect(DrawerItem.Index.SETTINGS)
                            drawerVm.switchDrawer(uiScope)
                            navController.navigate(Screen.Settings)
                        }
                    )
                    AppDrawerItem(
                        item = DrawerItem.exitItem,
                        selected = drawerVm.drawerSelected == DrawerItem.Index.EXIT,
                        onClick = {
                            drawerVm.setDrawerSelect(DrawerItem.Index.EXIT)
                            drawerVm.sendExitRequest()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun DrawerTop() {
    Box(
        modifier = Modifier
            .clipToBounds()
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.largeqr),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(HalfGrey),
            alpha = 0.1f,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = 1.2f,
                    scaleY = 1.2f
                )
        )
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "BarcodeFace Logo",
                    modifier = Modifier
                        .fillMaxHeight()
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                )
            }
            Text(
                text = "BarcodeFace",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AppDrawerItem(
    item: DrawerItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedBadgeColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedTextColor = MaterialTheme.colorScheme.onSecondary,
            selectedIconColor = MaterialTheme.colorScheme.onSecondary,
            selectedBadgeColor = MaterialTheme.colorScheme.onSecondary
        ),
        label = { Text(item.title) },
        icon = {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.iconDesc
            )
        },
        badge = {
            if (item.badge != null) {
                Text(item.badge.toString())
            }
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
}
