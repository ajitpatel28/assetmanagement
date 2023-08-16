package com.ajit.devicemanagement.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ajit.devicemanagement.data.provider.DataSyncWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi

class NetworkChangeReceiver : BroadcastReceiver() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo

        // Check if the device is connected to the internet
        val isConnected = networkInfo?.isConnected == true

        if (isConnected) {
            // Schedule the DataSyncWorker for syncing
            val syncRequest = OneTimeWorkRequestBuilder<DataSyncWorker>().build()
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
    }
}
