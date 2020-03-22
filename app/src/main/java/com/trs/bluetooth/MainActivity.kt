package com.trs.bluetooth

import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private var message = StringBuffer()
    private var bluetoothGatt: BluetoothGatt? = null
    private var mCharacteristic: BluetoothGattCharacteristic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
        txtMsg.text = message.append("消息提示\n").toString()
        button.setOnClickListener {
            startActivity(Intent(this,BluetoothSearchActivity::class.java))
        }
        btnSend.setOnClickListener {
            mCharacteristic?.value = edtSend.text.toString().toByteArray()
            txtMsg.text = message
                .append("发送的数据：")
                .append(edtSend.text.toString())
                .append("\n")
                .toString()
            bluetoothGatt?.writeCharacteristic(mCharacteristic)
        }
    }

    private fun getPermission() {
        AndPermission
            .with(this)
            .runtime()
            .permission(Permission.ACCESS_FINE_LOCATION)
            .onGranted{

            }
            .onDenied{
                Toast.makeText(this,"请授权",Toast.LENGTH_SHORT).show()
            }
            .start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(device: BluetoothDevice){
        bluetoothGatt = device.connectGatt(this,false,gattCallback)
    }

    private var gattCallback = object : BluetoothGattCallback(){
        /**
         * 连接状态
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    txtMsg.text = message.append("蓝牙连接成功\n").toString()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    txtMsg.text = message.append("蓝牙连断开\n").toString()
                }
            }
        }

        /**
         * 发现服务，在蓝牙连接的时候会调用
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            bluetoothGatt?.let {
                it.services.forEach { service ->
                    service.characteristics.forEach {
                        mCharacteristic = it
                    }
                }
            }
            bluetoothGatt?.setCharacteristicNotification(mCharacteristic,true)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            txtMsg.text = message
                .append("接收到数据：")
                .append(characteristic?.value.toString())
                .append("\n")
                .toString()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            when(status){
                BluetoothGatt.GATT_SUCCESS -> {
                    txtMsg.text = message.append("写入成功\n").toString()
                }
                BluetoothGatt.GATT_FAILURE -> {
                    txtMsg.text = message.append("写入失败\n").toString()
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            txtMsg.text = message
                .append("changed接收到数据：")
                .append(characteristic?.value.toString())
                .append("\n")
                .toString()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
