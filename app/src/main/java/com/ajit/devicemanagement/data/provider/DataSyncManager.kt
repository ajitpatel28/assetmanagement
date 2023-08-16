package com.ajit.devicemanagement.data.provider


import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataSyncManager @Inject constructor(private val context: Context) {

    private val workTag = "DataSyncWorkerTag"
    private val syncIntervalMinutes = 30L // Sync interval in minutes



    fun startSync(completion: () -> Unit) {
        /**
         * Schedule the DataSyncWorker for periodic syncing
         */
        val syncRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            syncIntervalMinutes, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.UPDATE, syncRequest)

        /**
         * Register a WorkManager observer to listen for the worker's completion
         */
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(syncRequest.id)
            .observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    /**
                     * Worker has completed, invoke the completion callback
                     */
                    completion.invoke()
                }
            }
    }

    fun stopSync() {
        /**
         *  Cancel the periodic sync
         */
        WorkManager.getInstance(context).cancelUniqueWork(workTag)
    }
}
