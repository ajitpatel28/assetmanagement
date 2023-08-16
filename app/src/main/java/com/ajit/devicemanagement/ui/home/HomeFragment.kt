package com.ajit.devicemanagement.ui.home

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.provider.DataSyncManager
import com.ajit.devicemanagement.databinding.FragmentHomeBinding
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.ui.device.DeviceListingAdapter
import com.ajit.devicemanagement.ui.device.DeviceViewModel
import com.ajit.devicemanagement.util.*
import com.ajit.devicemanagement.util.ImageUtils.saveImageLocally
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var searchView: SearchView
    private val viewModel:DeviceViewModel by viewModels()
    private lateinit var dataSyncManager: DataSyncManager
    private val currentUser = LoginFragment.UserSession.loggedInUser


    private val adapter by lazy {
        HomeDeviceListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_homeFragment_to_deviceDetailFragment,
                    Bundle().apply {
                        putParcelable("device", item)
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
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadData()
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        binding.homeRecyclerView.adapter = adapter

        binding.noDevicesFound.visibility = View.GONE
//
//        binding.addDeviceButton.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_addNewDeviceFragment)
//        }
//
//        binding.homeBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_self2)
//        }

        binding.userProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
        }


//        binding.myDevices.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_deviceListingFragment)
//
//        }

        binding.viewAllDevices.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_deviceListingFragment)

        }

        binding.apply {

            currentUser?.let {
                nametextView.text = "Hi, ${it.name}"
                ImageUtils.loadImageLocally(requireContext(), it.id, profileimageView)
            }
        }




        searchView = binding.searchView

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_deviceListingFragment)
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navigateToDeviceListingFragment(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                navigateToDeviceListingFragment(newText)
                return true
            }

        })


        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish() // Close the app
            }
        }

        // Add the BackPressedCallback to the OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    private fun navigateToDeviceListingFragment(query: String?) {
        findNavController().navigate(R.id.action_homeFragment_to_deviceListingFragment,Bundle().apply {
            putString("searchQuery",query)
        })
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
                        adapter.updateList(state.data.toMutableList())
                    } else {
                        binding.noDevicesFound.visibility = View.GONE
                        adapter.updateList(state.data.toMutableList())
                    }
                }
                else -> {}
            }
        }
    }


    override fun onResume() {
        super.onResume()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        loadData()

    }

    override fun onPause() {
        super.onPause()

        // Restore the original soft input mode when the fragment is paused
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

}