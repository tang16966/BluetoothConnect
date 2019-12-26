package com.trs.bluetooth.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trs.bluetooth.R
import com.trs.bluetooth.adapter.holder.RecyclerViewHolder

class BluAdapter(val context: Context) : RecyclerView.Adapter<RecyclerViewHolder>() {
    private var devices : MutableList<BluetoothDevice> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(View.inflate(context, R.layout.item_bluetooth,null))
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        var tvName = holder.getView<TextView>(R.id.tv_name)
        tvName.text = devices[position].name
    }

    fun setData(devices : MutableList<BluetoothDevice>){
        this.devices = devices
    }

    fun addData(device : BluetoothDevice){
        devices.add(device)
    }


}