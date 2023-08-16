package com.ajit.devicemanagement.ui.device

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajit.devicemanagement.data.model.Change
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.data.repository.DeviceRepository
import com.ajit.devicemanagement.util.Operation
import com.ajit.devicemanagement.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(private val deviceRepository: DeviceRepository) : ViewModel() {

    private val TAG = "DeviceViewModel"
    private val _devices = MutableLiveData<UiState<List<RoomDevice>>>()
    val devices: MutableLiveData<UiState<List<RoomDevice>>>
        get() = _devices

    private val _addDevice = MutableLiveData<UiState<RoomDevice>>()
    val addDevice: MutableLiveData<UiState<RoomDevice>>
        get() = _addDevice

    private val _updateDevice = MutableLiveData<UiState<String>>()
    val updateDevice: LiveData<UiState<String>>
        get() = _updateDevice

    private val _deleteDevice = MutableLiveData<UiState<String>>()
    val deleteDevice: LiveData<UiState<String>>
        get() = _deleteDevice

    fun getDevices() {
        _devices.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = deviceRepository.getDevices()
                _devices.value = UiState.Success(result)
            } catch (e: Exception) {
                _devices.value = UiState.Failure(e.message)
            }
        }
    }

    fun deleteAllDevices() = viewModelScope.launch {
        deviceRepository.deleteAllDevices()
    }

    fun addDevice(device: RoomDevice) {
        _addDevice.value = UiState.Loading
        viewModelScope.launch {
            try {
                deviceRepository.addDevice(device)
                val change = Change(operation = Operation.INSERT, device = device)
                deviceRepository.addChange(change)

                _addDevice.value = UiState.Success(device)
            } catch (e: Exception) {
                _addDevice.value = UiState.Failure(e.message)
            }
        }
    }

    fun updateDevice(device: RoomDevice) {
        _updateDevice.value = UiState.Loading
        viewModelScope.launch {
            try {
                deviceRepository.updateDevice(device)
                val change = Change(operation = Operation.UPDATE, device = device)
                deviceRepository.addChange(change)

                Log.e(TAG, "updated device $device")
                _updateDevice.value = UiState.Success("Device has been updated")
            } catch (e: Exception) {
                _updateDevice.value = UiState.Failure(e.message)
            }
        }
    }

    fun deleteDevice(device: RoomDevice) {
        _deleteDevice.value = UiState.Loading
        viewModelScope.launch {
            try {
                deviceRepository.deleteDevice(device)
                Log.e(TAG,"deletedevice called $device")
                val change = Change(operation = Operation.DELETE, device = device)
                Log.e(TAG,"deletedevice  change called $change")

                deviceRepository.addChange(change)

                Log.e(TAG,"device deleting $device")
                _deleteDevice.value = UiState.Success("Device has been deleted")
            } catch (e: Exception) {
                _deleteDevice.value = UiState.Failure(e.message)
            }
        }
    }
}


