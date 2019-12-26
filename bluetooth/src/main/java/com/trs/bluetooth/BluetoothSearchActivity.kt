package com.trs.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.trs.bluetooth.adapter.BluAdapter
import kotlinx.android.synthetic.main.activity_bluetooth_serach.*

class BluetoothSearchActivity : AppCompatActivity() {
    private lateinit var bluetoothAdapter : BluetoothAdapter
    private var mScanning: Boolean = false
    private val handler = Handler()
    private var adapter : BluAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_serach)
        toolbar.setNavigationOnClickListener{
            finish()
        }
        initView()
        initBluetooth()

    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothUtil.getBluetoothAdapter(this)
        if (bluetoothAdapter.isEnabled){
            scanLeDevice(true)
        }else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 200)
        }
    }


    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                handler.postDelayed({
                    mScanning = false
                    bluetoothAdapter.stopLeScan(leScanCallback)
                    Log.e("------------","结束搜索")
                }, 10000)
                mScanning = true
                Log.e("------------","开始搜索")
                bluetoothAdapter.startLeScan(leScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothAdapter.stopLeScan(leScanCallback)
            }
        }
    }


    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            if (device.name!=null){
                adapter?.addData(device)
                adapter?.notifyDataSetChanged()
            }

        }
    }

    private fun initView() {
        adapter = BluAdapter(this)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
    }


}
