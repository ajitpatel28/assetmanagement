package com.ajit.devicemanagement.data.repository

import com.ajit.devicemanagement.data.dao.DeviceDao
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice
import javax.inject.Inject


class DeviceRepository @Inject constructor(private val deviceDao: DeviceDao) {

    suspend fun getDevices(): List<RoomDevice> {
        return deviceDao.getDevices()
    }

    suspend fun getDeviceByDeviceId(deviceId: String): RoomDevice {
        return deviceDao.getDeviceByDeviceId(deviceId)!!
    }

    suspend fun addDevice(device: RoomDevice) {
        deviceDao.addDevice(device)
    }

    suspend fun updateDevice(device: RoomDevice) {
        // Retrieve the existing device from the database based on the deviceId
        val existingDevice = deviceDao.getDeviceByDeviceId(device.deviceId)

        // Update the fields of the existing device with the new values
        existingDevice?.let {
            it.employeeId = device.employeeId
            it.deviceType = device.deviceType
            it.deviceName = device.deviceName
            it.model = device.model
            it.manufacturer = device.manufacturer
            it.identificationId = device.identificationId
            it.otherDetails = device.otherDetails
            it.date = device.date
            it.lastModified = System.currentTimeMillis()

            // Update the device in the database
            deviceDao.updateDevice(existingDevice)
        }
    }
    suspend fun deleteDevice(device: RoomDevice) {
        deviceDao.deleteDevice(device)
    }

    suspend fun deleteAllDevices() {
        deviceDao.deleteAllDevices()
    }



    suspend fun addChange(change: Change) {
        deviceDao.addChange(change)
    }


}
