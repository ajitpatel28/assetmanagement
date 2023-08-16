package com.ajit.devicemanagement.data.dao

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice



@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices")
    suspend fun getDevices(): List<RoomDevice>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDevice(device: RoomDevice): Long

    @Query("SELECT * FROM devices WHERE device_id = :deviceId")
    suspend fun getDeviceByDeviceId(deviceId: String): RoomDevice?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDevice(device: RoomDevice)

    @Delete
    suspend fun deleteDevice(device: RoomDevice)

    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()

    @RawQuery
    fun getDevicesCursor(query: SupportSQLiteQuery): Cursor

    @RawQuery
    fun getChangesCursor(query: SupportSQLiteQuery): Cursor



    @Query("SELECT * FROM pending_changes")
    suspend fun getAllPendingChanges(): List<Change>

    @Insert
    suspend fun addChange(deviceChange: Change):Long

    @Delete
    suspend fun deleteChange(deviceChange: Change)

    @Query("DELETE FROM pending_changes")
    suspend fun deleteAllChanges()

}



