package com.misana.barcodeface.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misana.barcodeface.domain.model.Barcode
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode")
    fun getBarcodes(): Flow<List<Barcode>>

    @Query("SELECT * FROM barcode WHERE id = :id")
    suspend fun getBarcodeById(id: Int): Barcode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcode(barcode: Barcode)

    @Delete
    suspend fun deleteBarcode(barcode: Barcode)
}
