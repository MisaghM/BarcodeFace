package com.misana.barcodeface.presentation.home.barcodes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.ui.theme.SideGradient

internal const val alpha = 0.85f
internal const val newMinutes = 1L
internal val newModifier = Modifier
    .scale(1.02f)
    .border(BorderStroke(4.dp, Brush.linearGradient(SideGradient)))
    .padding(4.dp)
