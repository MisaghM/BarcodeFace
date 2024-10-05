package com.misana.barcodeface.presentation.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misana.barcodeface.domain.model.Barcode
import com.misana.barcodeface.domain.model.Emotion
import com.misana.barcodeface.domain.model.NetworkResult
import com.misana.barcodeface.domain.service.BarcodeService
import com.misana.barcodeface.domain.service.isBeforeMinutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(
    private val barcodeService: BarcodeService
) : ViewModel() {
    val snackbar: SnackbarHostState = SnackbarHostState()

    var showBottomSheet: Boolean by mutableStateOf(false)
        private set

    var showPackageDialog: Boolean by mutableStateOf(false)
        private set

    var selectedBarcodeId: Int by mutableIntStateOf(0)
        private set

    var barcodes: List<Barcode> by mutableStateOf(emptyList())
        private set

    var nextBarcode: Boolean by mutableStateOf(false)
        private set

    private var loadBarcodesJob: Job? = null
    private var nextBarcodeJob: Job? = null
    private var cameraFile: File? = null

    init {
        initLoadBarcodesJob()
    }

    private fun initLoadBarcodesJob() {
        loadBarcodesJob?.cancel()
        loadBarcodesJob = barcodeService.getBarcodes()
            .onEach { barcodes ->
                this.barcodes = barcodes
            }
            .launchIn(viewModelScope)
    }

    fun onCameraResult(result: Boolean) {
        nextBarcodeJob?.cancel()
        nextBarcodeJob = viewModelScope.launch {
            if (!result) {
                cameraFile = null
                snackbar.showSnackbar(
                    message = "Failed to capture image",
                    withDismissAction = true
                )
                return@launch
            }
            nextBarcode = true
            addBarcode()
            nextBarcode = false
        }
    }

    fun setCameraFile(file: File) {
        cameraFile = file
    }

    fun cancelNextBarcode() {
        nextBarcodeJob?.cancel()
        nextBarcode = false
        cameraFile = null
    }

    fun setShowSheet(show: Boolean) {
        showBottomSheet = show
    }

    fun setShowPackage(show: Boolean) {
        showPackageDialog = show
    }

    suspend fun getPackageData(): String {
        return barcodeService.getPackageData()
    }

    fun setSelectedBarcode(id: Int) {
        selectedBarcodeId = id
    }

    suspend fun getBarcode(id: Int): Barcode? {
        return barcodeService.getBarcodeById(id)
    }

    private suspend fun addBarcode() {
        val res = barcodeService.addBarcode(cameraFile!!)
        if (res.status != NetworkResult.Status.SUCCESS) {
            snackbar.showSnackbar(
                message = NetworkResult.message(res),
                withDismissAction = true
            )
        } else {
            if (res.body == "none") {
                snackbar.showSnackbar(
                    message = "No face detected!",
                    withDismissAction = true
                )
            }
        }
    }

    fun onePerHour(): Boolean {
        if (barcodes.isNotEmpty() &&
            !barcodes.first().timestamp.isBeforeMinutes(60)
        ) {
            viewModelScope.launch {
                snackbar.showSnackbar(
                    message = "You can only take one picture per hour.",
                    withDismissAction = true
                )
            }
            return false
        }
        return true
    }

    fun deleteBarcode(barcode: Barcode) {
        viewModelScope.launch {
            barcodeService.deleteBarcode(barcode)
        }
    }

    fun getEmotion(barcode: Barcode): Emotion {
        return barcodeService.barcodeToEmotion(barcode)
    }

    fun reset() {
        viewModelScope.launch {
            showBottomSheet = false
            selectedBarcodeId = 0
            loadBarcodesJob?.cancel()
            barcodes = emptyList()
            nextBarcode = false
            cameraFile = null
            nextBarcodeJob?.cancel()
            initLoadBarcodesJob()
        }
    }

    class Factory(
        private val barcodeService: BarcodeService
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(barcodeService) as T
        }
    }
}
