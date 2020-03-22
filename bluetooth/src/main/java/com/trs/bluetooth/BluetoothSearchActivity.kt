package com.trs.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.trs.bluetooth.adapter.BluAdapter
import kotlinx.android.synthetic.main.activity_bluetooth_serach.*
import org.greenrobot.eventbus.EventBus

class BluetoothSearchActivity : AppCompatActivity(){
    private val TAG = "BluetoothSearchActivity"
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

    private fun initRefresh() {
        refresh.setColorSchemeColors(resources.getColor(R.color.colorTheme))
        refresh.setOnRefreshListener{
            if (!mScanning){
                scanLeDevice(true)
            }
        }
        refresh.post {
            refresh.isRefreshing = true
            if (!mScanning){
                scanLeDevice(true)
            }
        }
    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothUtil.getBluetoothAdapter(this)
        if (bluetoothAdapter.isEnabled){
            initRefresh()
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
                    refresh.isRefreshing = false
                    Log.e(TAG,"结束搜索")
                }, 3000)
                mScanning = true
                Log.e(TAG,"开始搜索")
                bluetoothAdapter.startLeScan(leScanCallback)
            }
            else -> {
                mScanning = false
                refresh.isRefreshing = false
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
        adapter?.setOnItemClickListener(object : BluAdapter.OnItemClickListener{
            override fun onClick(position: Int, device: BluetoothDevice) {
                EventBus.getDefault().post(BleEvent(device))
                finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter.stopLeScan(leScanCallback)
    }




}
