package com.misana.barcodeface.domain.service

import android.content.Context
import android.graphics.Bitmap
import com.misana.barcodeface.CameraProvider
import com.misana.barcodeface.data.repository.BarcodeRepository
import com.misana.barcodeface.data.repository.FerRepository
import com.misana.barcodeface.domain.model.Barcode
import com.misana.barcodeface.domain.model.Emotion
import com.misana.barcodeface.domain.model.NetworkResult
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.EnumMap

class BarcodeService(
    private val context: Context,
    private val barcodeRepo: BarcodeRepository,
    private val ferRepo: FerRepository
) {
    fun getBarcodes() = barcodeRepo.getBarcodes().map { barcodes ->
        barcodes.sortedByDescending { it.timestamp }
    }

    suspend fun getBarcodeById(id: Int) = barcodeRepo.getBarcodeById(id)
    suspend fun deleteBarcode(barcode: Barcode) = barcodeRepo.deleteBarcode(barcode)
    suspend fun insertBarcode(barcode: Barcode) = barcodeRepo.insertBarcode(barcode)

    suspend fun addBarcode(cameraFile: File): NetworkResult<String> {
        val (width, height) = CameraProvider.getImageDimensions(cameraFile)
        val savedFile = File(context.cacheDir, CameraProvider.CAMERA_SAVED_FILE)
        val compressedFile = Compressor.compress(context, cameraFile) {
            default(
                width = 600,
                height = 600 * (height / width),
                format = Bitmap.CompressFormat.JPEG,
                quality = 80
            )
            destination(savedFile)
        }
        val result = ferRepo.detect("barcodeface_app", compressedFile)
        if (result.status == NetworkResult.Status.SUCCESS) {
            if (result.body != "none") {
                insertBarcode(Barcode(result.body!!))
            }
        }
        return result
    }

    suspend fun getPackageData(): String {
        val list = barcodeRepo.getBarcodes().first()
        val x = EnumMap(Emotion.entries.associateWith { 0 })
        for (barcode in list) {
            val emotion = barcodeToEmotion(barcode)
            x[emotion] = x[emotion]!! + 1
        }
        return x.toString()
    }

    fun barcodeToEmotion(barcode: Barcode): Emotion {
        return when (barcode.content) {
            "A" -> Emotion.ANGRY
            "N" -> Emotion.NEUTRAL
            "D" -> Emotion.DISGUST
            "F" -> Emotion.FEAR
            "H" -> Emotion.HAPPY
            "S" -> Emotion.SAD
            "U" -> Emotion.SURPRISE
            else -> Emotion.NEUTRAL
        }
    }
}
