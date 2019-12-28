package com.trs.bluetooth.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.trs.bluetooth.R
import com.trs.bluetooth.adapter.holder.RecyclerViewHolder

class BluAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerViewHolder>() {
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
        holder.itemView.setOnClickListener{

        }
    }

    fun setData(devices : MutableList<BluetoothDevice>){
        this.devices = devices
    }

    fun addData(device : BluetoothDevice){
        if (!devices.contains(device)){
            devices.add(device)
        }
    }


}