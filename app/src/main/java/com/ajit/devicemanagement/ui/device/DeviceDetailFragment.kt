package com.ajit.devicemanagement.ui.device

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.databinding.FragmentDeviceDetailBinding
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.UiState
import com.ajit.devicemanagement.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DeviceDetailFragment : BaseFragment() {

    val TAG = "DeviceDetailFragment"
    lateinit var binding: FragmentDeviceDetailBinding
    val viewModel: DeviceViewModel by viewModels()
    private var isExpanded = false

    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
    }
    private val rotateAntiClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_anti_clock_wise)
    }
    private val sdf = SimpleDateFormat("dd MMM yyyy")






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeviceDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val device = arguments?.getParcelable<RoomDevice>("device")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isExpanded) {
                    shrinkFab()
                    isExpanded= false
                } else {
                    val currentDestination = findNavController().currentDestination?.id

                    if (currentDestination == R.id.deviceDetailFragment) {
                        findNavController().navigateUp()
                    }

                }
            }
        })

        device?.let {
            displayDeviceDetails(it)
        }


        binding.deviceDetailBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.deviceDetailEditBtn.setOnClickListener {
            device?.let {
                val bundle = Bundle().apply {
                    putParcelable("device", it)
                }
                findNavController().navigate(
                    R.id.action_deviceDetailFragment_to_addNewDeviceFragment,
                    bundle
                )
            }
        }

        binding.deviceDetailDeleteBtn.setOnClickListener {
            device?.let {
                viewModel.deleteDevice(it)
                Log.e(TAG, "device deleted")
            }
        }


        binding.mainFabBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isExpanded) {
                        expandFab()
                    } else {
                        val outRect = Rect()
                        binding.deviceDetailEditBtn.getGlobalVisibleRect(outRect)
                        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                            shrinkFab()
                        }
                    }
                }
            }
            false // Return false to allow other touch events to be handled
        }


        viewModel.deleteDevice.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    toast(state.error)
                }
                is UiState.Success -> {
                    toast("Device has been deleted")
                    findNavController().navigate(R.id.action_deviceDetailFragment_to_deviceListingFragment)
                }
            }
        }
    }



    private fun displayDeviceDetails(device: RoomDevice) {
        with(binding) {
            date.text = sdf.format(Date(device.date))
            deviceDetailIdValue.text = device.deviceId
            deviceDetailTypeValue.text = device.deviceType
            deviceDetailNameValue.text = device.deviceName
            deviceDetailModelValue.text = device.model
            deviceDetailManufacturerValue.text = device.manufacturer
            deviceDetailIdentificationIdValue.text = device.identificationId
            deviceDetailDescValue.text = device.otherDetails
        }
    }

    private fun shrinkFab() {

        binding.mainFabBtn.startAnimation(rotateAntiClockWiseFabAnim)
        binding.deviceDetailEditBtn.startAnimation(toBottomFabAnim)
        binding.deviceDetailDeleteBtn.startAnimation(toBottomFabAnim)

        isExpanded = !isExpanded

        binding.mainFabBtn.setImageResource(R.drawable.baseline_add_circle_24)

    }

    private fun expandFab() {


        binding.deviceDetailEditBtn.startAnimation(fromBottomFabAnim)
        binding.deviceDetailDeleteBtn.startAnimation(fromBottomFabAnim)

        isExpanded = !isExpanded
        binding.mainFabBtn.setImageResource(R.drawable.baseline_close_24)


    }


}
