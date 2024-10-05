package com.misana.barcodeface

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class TakePictureSelfieContract : ActivityResultContracts.TakePicture() {
    override fun createIntent(context: Context, input: Uri): Intent {
        super.createIntent(context, input)
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, input)
            .putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
            .putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            .putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            .putExtra("android.intent.extras.CAMERA_FACING", 1)
            .putExtra("camerafacing", "front")
            .putExtra("previous_mode", "Selfie")
    }
}

class CameraProvider : FileProvider(R.xml.fileprovider_paths) {
    companion object {
        const val CAMERA_CACHE_FILE = "selected.jpg"
        const val CAMERA_SAVED_FILE = "selected_saved.jpg"

        fun getImageFile(context: Context): File {
            val directory = File(context.cacheDir, "camera")
            directory.mkdirs()
            return File(directory, CAMERA_CACHE_FILE)
        }

        fun fileToContentUri(file: File, context: Context): Uri {
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(context, authority, file)
        }

        fun contentUriToFile(context: Context, uri: Uri, filename: String): File? {
            var inputStream: InputStream? = null
            val outputFile = File(context.cacheDir, filename)
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                val bufferedIs = BufferedInputStream(inputStream)

                val bufferedOs = BufferedOutputStream(FileOutputStream(outputFile))
                val buffer = ByteArray(1024)

                while (true) {
                    val readBytes = bufferedIs.read(buffer)
                    if (readBytes == -1) {
                        bufferedOs.flush()
                        break
                    }
                    bufferedOs.write(buffer)
                    bufferedOs.flush()
                }
                bufferedIs.close()
                bufferedOs.close()
            } catch (e: Exception) {
                return null
            } finally {
                inputStream?.close()
            }
            return outputFile
        }

        fun getImageDimensions(file: File): Pair<Int, Int> {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(file.absolutePath, options)
                return Pair(options.outWidth, options.outHeight)
            } catch (e: Exception) {
                return Pair(0, 0)
            }
        }
    }
}
