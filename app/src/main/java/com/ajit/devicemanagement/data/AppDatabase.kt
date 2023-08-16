package com.ajit.devicemanagement.data

import com.ajit.devicemanagement.data.dao.DeviceDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice

@Database(entities = [RoomDevice::class, Change::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    companion object {
        private const val DATABASE_NAME = "device_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                Companion.instance = instance
                return instance
            }
        }
    }
}


