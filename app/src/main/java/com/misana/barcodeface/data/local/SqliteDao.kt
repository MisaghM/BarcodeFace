package com.misana.barcodeface.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SqliteDao {
    @Query("DELETE FROM sqlite_sequence")
    suspend fun resetSequence()
}
