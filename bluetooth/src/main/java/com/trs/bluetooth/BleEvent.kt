package com.trs.bluetooth

import android.bluetooth.BluetoothDevice

data class BleEvent(
    var device: BluetoothDevice
)