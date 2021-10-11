package com.example.zzmedble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zzmedbpblelib.callback.BleCallBack
import com.example.zzmedbpblelib.callback.FailureEnum

import com.example.zzmedbpblelib.ZzMedBleManager
import com.example.zzmedbpblelib.fastble.data.BleDevice
import com.example.zzmedbpblelib.utils.BpDataResult
import com.example.zzmedbpblelib.utils.GattUUID
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class HomeActivity : AppCompatActivity(){

    private val REQUEST_LOCATION_PERMISSIONS = 0x123
    private lateinit var bleAdapter:BleListAdapter
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.layoutManager = LinearLayoutManager(this)
        bleAdapter = BleListAdapter()
        recycler.adapter = bleAdapter
        bleAdapter.addChildClickViewIds(R.id.connect)
        bleAdapter.setOnItemChildClickListener { adapter, view, position ->
            //获取选择连接蓝牙的mac地址进行连接
            address = bleAdapter.data[position].address
            val btn = view as Button
            btn.text = "连接中"
            onScan()
        }
        address = getSpValue("ble",this@HomeActivity,"address","")
        //扫描
        btn.setOnClickListener {
            onScan()
        }

        dis_connect.setOnClickListener {
          ZzMedBleManager.getInstance().stopBinding(address)
        }

        stopScan.setOnClickListener {
            ZzMedBleManager.getInstance().stopScan()
        }

    }

    private fun onScan(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS)
        }else{
            val uuids = arrayOf<UUID>(GattUUID.Service.BloodPressureService.uuid)
            ZzMedBleManager.getInstance()
                .setScanRuleConfig(uuids)
                .setBleAddress(address)
                .startBle(this,object : BleCallBack {

                    override fun onFailure(error: FailureEnum, errorStr: String) {

                    }

                    override fun scanList(mDeviceList: List<BluetoothDevice>) {
                        val bleBeanList = mutableListOf<BleDataBean>()
                        mDeviceList.forEach {
                            bleBeanList.add(BleDataBean(it.name,it.address,it.name.substring(DeviceType.BLOOD_9200X.prefix.length)))
                        }

                        bleAdapter.setNewInstance(bleBeanList)
                    }

                    override fun onConnectSuccess(mDevice: BluetoothDevice,mAddress: String,mDeviceName:String) {
                        Toast.makeText(this@HomeActivity,"连接成功",Toast.LENGTH_SHORT).show()
                        //存储最新的mac到本地
                        putSpValue("ble",this@HomeActivity,"address",mAddress)
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onDataResult(data: BpDataResult) {
                        tv.text = "测量数据=====\n高压--${data.systolic}\n低压--${data.diastolic}" +
                                "\n脉搏--${data.pulse},\nmovement--${data.movement},\nirregular--${data.irregular},\ntime${data.start_time}"
                    }

                })
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val uuids = arrayOf<UUID>(GattUUID.Service.BloodPressureService.uuid)
                ZzMedBleManager.getInstance()
                    .setScanRuleConfig(uuids)
                    .setBleAddress(address)
                    .startBle(this, object : BleCallBack {

                        override fun onFailure(error: FailureEnum, errorStr: String) {

                        }

                        override fun scanList(mDeviceList: List<BluetoothDevice>) {
                            val bleBeanList = mutableListOf<BleDataBean>()
                            mDeviceList.forEach {
                                bleBeanList.add(BleDataBean(it.name,it.address,it.name.substring(DeviceType.BLOOD_9200X.prefix.length)))
                            }

                            bleAdapter.setNewInstance(bleBeanList)
                        }

                        override fun onConnectSuccess(mDevice: BluetoothDevice,mAddress: String,mDeviceName:String) {
                            Toast.makeText(this@HomeActivity,"连接成功",Toast.LENGTH_SHORT).show()
                            //存储最新的mac到本地
                            putSpValue("ble",this@HomeActivity,"address",mAddress)
                        }

                        @SuppressLint("SetTextI18n")
                        override fun onDataResult(data: BpDataResult) {
                            tv.text = "测量数据=====\n高压--${data.systolic}\n低压--${data.diastolic}" +
                                    "\n脉搏--${data.pulse},\nmovement--${data.movement},\nirregular--${data.irregular},\ntime${data.start_time}"
                        }

                    })

            }
        }

    }

}