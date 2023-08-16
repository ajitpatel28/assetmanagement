package com.ajit.devicemanagement.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.databinding.DashboardCardLayoutBinding
import com.ajit.devicemanagement.databinding.HomeDeviceItemLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardListingAdapter(val onItemClicked: (Int, String) -> Unit) :
    RecyclerView.Adapter<DashboardListingAdapter.MyViewHolder>() {

    private var deviceTypesCountMap: Map<String, Int> = mapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DashboardCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val deviceType = deviceTypesCountMap.keys.elementAt(position)
        val deviceCount = deviceTypesCountMap[deviceType] ?: 0
        holder.bind(deviceType, deviceCount)
    }

    fun updateList(deviceList: List<RoomDevice>) {
        val countMap = deviceList.groupingBy { it.deviceType }.eachCount()
        deviceTypesCountMap = countMap
        notifyDataSetChanged()
        Log.e("DashboardAdapter", "Device Types Count Map: $deviceTypesCountMap")

    }

    override fun getItemCount(): Int {
        return deviceTypesCountMap.size
    }

    inner class MyViewHolder(private val binding: DashboardCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deviceType: String, deviceCount: Int) {
            binding.deviceTypeTextView.text = deviceType
            binding.deviceCountTextView.text = deviceCount.toString()
            Log.e("DashboardAdapter", "Device Type: $deviceType, Count: $deviceCount")


            binding.root.setOnClickListener {
                onItemClicked(adapterPosition, deviceType)
            }
        }
    }
}



