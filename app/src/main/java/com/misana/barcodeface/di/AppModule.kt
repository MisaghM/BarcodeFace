package com.misana.barcodeface.di

import android.content.Context
import androidx.room.Room
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.misana.barcodeface.data.local.AppDatabase
import com.misana.barcodeface.data.local.settingsDataStore
import com.misana.barcodeface.data.remote.FerApi
import com.misana.barcodeface.data.repository.BarcodeRepository
import com.misana.barcodeface.data.repository.FerRepository
import com.misana.barcodeface.data.repository.SettingsRepository
import com.misana.barcodeface.domain.service.BarcodeService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class AppModule(
    appContext: Context
) {
    private val appDatabase: AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    private val okhttp: OkHttpClient = OkHttpClient.Builder().build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(FerApi.BASE_URL)
        .client(okhttp)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val ferApi = retrofit.create(FerApi::class.java)
    private val barcodeRepo = BarcodeRepository(appDatabase.barcodeDao)

    val settingsRepo = SettingsRepository(appContext.settingsDataStore)
    val ferRepo = FerRepository(ferApi)
    val barcodeService = BarcodeService(appContext, barcodeRepo, ferRepo)

    @OptIn(DelicateCoroutinesApi::class)
    fun clearDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.clearAllTables()
            appDatabase.sqliteDao.resetSequence()
        }
    }
}
