package com.ajit.devicemanagement.data.provider


import com.ajit.devicemanagement.data.dao.DeviceDao
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ajit.devicemanagement.data.AppDatabase
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.repository.FirestoreRepository
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.util.Operation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlin.coroutines.resume

@ExperimentalCoroutinesApi
@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val deviceDao: DeviceDao by lazy {
        AppDatabase.getDatabase(context).deviceDao()
    }

    private var dataSyncManager: DataSyncManager = DataSyncManager(context)

    private val firestoreRepository = FirestoreRepository()

    private val currentUser = LoginFragment.UserSession.loggedInUser

    override suspend fun doWork(): Result {
        Log.d("DataSyncWorker", "Starting data synchronization")

        return withContext(Dispatchers.IO) {
            try {
                syncDevices()
                Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Data synchronization failed", e)
            Result.failure()
        }
        }
    }


    private suspend fun syncDevices() {
        coroutineScope {
            Log.d("DataSyncWorker", "Sync devices called")

            val pendingChanges = getPendingChangesFromContentProvider()
            Log.e("DataSyncWorker", "pending changess called $pendingChanges")

            if (pendingChanges.isEmpty()) {
                initialSync()

            }
            else {

                pendingChanges.forEach { change ->
                    when (change.operation) {
                        Operation.INSERT -> launch { syncInsertOperation(change) }
                        Operation.UPDATE -> launch { syncUpdateOperation(change) }
                        Operation.DELETE -> launch { syncDeleteOperation(change) }
                    }
                        dataSyncManager.stopSync()

                }
            }
        }
    }



    private suspend fun fetchDevicesFromFirestore(): List<RoomDevice> {
        return suspendCancellableCoroutine { continuation ->
            firestoreRepository.fetchDevicesFromFirestore { devices ->
                continuation.resume(devices) // Resume the coroutine with the fetched devices
            }
        }
    }


    private suspend fun syncInsertOperation(change: Change) {
        val device = change.device
        Log.e("SyncWorker", "instering device $device")

        try {
            val success = suspendCancellableCoroutine { continuation ->
                firestoreRepository.addDevice(device) { success ->
                    continuation.resume(success) {
                    }
                }
            }

            if (success) {
                withContext(Dispatchers.IO) {
                    deleteSyncedChange(change)
                }
            } else {
                Log.e("DataSyncWorker", "Failed to add device to Firestore: $device")

            }
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Exception occurred during device addition: ${e.message}")

        }
    }


    private suspend fun syncUpdateOperation(change: Change) {
        val device = change.device
        Log.e("DataSyncWorker", "Sync Update called, $device")


        try {
            val success = suspendCancellableCoroutine { continuation ->
                firestoreRepository.updateDevice(device) { success ->
                    continuation.resume(success) {
                    }
                }
            }

            if (success) {
                withContext(Dispatchers.IO) {
                    deleteSyncedChange(change)
                }
            } else {

                Log.e("DataSyncWorker", "Failed to update device in Firestore: $device")

            }
        } catch (e: Exception) {
        }
    }

    private suspend fun syncDeleteOperation(change: Change) {
        val device = change.device
        Log.e("DataSyncWorker", "Sync delete called $device")

        try {
            val success = suspendCancellableCoroutine { continuation ->
                firestoreRepository.deleteDevice(device.deviceId) { success ->
                    continuation.resume(success) {
                        // Handle cancellation if needed
                    }
                }
            }

            if (success) {
                withContext(Dispatchers.IO) {
                    deleteSyncedChange(change)
                }
            } else {
                // Handle device addition failure to Firestore
            }
        } catch (e: Exception) {
            // Handle any other exceptions
        }
    }


    private suspend fun getPendingChangesFromContentProvider(): List<Change> {
        return withContext(Dispatchers.IO) {
            val contentResolver = applicationContext.contentResolver
            val uri =
                Uri.parse("content://com.ajit.devicemanagement.data.provider.DeviceContentProvider/pending_changes")
            val projection = arrayOf(
                "change_id",
                "operation",
                "device_id",
                "identification_id",
                "device_type",
                "device_name",
                "model",
                "manufacturer",
                "os",
                "other_details",
                "date",
                "last_modified",
            )
            val cursor = contentResolver.query(uri, projection, null, null, null)
            val changes = mutableListOf<Change>()
            cursor?.use { cursor ->
                Log.d("DataSyncWorker", "Number of rows in cursor: ${cursor.count}")
                while (cursor.moveToNext()) {
                    val change = Change.fromCursor(cursor)
                    changes.add(change)
                }
            }

            changes
        }
    }


    private suspend fun deleteSyncedChange(change: Change) {
        Log.e("SyncWorker", "deleting $change")
        withContext(Dispatchers.IO) {
            val contentResolver = applicationContext.contentResolver
            val uri =
                Uri.parse("content://com.ajit.devicemanagement.data.provider.DeviceContentProvider/pending_changes/${change.id}")
            val selectionArgs = arrayOf(change.id.toString()) // Provide the selection arguments
            val rowsDeleted = contentResolver.delete(uri, null, selectionArgs)
            Log.d("SyncWorker", "uri $uri")
            Log.d("SyncWorker", "Number of rows deleted: $rowsDeleted")
        }
    }



    private suspend fun initialSync() {
        // Delete all existing devices from the devices table in Room

        val devicesFromFirestore = fetchDevicesFromFirestore()
        Log.e("DataSyncWorker"," initial sync devices  to be added $devicesFromFirestore")

        deleteAllDevicesFromRoom()

        if (devicesFromFirestore.isNotEmpty()) {
            // Add devices from Firestore to the devices table in Room
            addDevicesToRoom(devicesFromFirestore)
        }

        dataSyncManager.stopSync()
    }




    private suspend fun deleteAllDevicesFromRoom() {
        withContext(Dispatchers.IO) {
            deviceDao.deleteAllDevices()
        }
    }


    private suspend fun addDevicesToRoom(devices: List<RoomDevice>) {
        withContext(Dispatchers.IO) {
            devices.forEach {
                Log.e("DataSyncWorker"," adding devices  to be room $it")
                deviceDao.addDevice(it)
            }
        }
    }

}

