package com.ajit.devicemanagement.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.data.provider.DataSyncManager
import com.ajit.devicemanagement.databinding.FragmentDashboardBinding
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.ui.device.DeviceListingFragment
import com.ajit.devicemanagement.ui.device.DeviceViewModel
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : BaseFragment() {

    lateinit var binding: FragmentDashboardBinding
    private val viewModel: DeviceViewModel by viewModels()
    private lateinit var dataSyncManager: DataSyncManager
    private val currentUser = LoginFragment.UserSession.loggedInUser


    private val deviceAdapter by lazy {
        DashboardListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_dashboardFragment_to_deviceListingFragment,
                    Bundle().apply {
                        putString("searchQuery", item)
                    }
                )
            }
        )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataSyncManager = DataSyncManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadData()


        // Set the adapter for the RecyclerView
        binding.dashboardRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceAdapter
        }


        binding.viewAllDevices.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_deviceListingFragment)

        }

        binding.apply {

            currentUser?.let {
                nametextView.text = "Hi, ${it.name}"
                ImageUtils.loadImageLocally(requireContext(), it.id, profileimageView)
            }
        }


    }


    private fun navigateToDeviceListingFragment1() {
        findNavController().navigate(R.id.action_dashboardFragment_to_deviceListingFragment)

        Handler(Looper.getMainLooper()).postDelayed({
            // Find the search view in the DeviceListingFragment and request focus
            val deviceListingFragment = findNavController().currentDestination?.id == R.id.deviceListingFragment
            if (deviceListingFragment) {
                val searchView = requireActivity().findViewById<SearchView>(R.id.searchView)
                searchView.requestFocus()
            }
        }, 300)
    }


    private fun loadData(){
        viewModel.getDevices()
        viewModel.devices.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    if (state.data.isEmpty()) {
                        binding.noDevicesFound.visibility = View.VISIBLE
                        deviceAdapter.updateList(state.data.toMutableList())

                    } else {
                        binding.noDevicesFound.visibility = View.GONE
                        deviceAdapter.updateList(state.data.toMutableList())
                    }
                }
                else -> {}
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.dashboardSearchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                navigateToDeviceListingFragment1()
            }
        }
        loadData()

    }

    override fun onPause() {
        super.onPause()
        binding.dashboardSearchView.clearFocus()
    }




}