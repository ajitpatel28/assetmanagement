package com.ajit.devicemanagement.di

import android.content.Context
import androidx.work.WorkManager
import com.ajit.devicemanagement.data.provider.DataSyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSyncModule {

    @Provides
    @Singleton
    fun provideDataSyncManager(context: Context): DataSyncManager {
        return DataSyncManager(context)
    }




    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}

