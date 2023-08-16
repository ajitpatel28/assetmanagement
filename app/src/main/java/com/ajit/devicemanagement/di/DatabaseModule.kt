package com.ajit.devicemanagement.di

import android.content.Context
import androidx.room.Room
import com.ajit.devicemanagement.data.AppDatabase
import com.ajit.devicemanagement.data.dao.DeviceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "device_database")
            .build()
    }

    @Provides
    @Singleton
    fun provideDeviceDao(appDatabase: AppDatabase): DeviceDao {
        return appDatabase.deviceDao()
    }
}
