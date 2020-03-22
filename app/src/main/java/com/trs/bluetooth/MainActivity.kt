package com.trs.bluetooth

import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {
    private var message = StringBuffer()
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null
    private lateinit var coroutineScope: CoroutineScope


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        txtMsg.text = message.append("消息提示\n").toString()
        button.setOnClickListener {
            startActivityForResult(Intent(this,BluetoothSearchActivity::class.java),200)
        }
        btnSend.setOnClickListener {
            writeCharacteristic?.value = edtSend.text.toString().toByteArray()
            writeCharacteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            txtMsg.text = message
                .append("发送的数据：${edtSend.text}\n")
                .toString()
            bluetoothGatt?.writeCharacteristic(writeCharacteristic)
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
    fun onMessageEvent(event: BleEvent){
        txtMsg.text = message.append("蓝牙正在连接...${event.device.name}...${event.device.address}\n").toString()
        bluetoothGatt = event.device.connectGatt(this,false,gattCallback)
    }



    private var gattCallback = object : BluetoothGattCallback(){
        /**
         * 连接状态
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    coroutineScope.launch{
                        delay(500)
                        withContext(Dispatchers.Main){
                            txtMsg.text = message.append("蓝牙连接成功\n").toString()
                            gatt.discoverServices()
                        }
                    }
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
                    message.append("{\n")
                    service.characteristics.forEach {
                        if (it.uuid == UUID.fromString(JDY_TAG_CHAR_DATA_UUID)){
                            notifyCharacteristic = it
                            writeCharacteristic = it
                        }
                        message.append("uuid:${it.uuid}\n")
                    }
                    coroutineScope.launch{
                        txtMsg.text = message.append("}\n").toString()
                    }
                }
                bluetoothGatt?.setCharacteristicNotification(notifyCharacteristic,true)
                coroutineScope.launch{
                    txtMsg.text = message.append("连接成功的uuid:${notifyCharacteristic?.uuid}").toString()
                }

            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            coroutineScope.launch{
                txtMsg.text = message
                    .append("接收到数据：${characteristic?.value.toString()}\n")
                    .toString()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            coroutineScope.launch{
                when(status){
                    BluetoothGatt.GATT_SUCCESS -> {
                        txtMsg.text = message.append("写入成功\n").toString()
                    }
                    BluetoothGatt.GATT_FAILURE -> {
                        txtMsg.text = message.append("写入失败\n").toString()
                    }
                }
            }

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            coroutineScope.launch{
                txtMsg.text = message
                    .append("changed接收到数据：${characteristic?.value.toString()}\n")
                    .toString()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
        EventBus.getDefault().unregister(this)
    }
}
