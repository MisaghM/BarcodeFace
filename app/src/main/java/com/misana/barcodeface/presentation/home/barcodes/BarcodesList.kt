package com.misana.barcodeface.presentation.home.barcodes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.R
import com.misana.barcodeface.domain.model.Barcode
import com.misana.barcodeface.domain.service.isBeforeMinutes
import com.misana.barcodeface.presentation.home.HomeViewModel
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun BarcodeListItem(
    homeVm: HomeViewModel,
    barcode: Barcode,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val newItemModifier = if (barcode.timestamp.isBeforeMinutes(newMinutes)) Modifier else newModifier

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Image(
                painter = rememberQrCodePainter(barcode.content),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.inverseSurface.copy(alpha = alpha)
                ),
                modifier = Modifier.size(100.dp).then(newItemModifier)
            )
            VerticalDivider()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(x = (-8).dp)
                ) {
                    Text("Barcoded Emotion:")
                    Text(homeVm.getEmotion(barcode).toString())
                }
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(8.dp, 8.dp)
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun BarcodeLoadingListItem(
    onCancel: () -> Unit = {}
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable {}
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier.size(100.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.2f)
                )
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            VerticalDivider()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text("Loading...")
                TextButton(
                    onClick = onCancel,
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(8.dp, 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
