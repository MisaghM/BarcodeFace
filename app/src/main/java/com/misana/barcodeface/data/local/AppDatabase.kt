package com.misana.barcodeface.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.misana.barcodeface.domain.model.Barcode

@Database(
    entities = [Barcode::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val barcodeDao: BarcodeDao
    abstract val sqliteDao: SqliteDao

    companion object {
        const val DATABASE_NAME = "barcodeface_db"
    }
}
