package com.ajit.devicemanagement.data.dao

import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.util.Operation

object DummyData {
    fun getDummyDevices(): List<RoomDevice> {
        return listOf(
            RoomDevice(
                deviceId = "Device1",
                employeeId = "Employee1",
                deviceType = "Type1",
                deviceName = "Name1",
                model = "Model1",
                manufacturer = "Manufacturer1",
                identificationId = "ID1",
                otherDetails = "Details1",
                date = 1625808000000
            ),
            RoomDevice(
                deviceId = "Device2",
                employeeId = "Employee2",
                deviceType = "Type2",
                deviceName = "Name2",
                model = "Model2",
                manufacturer = "Manufacturer2",
                identificationId = "ID2",
                otherDetails = "Details2",
                date = 1625894400000
            )
        )
    }

    fun getDummyChanges(): List<Change> {
        return listOf(
            Change(
                id = 1,
                operation = Operation.INSERT,
                device = RoomDevice(
                    deviceId = "Device1",
                    employeeId = "Employee1",
                    deviceType = "Type1",
                    deviceName = "Name1",
                    model = "Model1",
                    manufacturer = "Manufacturer1",
                    identificationId = "ID1",
                    otherDetails = "Details1",
                    date = 1625808000000
                )
            ),
            Change(
                id = 2,
                operation = Operation.UPDATE,
                device = RoomDevice(
                    deviceId = "Device2",
                    employeeId = "Employee2",
                    deviceType = "Type2",
                    deviceName = "Name2",
                    model = "Model2",
                    manufacturer = "Manufacturer2",
                    identificationId = "ID2",
                    otherDetails = "Details2",
                    date = 1625894400000
                )
            )
        )
    }
}
