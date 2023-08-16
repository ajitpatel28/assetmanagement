package com.ajit.devicemanagement.ui.device

import com.ajit.devicemanagement.data.provider.DataSyncManager
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.databinding.FragmentDeviceListingBinding
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class DeviceListingFragment : BaseFragment() {

    private val TAG = "DeviceListingFragment"
    private lateinit var binding: FragmentDeviceListingBinding
    private val viewModel: DeviceViewModel by viewModels()
    private lateinit var dataSyncManager: DataSyncManager
    private lateinit var searchView: SearchView
    private val networkChangeReceiver = NetworkChangeReceiver()
    private val adapter by lazy {
        DeviceListingAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(
                    R.id.action_deviceListingFragment_to_deviceDetailFragment,
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
        binding = FragmentDeviceListingBinding.inflate(layoutInflater)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


        loadData()



        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.recyclerView.adapter = adapter



        searchView = binding.searchView

        val query = arguments?.getString("searchQuery")
        if (!query.isNullOrEmpty()) {
            Log.e(TAG,"$query")
            searchView.setQuery(query, false)
            searchView.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
            filterList(query)
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

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
                        // Show the empty list layout if the list is empty
                        binding.noDevicesFound.visibility = View.VISIBLE
                        adapter.updateList(state.data.toMutableList())
                    } else {
                        // Hide the empty list layout if the list is not empty
                        binding.noDevicesFound.visibility = View.GONE
                        adapter.updateList(state.data.toMutableList())
                    }
                }
                else -> {}
            }
        }
    }

    private fun filterList(query: String?) {
        val deviceListState = viewModel.devices.value
        if (query != null && deviceListState is UiState.Success) {
            val deviceList = deviceListState.data
            val filteredList = deviceList.filter { item ->
                item.deviceName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                        item.deviceType.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                        item.deviceId.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }

            if (filteredList.isNullOrEmpty()) {
                binding.noDevicesFound.visibility = View.VISIBLE
                adapter.updateList(mutableListOf())
                toast("No Data found")
            } else {
                binding.noDevicesFound.visibility = View.GONE
                adapter.updateList(filteredList.toMutableList())
            }
        } else {
            binding.noDevicesFound.visibility = View.GONE
            adapter.updateList(mutableListOf()) // Clear the adapter when the search query is null or the device list state is not success
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the network change receiver
        requireContext().unregisterReceiver(networkChangeReceiver)
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







