package com.ajit.devicemanagement.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.ajit.devicemanagement.data.AppDatabase
import com.ajit.devicemanagement.data.dao.DeviceDao
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice
import kotlinx.coroutines.*

class DeviceContentProvider : ContentProvider() {
    private lateinit var deviceDao: DeviceDao

    // Authority for the content provider
    private val authority = "com.ajit.devicemanagement.data.provider.DeviceContentProvider"

    // UriMatcher codes for the tables
    private val devicesCode = 1
    private val changesCode = 2

    // UriMatcher instance
    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        // Add the content URI patterns for devices and changes tables
        uriMatcher.addURI(authority, "devices", devicesCode)
        uriMatcher.addURI(authority, "pending_changes", changesCode)
        uriMatcher.addURI(authority, "pending_changes/#", changesCode)
    }

    override fun onCreate(): Boolean {
        Log.d("DeviceContentProvider", "DeviceContentProvider called")
        val context = context ?: return false
        val database = AppDatabase.getDatabase(context)
        deviceDao = database.deviceDao()
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            devicesCode -> {
                val device = RoomDevice.fromContentValues(values!!)
                GlobalScope.launch {
                    Log.d("DeviceContentProvider", "Inserting device: $device")
                    val insertedId = deviceDao.addDevice(device)
                    device.deviceId = insertedId.toString()
                    withContext(Dispatchers.Main) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
                Log.d("DeviceContentProvider", "Inserted device: $device")
                ContentUris.withAppendedId(uri, device.deviceId.toLong())
            }
            changesCode -> {
                val change = Change.fromContentValues(values!!)
                GlobalScope.launch {
                    Log.d("DeviceContentProvider", "Inserting change: $change")
                    val insertedId = deviceDao.addChange(change)
                    change.id = insertedId
                    withContext(Dispatchers.Main) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
                ContentUris.withAppendedId(uri, change.id)
            }
            else -> null
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            devicesCode -> {
                val query = SimpleSQLiteQuery("SELECT * FROM devices ")
                deviceDao.getDevicesCursor(query)
            }
            changesCode -> {
                val query = SimpleSQLiteQuery("SELECT * FROM pending_changes")
                deviceDao.getChangesCursor(query)
            }
            else -> null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            devicesCode -> {
                val device = RoomDevice.fromContentValues(values!!)
                Log.d("DeviceContentProvider", "content updating device: $device")
                GlobalScope.launch {
                    deviceDao.addDevice(device)
                    Log.d("DeviceContentProvider", "Update method called for URI: $uri")
                    Log.e("DeviceContentProvider", "Updating device $device")
                    withContext(Dispatchers.Main) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
                1
            }
            changesCode -> {
                val change = Change.fromContentValues(values!!)
                GlobalScope.launch {
                    deviceDao.deleteChange(change)
                    Log.d("DeviceContentProvider", "Update method called for URI: $uri")
                    Log.e("DeviceContentProvider", "Updating change $change")
                    withContext(Dispatchers.Main) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
                1
            }
            else -> 0
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d("DeviceContentProvider", "URI received: $uri")

        return when (uriMatcher.match(uri)) {
            devicesCode -> {
                Log.d("DeviceContentProvider", " device cocde URI received: $uri")

                GlobalScope.launch {
                    val deviceId = selectionArgs?.getOrNull(0)
                    if (deviceId != null) {
                        val device = deviceDao.getDevices().find { it.deviceId == deviceId }
                        device?.let {
                            deviceDao.deleteDevice(it)
                            withContext(Dispatchers.Main) {
                                context?.contentResolver?.notifyChange(uri, null)
                            }
                        }
                    }
                }
                1
            }
            changesCode -> {
                Log.d("DeviceContentProvider", " change URI called: $uri")

                GlobalScope.launch {
                    val changeId = selectionArgs?.getOrNull(0)
                    Log.e("content prvider","change deleting, $selectionArgs")
                    if (changeId != null) {
                        val change = deviceDao.getAllPendingChanges().find { it.id == changeId.toLong() }
                        change?.let {
                            deviceDao.deleteChange(it)
                            withContext(Dispatchers.Main) {
                                context?.contentResolver?.notifyChange(uri, null)
                            }
                        }
                    }
                }
                1
            }
            else -> {
                    Log.d("DeviceContentProvider", "Uri match not found for: $uri")
                    0


            }
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}






