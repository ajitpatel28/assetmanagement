package com.ajit.devicemanagement.data.dao

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.ajit.devicemanagement.data.AppDatabase
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.data.provider.DeviceContentProvider
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DeviceContentProviderTest {
    private lateinit var contentResolver: ContentResolver
    private lateinit var deviceDao: DeviceDao
    private lateinit var provider: DeviceContentProvider

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        contentResolver = context.contentResolver
        deviceDao = AppDatabase.getDatabase(context).deviceDao()
        provider = DeviceContentProvider().apply {
            attachInfo(context, null)
        }
    }


    @After
    fun teardown() {
        // Close the database connection
        AppDatabase.getDatabase(ApplicationProvider.getApplicationContext()).close()
    }


    @Test
    fun testInsertDevice() = runBlocking(Dispatchers.IO) {
        val device = DummyData.getDummyDevices()[0]
        val values = ContentValues().apply {
            put("device_id", device.deviceId)
            put("employee_id", device.employeeId)
            put("device_type", device.deviceType)
            put("device_name", device.deviceName)
            put("model", device.model)
            put("manufacturer", device.manufacturer)
            put("identification_id", device.identificationId)
            put("other_details", device.otherDetails)
            put("date", device.date)
        }

        val uri = contentResolver.insert(
            Uri.parse("content://com.ajit.devicemanagement.data.provider.DeviceContentProvider/devices"),
            values
        )

        assertNotNull(uri)
        val insertedDeviceId = ContentUris.parseId(uri!!)
        println("devices $insertedDeviceId")
        assertTrue(insertedDeviceId > 0)

        val query = SimpleSQLiteQuery("SELECT * FROM devices")
        val deviceCursor = deviceDao.getDevicesCursor(query)

        if (deviceCursor.moveToFirst()) {
            val retrievedDevice = RoomDevice.fromCursor(deviceCursor)
            assertEquals(device, retrievedDevice)
        } else {
            fail("No devices found in the database")
        }

        deviceCursor.close()
    }


    @Test
    fun testQueryDevices() {
        runBlocking {
            // Insert a device to ensure there is data in the database
            val device = DummyData.getDummyDevices()[0]
            deviceDao.addDevice(device)

            val query = SimpleSQLiteQuery("SELECT * FROM devices")
            val deviceCursor = withContext(Dispatchers.IO) {
                deviceDao.getDevicesCursor(query)
            }

        assertEquals(1, deviceCursor.count)

            deviceCursor.close()
        }
    }


}