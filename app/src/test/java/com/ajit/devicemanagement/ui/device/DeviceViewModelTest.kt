package com.ajit.devicemanagement.ui.device

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ajit.devicemanagement.data.dao.DummyData
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.repository.DeviceRepository
import com.ajit.devicemanagement.util.Operation
import com.ajit.devicemanagement.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class DeviceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var deviceRepository: DeviceRepository


    private lateinit var deviceViewModel: DeviceViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deviceRepository = mock(DeviceRepository::class.java)
        deviceViewModel = DeviceViewModel(deviceRepository)
    }

    @After
    fun cleanup() {
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun testGetDevices_success() = testScope.runBlockingTest {
        val devices = DummyData.getDummyDevices()

        `when`(deviceRepository.getDevices()).thenReturn(devices)

        deviceViewModel.getDevices()

        val uiState = deviceViewModel.devices.value
        assertTrue(uiState is UiState.Success)
        assertEquals(UiState.Success(devices), uiState)
    }

    @Test
    fun testGetDevices_failure() = testScope.runBlockingTest {
        val errorMessage = "Failed to get devices"
        `when`(deviceRepository.getDevices()).thenThrow(RuntimeException(errorMessage))

        deviceViewModel.getDevices()

        val uiState = deviceViewModel.devices.value
        assertTrue(uiState is UiState.Failure)
        assertEquals(UiState.Failure("Failed to get devices"), uiState)
    }

    @Test
    fun testAddDevice_success() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        doAnswer {
            val change = Change(operation = Operation.INSERT, device = device)
            testScope.runBlockingTest {
                deviceRepository.addChange(change)
            }
            null
        }.`when`(deviceRepository).addDevice(device)

        deviceViewModel.addDevice(device)

        val uiState = deviceViewModel.addDevice.value
        assertTrue(uiState is UiState.Success)
        assertEquals(UiState.Success(device), uiState)
    }

    @Test
    fun testAddDevice_failure() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        val errorMessage = "Failed to add device"
        doThrow(RuntimeException(errorMessage)).`when`(deviceRepository).addDevice(device)

        deviceViewModel.addDevice(device)

        val uiState = deviceViewModel.addDevice.value
        assertTrue(uiState is UiState.Failure)
        assertEquals(UiState.Failure("Failed to add device"), uiState)
    }

    @Test
    fun testUpdateDevice_success() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        doAnswer {
            val change = Change(operation = Operation.UPDATE, device = device)
            testScope.runBlockingTest {
                deviceRepository.addChange(change)
            }
            null
        }.`when`(deviceRepository).updateDevice(device)

        deviceViewModel.updateDevice(device)

        val uiState = deviceViewModel.updateDevice.value
        assertTrue(uiState is UiState.Success)
        assertEquals(UiState.Success("Device has been updated"), uiState)
    }

    @Test
    fun testUpdateDevice_failure() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        val errorMessage = "Failed to update device"
        doThrow(RuntimeException(errorMessage)).`when`(deviceRepository).updateDevice(device)

        deviceViewModel.updateDevice(device)

        val uiState = deviceViewModel.updateDevice.value
        assertTrue(uiState is UiState.Failure)
        assertEquals(UiState.Failure("Failed to update device"), uiState)
    }

    @Test
    fun testDeleteDevice_success() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        doAnswer {
            val change = Change(operation = Operation.DELETE, device = device)
            testScope.runBlockingTest {
                deviceRepository.addChange(change)
            }
            null
        }.`when`(deviceRepository).deleteDevice(device)

        deviceViewModel.deleteDevice(device)

        verify(deviceRepository).deleteDevice(device)

        val uiState = deviceViewModel.deleteDevice.value
        assertTrue(uiState is UiState.Success)
        assertEquals(UiState.Success("Device has been deleted"), uiState)
    }

    @Test
    fun testDeleteDevice_failure() = testScope.runBlockingTest {
        val device = DummyData.getDummyDevices().first()

        val errorMessage = "Failed to delete device"
        doThrow(RuntimeException(errorMessage)).`when`(deviceRepository).deleteDevice(device)

        deviceViewModel.deleteDevice(device)

        verify(deviceRepository).deleteDevice(device)

        val uiState = deviceViewModel.deleteDevice.value
        assertTrue(uiState is UiState.Failure)
        assertEquals(UiState.Failure("Failed to delete device"), uiState)
    }

}
