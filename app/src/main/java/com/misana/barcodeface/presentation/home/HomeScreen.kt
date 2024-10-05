package com.misana.barcodeface.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import com.misana.barcodeface.CameraProvider
import com.misana.barcodeface.R
import com.misana.barcodeface.TakePictureSelfieContract
import com.misana.barcodeface.domain.model.Barcode
import com.misana.barcodeface.domain.model.ListViewType
import com.misana.barcodeface.domain.service.toLocalTimeString
import com.misana.barcodeface.presentation.home.barcodes.BarcodeGridItem
import com.misana.barcodeface.presentation.home.barcodes.BarcodeListItem
import com.misana.barcodeface.presentation.home.barcodes.BarcodeLoadingGridItem
import com.misana.barcodeface.presentation.home.barcodes.BarcodeLoadingListItem
import com.misana.barcodeface.presentation.settings.SettingsViewModel
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeScreen(
    homeVm: HomeViewModel,
    settingsVm: SettingsViewModel,
    onDrawerClick: () -> Unit
) {
    val uiScope = rememberCoroutineScope()
    val launchCamera = cameraLauncher(
        onCameraResult = { res -> homeVm.onCameraResult(res) },
        setCameraFile = { file -> homeVm.setCameraFile(file) }
    )
    AppScaffold(
        settingsVm = settingsVm,
        snackbarState = homeVm.snackbar,
        onDrawerClick = onDrawerClick,
        onFabClick = {
            if (!homeVm.nextBarcode && homeVm.onePerHour()) {
                launchCamera()
            }
        },
        onShareClick = {
            if (homeVm.barcodes.isNotEmpty()) {
                homeVm.setShowPackage(true)
            } else {
                uiScope.launch {
                    homeVm.snackbar.showSnackbar(
                        message = "No barcodes to share",
                        withDismissAction = true
                    )
                }
            }
        }
    ) {
        AppList(homeVm, settingsVm.settings.listViewType)
        if (homeVm.showPackageDialog) {
            PackageDialog(homeVm = homeVm)
        }
    }
}

@Composable
fun cameraLauncher(
    onCameraResult: (Boolean) -> Unit,
    setCameraFile: (File) -> Unit
): () -> Unit {
    val uiScope = rememberCoroutineScope()
    val cameraLauncher = rememberLauncherForActivityResult(
        TakePictureSelfieContract()
    ) { result ->
        uiScope.launch {
            onCameraResult(result)
        }
    }
    val context = LocalContext.current
    return {
        val file = CameraProvider.getImageFile(context)
        val uri = CameraProvider.fileToContentUri(file, context)
        setCameraFile(file)
        cameraLauncher.launch(uri)
    }
}

@Composable
private fun PackageDialog(
    homeVm: HomeViewModel
) {
    val uiScope = rememberCoroutineScope()
    var packageData by remember {
        mutableStateOf("")
    }

    LaunchedEffect(true) {
        uiScope.launch {
            delay(1000)
            packageData = homeVm.getPackageData()
        }
    }

    Dialog(
        onDismissRequest = { homeVm.setShowPackage(false) }
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.75f)
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (packageData.isNotEmpty()) {
                    Image(
                        painter = rememberQrCodePainter(packageData),
                        contentDescription = "Packages barcodes",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                }
                Text(
                    text = "Scan this QR code on the second app to see your results.",
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
                TextButton(
                    onClick = { homeVm.setShowPackage(false) }
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}

@Composable
private fun AppList(
    homeVm: HomeViewModel,
    listViewType: ListViewType
) {
    if (homeVm.showBottomSheet) {
        AppBottomSheet(
            homeVm = homeVm,
            barcodeId = homeVm.selectedBarcodeId
        )
    }

    val lazyGridState = rememberLazyGridState()
    val uiScope = rememberCoroutineScope()
    val isTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
                    lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(homeVm.nextBarcode) {
        if (homeVm.nextBarcode) {
            uiScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    Box {
        ScrollToTop(
            isTop = isTop,
            onClick = {
                uiScope.launch {
                    lazyGridState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = (-16).dp,
                    y = (-16).dp
                )
                .zIndex(1f)
        )
        AllBarcodes(homeVm, listViewType, lazyGridState)
    }
}

@Composable
fun ScrollToTop(
    isTop: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isTop,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .alpha(0.92f)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .clickable { onClick() }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_upward),
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = "Scroll to top",
            )
        }
    }
}

@Composable
private fun AllBarcodes(
    homeVm: HomeViewModel,
    listViewType: ListViewType,
    lazyGridState: LazyGridState
) {
    val uiScope = rememberCoroutineScope()

    val gridListView = when (listViewType) {
        ListViewType.GRID -> GridCells.Adaptive(minSize = 120.dp)
        ListViewType.LIST -> GridCells.Fixed(1)
    }

    fun onDelete(barcode: Barcode) {
        homeVm.deleteBarcode(barcode)
        uiScope.launch {
            homeVm.snackbar.showSnackbar(
                message = "Barcode deleted",
                withDismissAction = true
            )
        }
    }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = gridListView
    ) {
        if (listViewType == ListViewType.GRID) {
            if (homeVm.nextBarcode) {
                item(key = -1) {
                    BarcodeLoadingGridItem(
                        onCancel = { homeVm.cancelNextBarcode() }
                    )
                }
            }
            items(
                items = homeVm.barcodes,
                key = { barcode -> barcode.id }
            ) { barcode ->
                BarcodeGridItem(
                    barcode = barcode,
                    onClick = {
                        homeVm.setSelectedBarcode(barcode.id)
                        homeVm.setShowSheet(true)
                    },
                    onDelete = { onDelete(barcode) }
                )
            }
        } else {
            if (homeVm.nextBarcode) {
                item(key = -1) {
                    BarcodeLoadingListItem(
                        onCancel = { homeVm.cancelNextBarcode() }
                    )
                }
            }
            items(
                items = homeVm.barcodes,
                key = { barcode -> barcode.id }
            ) { barcode ->
                BarcodeListItem(
                    homeVm = homeVm,
                    barcode = barcode,
                    onClick = {
                        homeVm.setSelectedBarcode(barcode.id)
                        homeVm.setShowSheet(true)
                    },
                    onDelete = { onDelete(barcode) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBottomSheet(
    homeVm: HomeViewModel,
    barcodeId: Int
) {
    var barcode by remember {
        mutableStateOf(Barcode("LOADING"))
    }

    LaunchedEffect(barcodeId) {
        val fetch = homeVm.getBarcode(barcodeId)
        if (fetch != null) {
            barcode = fetch
        } else {
            homeVm.snackbar.showSnackbar(
                message = "Error fetching barcode",
                withDismissAction = true
            )
        }
    }

    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { homeVm.setShowSheet(false) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        sheetMaxWidth = 500.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 18.dp)
                .padding(horizontal = 18.dp)
                .fillMaxWidth()
        ) {
            if (barcode.id == 0) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
            } else {
                Image(
                    painter = rememberQrCodePainter(barcode.content),
                    contentDescription = "Selected Barcode",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(2f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ID: ")
                        Text(
                            text = barcode.id.toString(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Emotion: ")
                        Text(
                            text = homeVm.getEmotion(barcode).toString(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Time: ")
                        Text(
                            text = barcode.timestamp.toLocalTimeString(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
