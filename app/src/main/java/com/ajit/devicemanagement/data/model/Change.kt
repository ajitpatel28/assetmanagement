package com.ajit.devicemanagement.data.model

import android.content.ContentValues
import android.database.Cursor
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ajit.devicemanagement.util.Operation

@Entity(tableName = "pending_changes")
data class Change(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "change_id")
    var id: Long = 0,
    @ColumnInfo(name = "operation")
    val operation: Operation,
    @Embedded
    val device: RoomDevice
) {

    companion object {
        fun fromContentValues(values: ContentValues): Change {
            val id = values.getAsLong("change_id") ?: 0
            val operationOrdinal = values.getAsInteger("operation") ?: 0
            val operation = Operation.values()[operationOrdinal]
            val device = RoomDevice.fromContentValues(values)
            return Change(id, operation, device)
        }

        fun fromCursor(cursor: Cursor): Change {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("change_id"))
            val operationString = cursor.getString(cursor.getColumnIndexOrThrow("operation"))
            val operation = Operation.valueOf(operationString)
            val device = RoomDevice.fromCursor(cursor)
            return Change(id, operation, device)
        }
    }
}