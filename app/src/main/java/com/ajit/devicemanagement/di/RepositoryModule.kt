package com.ajit.devicemanagement.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ajit.devicemanagement.data.dao.DeviceDao
import com.ajit.devicemanagement.data.repository.AuthRepository
import com.ajit.devicemanagement.data.repository.AuthRepositoryImpl
import com.ajit.devicemanagement.data.repository.DeviceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun provideDeviceRepository(deviceDao: DeviceDao): DeviceRepository {
        return DeviceRepository(deviceDao)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database, appPreferences, gson)
    }
}

