//package com.ajit.devicemanagement.data.dao
//
//import android.content.Context
//import androidx.test.core.app.ApplicationProvider
//import androidx.work.*
//import androidx.work.testing.WorkManagerTestInitHelper
//import com.ajit.devicemanagement.data.provider.DataSyncWorker
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//import org.robolectric.shadows.ShadowLog
//import org.robolectric.shadows.ShadowLooper
//import java.util.concurrent.TimeUnit
//
//@RunWith(RobolectricTestRunner::class)
//@Config(manifest = Config.NONE)
//class DataSyncManagerTest {
//
//    private lateinit var context: Context
//
//    @Before
//    fun setup() {
//        context = ApplicationProvider.getApplicationContext()
//        ShadowLog.stream = System.out
//    }
//
//    @Test
//    fun testStartSync() {
//        val workRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
//            repeatInterval = 30L, TimeUnit.MINUTES
//        ).build()
//
//        val workManager = WorkManager.getInstance(context)
//        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
//
//        // Enqueue the work request
//        workManager.enqueue(workRequest)
//
//        // Start the work
//        testDriver?.setAllConstraintsMet(workRequest.id)
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        val workInfo = workManager.getWorkInfoById(workRequest.id).get()
//
//        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)
//    }
//
//    @Test
//    fun testStopSync() {
//        val workRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
//            repeatInterval = 30L, TimeUnit.MINUTES
//        ).build()
//
//        val workManager = WorkManager.getInstance(context)
//
//        // Enqueue the work request
//        workManager.enqueue(workRequest)
//
//        // Stop the work
//        workManager.cancelWorkById(workRequest.id).result.get()
//
//        val workInfo = workManager.getWorkInfoById(workRequest.id).get()
//
//        assertEquals(WorkInfo.State.CANCELLED, workInfo.state)
//    }
//}
