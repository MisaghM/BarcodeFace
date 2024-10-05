package com.misana.barcodeface.data.repository

import com.misana.barcodeface.data.local.BarcodeDao
import com.misana.barcodeface.domain.model.Barcode
import kotlinx.coroutines.flow.Flow

class BarcodeRepository(
    private val dao: BarcodeDao
) {
    fun getBarcodes(): Flow<List<Barcode>> = dao.getBarcodes()
    suspend fun getBarcodeById(id: Int): Barcode? = dao.getBarcodeById(id)
    suspend fun insertBarcode(barcode: Barcode) {
        dao.insertBarcode(barcode.copy(
            timestamp = System.currentTimeMillis()
        ))
    }
    suspend fun deleteBarcode(barcode: Barcode) = dao.deleteBarcode(barcode)
}
