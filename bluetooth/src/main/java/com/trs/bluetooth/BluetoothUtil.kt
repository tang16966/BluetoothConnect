package com.trs.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

object BluetoothUtil{

    fun isEnabled(context: Context) : Boolean{
        return getBluetoothAdapter(context).isEnabled
    }

    fun getBluetoothAdapter(context: Context) : BluetoothAdapter{
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

}