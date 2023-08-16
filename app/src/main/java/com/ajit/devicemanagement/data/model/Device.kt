package com.ajit.devicemanagement.data.model

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "devices")
data class RoomDevice(

    @PrimaryKey
    @ColumnInfo(name = "device_id")
    var deviceId: String,

    @ColumnInfo(name = "employee_id")
    var employeeId: String,

    @ColumnInfo(name = "device_type")
    var deviceType: String,

    @ColumnInfo(name = "device_name")
    var deviceName: String,

    @ColumnInfo(name = "model")
    var model: String,

    @ColumnInfo(name = "manufacturer")
    var manufacturer: String,

    @ColumnInfo(name = "identification_id")
    var identificationId: String,

    @ColumnInfo(name = "other_details")
    var otherDetails: String,

    @ColumnInfo(name = "date")
    var date: Long,

    @ColumnInfo(name = "last_modified")
    var lastModified: Long = System.currentTimeMillis(),

) : Parcelable {
    companion object {
        fun fromContentValues(values: ContentValues): RoomDevice {
            val deviceId = values.getAsString("device_id") ?: ""
            val employeeId = values.getAsString("employee_id") ?: ""
            val deviceType = values.getAsString("device_type") ?: ""
            val deviceName = values.getAsString("device_name") ?: ""
            val model = values.getAsString("model") ?: ""
            val manufacturer = values.getAsString("manufacturer") ?: ""
            val identificationId = values.getAsString("identification_id") ?: ""
            val otherDetails = values.getAsString("other_details") ?: ""
            val date = values.getAsLong("date") ?: 0L
            val lastModified = values.getAsLong("last_modified") ?: System.currentTimeMillis()

            return RoomDevice(
                deviceId = deviceId,
                employeeId = employeeId,
                deviceType = deviceType,
                deviceName = deviceName,
                model = model,
                manufacturer = manufacturer,
                identificationId = identificationId,
                otherDetails = otherDetails,
                date = date,
                lastModified = lastModified,
            )
        }

        fun fromCursor(cursor: Cursor): RoomDevice {
//            val idIndex = cursor.getColumnIndexOrThrow("id")
            val deviceIdIndex = cursor.getColumnIndexOrThrow("device_id")
            val employeeIdIndex = cursor.getColumnIndexOrThrow("employee_id")
            val deviceTypeIndex = cursor.getColumnIndexOrThrow("device_type")
            val deviceNameIndex = cursor.getColumnIndexOrThrow("device_name")
            val modelIndex = cursor.getColumnIndexOrThrow("model")
            val manufacturerIndex = cursor.getColumnIndexOrThrow("manufacturer")
            val identificationIdIndex = cursor.getColumnIndexOrThrow("identification_id")
            val otherDetailsIndex = cursor.getColumnIndexOrThrow("other_details")
            val dateIndex = cursor.getColumnIndexOrThrow("date")
            val lastModifiedIndex = cursor.getColumnIndexOrThrow("last_modified")

//            val id = cursor.getLong(idIndex)
            val deviceId = cursor.getString(deviceIdIndex)
            val employeeId = cursor.getString(employeeIdIndex)
            val deviceType = cursor.getString(deviceTypeIndex)
            val deviceName = cursor.getString(deviceNameIndex)
            val model = cursor.getString(modelIndex)
            val manufacturer = cursor.getString(manufacturerIndex)
            val identificationId = cursor.getString(identificationIdIndex)
            val otherDetails = cursor.getString(otherDetailsIndex)
            val date = cursor.getLong(dateIndex)
            val lastModified = cursor.getLong(lastModifiedIndex)

            return RoomDevice(
//                id = id,
                deviceId = deviceId,
                employeeId = employeeId,
                deviceType = deviceType,
                deviceName = deviceName,
                model = model,
                manufacturer = manufacturer,
                identificationId = identificationId,
                otherDetails = otherDetails,
                date = date,
                lastModified = lastModified
            )
        }
    }

}
