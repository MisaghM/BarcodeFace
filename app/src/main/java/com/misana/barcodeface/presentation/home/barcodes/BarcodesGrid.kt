package com.misana.barcodeface.presentation.home.barcodes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.R
import com.misana.barcodeface.domain.model.Barcode
import com.misana.barcodeface.domain.service.isBeforeMinutes
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarcodeGridItem(
    barcode: Barcode,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    val newItemModifier = if (barcode.timestamp.isBeforeMinutes(newMinutes)) Modifier else newModifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { menuOpen = true }
            )
            .padding(12.dp)
    ) {
        Image(
            painter = rememberQrCodePainter(barcode.content),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.inverseSurface.copy(alpha = alpha)
            ),
            modifier = Modifier
                .fillMaxSize()
                .then(newItemModifier),
        )
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = onDelete
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarcodeLoadingGridItem(
    onCancel: () -> Unit = {}
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .combinedClickable(
                onClick = { },
                onLongClick = { menuOpen = true }
            )
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f)
        )
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false }
        ) {
            DropdownMenuItem(
                text = { Text("Cancel") },
                onClick = onCancel
            )
        }
    }
}
