package com.ajit.devicemanagement.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ajit.devicemanagement.data.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DeviceDaoTest {

    private lateinit var deviceDao: DeviceDao
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        deviceDao = db.deviceDao()
    }

    @After
    fun cleanup() {
        db.close()
    }

    @Test
    fun testGetDevices() = runBlocking {
        val devices = DummyData.getDummyDevices()
        devices.forEach { deviceDao.addDevice(it) }

        val result = deviceDao.getDevices()

        assertEquals(devices.size, result.size)
        assertEquals(devices, result)
    }

    @Test
    fun testAddDevice() = runBlocking {
        val device = DummyData.getDummyDevices().first()

        deviceDao.addDevice(device)

        val result = deviceDao.getDeviceByDeviceId(device.deviceId)

        assertEquals(device, result)
    }

    @Test
    fun testGetDeviceByDeviceId() = runBlocking {
        val device = DummyData.getDummyDevices().first()

        deviceDao.addDevice(device)
        val result = deviceDao.getDeviceByDeviceId(device.deviceId)

        assertEquals(device, result)
    }

    @Test
    fun testUpdateDevice() = runBlocking {
        val device = DummyData.getDummyDevices().first()

        deviceDao.addDevice(device)

        val updatedDevice = device.copy(
            deviceType = "UpdatedType",
            deviceName = "UpdatedName"
        )
        deviceDao.updateDevice(updatedDevice)

        val result = deviceDao.getDeviceByDeviceId(device.deviceId)

        assertEquals(updatedDevice, result)
    }

    @Test
    fun testDeleteDevice() = runBlocking {
        val devices = DummyData.getDummyDevices()

        deviceDao.addDevice(devices[0])
        deviceDao.addDevice(devices[1])

        deviceDao.deleteDevice(devices[0])

        val result = deviceDao.getDevices()

        assertEquals(1, result.size)
        assertEquals(devices[1], result[0])
    }

    @Test
    fun testDeleteAllDevices() = runBlocking {
        val devices = DummyData.getDummyDevices()
        devices.forEach { deviceDao.addDevice(it) }

        deviceDao.deleteAllDevices()

        val result = deviceDao.getDevices()

        assertTrue(result.isEmpty())
    }

    @Test
    fun testGetAllPendingChanges() = runBlocking {
        val changes = DummyData.getDummyChanges()
        changes.forEach { deviceDao.addChange(it) }

        val result = deviceDao.getAllPendingChanges()

        assertEquals(changes.size, result.size)
        assertEquals(changes, result)
    }

    @Test
    fun testAddChange() = runBlocking {
        val change = DummyData.getDummyChanges().first()

        deviceDao.addChange(change)

        val result = deviceDao.getAllPendingChanges()

        assertEquals(1, result.size)
        assertEquals(change, result[0])
    }

    @Test
    fun testDeleteChange() = runBlocking {
        val changes = DummyData.getDummyChanges()

        deviceDao.addChange(changes[0])
        deviceDao.addChange(changes[1])

        deviceDao.deleteChange(changes[0])

        val result = deviceDao.getAllPendingChanges()

        assertEquals(1, result.size)
        assertEquals(changes[1], result[0])
    }

    @Test
    fun testDeleteAllChanges() = runBlocking {
        val changes = DummyData.getDummyChanges()
        changes.forEach { deviceDao.addChange(it) }

        deviceDao.deleteAllChanges()

        val result = deviceDao.getAllPendingChanges()

        assertTrue(result.isEmpty())
    }
}
