package com.ajit.devicemanagement.ui.device

import com.ajit.devicemanagement.data.provider.DataSyncManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.databinding.FragmentAddNewDeviceBinding
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNewDeviceFragment : BaseFragment() {

    private lateinit var binding: FragmentAddNewDeviceBinding
    private lateinit var dataSyncManager: DataSyncManager
    private val viewModel: DeviceViewModel by viewModels()
    private val currentUser = LoginFragment.UserSession.loggedInUser

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataSyncManager = DataSyncManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceToEdit: RoomDevice? = arguments?.getParcelable("device")

        binding.addDeviceBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (deviceToEdit != null) {
            // Editing existing device, populate fields with deviceToEdit data
            binding.editTextDeviceId.setText(deviceToEdit.deviceId)
            binding.editTextDeviceId.isEnabled = false // Make deviceId non-editable
            binding.editTextDeviceId.setBackgroundResource(R.drawable.non_editable_edit_text) // Change background
            binding.spinnerDeviceType.setSelection(getIndexForDeviceType(deviceToEdit.deviceType))
            binding.editTextDeviceName.setText(deviceToEdit.deviceName)
            binding.editTextModel.setText(deviceToEdit.model)
            binding.editTextManufacturer.setText(deviceToEdit.manufacturer)
            binding.editTextDeviceIdentificationId.setText(deviceToEdit.identificationId)
            binding.editTextOtherDetails.setText(deviceToEdit.otherDetails)
            binding.addDeviceBtn.text = getString(R.string.update_button_label)
            binding.titletext.text = getString(R.string.update_your_device)
        } else {
            // Adding new device
            binding.addDeviceBtn.text = getString(R.string.add_button_label)
        }

        binding.addDeviceBtn.setOnClickListener {
            val deviceId = binding.editTextDeviceId.text.toString()
            val deviceType = binding.spinnerDeviceType.selectedItem.toString()
            val deviceName = binding.editTextDeviceName.text.toString()
            val model = binding.editTextModel.text.toString()
            val manufacturer = binding.editTextManufacturer.text.toString()
            val identificationId = binding.editTextDeviceIdentificationId.text.toString()
            val desc = binding.editTextOtherDetails.text.toString()

            if (validateInput(deviceId, deviceType, deviceName, model, manufacturer, identificationId, desc)) {
                val device = RoomDevice(
                    deviceId = deviceId,
                    employeeId = currentUser?.employeeId.toString(),
                    deviceType = deviceType,
                    deviceName = deviceName,
                    model = model,
                    manufacturer = manufacturer,
                    identificationId = identificationId,
                    otherDetails = desc,
                    date = (deviceToEdit?.date ?: System.currentTimeMillis()),
                )

                lifecycleScope.launch {
                    if (deviceToEdit != null) {
                        // Update existing device
                        viewModel.updateDevice(device)
                    } else {
                        // Add new device
                        viewModel.addDevice(device)
                    }
                }
            }
        }

        viewModel.addDevice.observe(viewLifecycleOwner) { state ->
            handleAddDeviceState(state, deviceToEdit != null)
        }

        viewModel.updateDevice.observe(viewLifecycleOwner) { state ->
            handleUpdateDeviceState(state)
        }
    }

    private fun handleAddDeviceState(state: UiState<RoomDevice>, isEditing: Boolean) {
        when (state) {
            is UiState.Loading -> {
                binding.addDeviceProgress.show()
            }
            is UiState.Failure -> {
                binding.addDeviceProgress.hide()
                toast(state.error)
            }
            is UiState.Success -> {
                binding.addDeviceProgress.hide()
                val message = if (isEditing) "Device has been updated" else "Device has been added"
                toast(message)
                findNavController().navigate(R.id.action_addNewDeviceFragment_to_deviceListingFragment)
            }
        }
    }

    private fun handleUpdateDeviceState(state: UiState<String>) {
        when (state) {
            is UiState.Loading -> {
                binding.addDeviceProgress.show()
            }
            is UiState.Failure -> {
                binding.addDeviceProgress.hide()
                toast(state.error)
            }
            is UiState.Success -> {
                binding.addDeviceProgress.hide()
                toast("Device has been updated")
                findNavController().navigate(R.id.action_addNewDeviceFragment_to_deviceListingFragment)
            }
        }
    }

    private fun getIndexForDeviceType(deviceType: String): Int {
        val deviceTypes = resources.getStringArray(R.array.device_types)
        return deviceTypes.indexOf(deviceType)
    }

    private fun validateInput(
        deviceId: String,
        deviceType: String,
        deviceName: String,
        model: String,
        manufacturer: String,
        os: String,
        desc: String
    ): Boolean {
        if (deviceId.isBlank() || deviceType.isBlank() || deviceName.isBlank() || model.isBlank() || manufacturer.isBlank()
            || os.isBlank() || desc.isBlank()
        ) {
            showSnackbar("Please fill in all fields")
            return false
        }
        return true
    }


}
