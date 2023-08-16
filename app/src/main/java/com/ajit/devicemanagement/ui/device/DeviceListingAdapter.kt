package com.ajit.devicemanagement.ui.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.databinding.ItemDeviceLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class DeviceListingAdapter(
    val onItemClicked: (Int, RoomDevice) -> Unit
) : RecyclerView.Adapter<DeviceListingAdapter.MyViewHolder>() {

    val sdf = SimpleDateFormat("dd MMM yyyy")
    private var list: MutableList<RoomDevice> = arrayListOf()
    private var originalList: MutableList<RoomDevice> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemDeviceLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<RoomDevice>) {
        this.originalList = list
        this.list = list.toMutableList() // Create a copy of the list for filtering
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ItemDeviceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RoomDevice) {
            binding.date.text = sdf.format(Date(item.date))
            binding.deviceTypeValue.text = item.deviceType
            binding.deviceIdValue.text = item.deviceId
            binding.deviceNameValue.text = item.deviceName
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }

        }
    }
}