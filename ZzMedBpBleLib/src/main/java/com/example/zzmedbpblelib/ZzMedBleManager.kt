package com.example.zzmedbpblelib

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.content.Context
import android.widget.Toast
import com.example.zzmedbpblelib.callback.BleCallBack
import com.example.zzmedbpblelib.callback.BleTypeEnum
import com.example.zzmedbpblelib.callback.FailureEnum
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import java.util.*
import com.example.zzmedbpblelib.fastble.BleManager
import com.example.zzmedbpblelib.fastble.callback.BleGattCallback
import com.example.zzmedbpblelib.fastble.callback.BleIndicateCallback
import com.example.zzmedbpblelib.fastble.callback.BleScanCallback
import com.example.zzmedbpblelib.fastble.data.BleDevice
import com.example.zzmedbpblelib.fastble.data.BleScanState
import com.example.zzmedbpblelib.fastble.exception.BleException
import com.example.zzmedbpblelib.fastble.scan.BleScanRuleConfig
import com.example.zzmedbpblelib.utils.BleUtils
import com.example.zzmedbpblelib.utils.DataUtils
import kotlinx.coroutines.*
import java.lang.Exception

class ZzMedBleManager {

    private  var bindBleDevice: String = ""

    private var deviceIdNow: String? = ""
    private lateinit var connectBleType : BleTypeEnum
    private lateinit var callback: BleCallBack
    private lateinit var mContext: Context
    private var mAddress: String = ""
    //欧姆龙血压计对应的uuid
    private var mServiceUUID: String = "00001810-0000-1000-8000-00805f9b34fb"
    private var mChartUUID: String = "00002a35-0000-1000-8000-00805f9b34fb"

    companion object{
        private var mApp: Application? = null
        @JvmStatic
        fun getInstance() = ZzMedBleManager()
        @JvmStatic
        fun init(app: Application?){
            if (mApp==null) {
                mApp = app

                //初始化蓝牙连接库
                BleManager.getInstance().init(app)
                BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(10, 5000).operateTimeout = 5000
            }
        }
    }

    /**
     * Get the Context
     *
     * @return
     */
    fun getContext(): Context {
        return mContext
    }

    /**
     * Get the BleManager
     *
     * @return
     */
    fun getBleManager(): BleManager? {
        return BleManager.getInstance()
    }

    /**
     * 配置蓝牙过滤扫描规则
     */
    fun setScanRuleConfig(uuids: Array<UUID>) : ZzMedBleManager{
        val scanRuleConfig = BleScanRuleConfig.Builder()
            .setServiceUuids(uuids)
            .setScanTimeOut(-1)
            .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
        return this@ZzMedBleManager
    }

    /**
     * 连接血糖设备还是血压设备
     */
    fun setBleType(bleType : BleTypeEnum):ZzMedBleManager{
        connectBleType = bleType
        return this@ZzMedBleManager
    }

    /**
     * 配置筛选蓝牙的参数
     */
    fun setBleAddress(address:String):ZzMedBleManager{
        this.mAddress = address
        return this@ZzMedBleManager
    }

    /**
     * 配置BleServiceUuid
     */
    fun setBleServiceUuid(serviceUUId:String):ZzMedBleManager{
        this.mServiceUUID = serviceUUId
        return this@ZzMedBleManager
    }

    /**
     * 配置BleCharUuid
     */
    fun setBleCharUuid(charUUId:String):ZzMedBleManager{
        this.mChartUUID = charUUId
        return this@ZzMedBleManager
    }

    /**
     * 打开蓝牙 开始扫描
     */
    fun startBle(context: Context, mCallback: BleCallBack) {
        mContext = context
        callback = mCallback
        //是否支持蓝牙
        if (BleManager.getInstance().isSupportBle) {
            //是否开启蓝牙
            if (BleManager.getInstance().isBlueEnable) {
                //判断是否已经绑定蓝牙
                val deviceList = BleManager.getInstance().allConnectedDevice
                if (deviceList.isNotEmpty()&&deviceList.size>0) {
                    BleManager.getInstance().allConnectedDevice.forEach {
                        if (it.mac!=null && it.mac == mAddress) {
                            delConnect(it.mac, callback)
//                            Handler(Looper.getMainLooper()).postDelayed({
//
//                            }, 500)
                        }
                    }
                }else {
                    stopScan()
                    startScan(callback)
                }
            } else {
                callback.onFailure(FailureEnum.NOT_OPEN_BLE,"蓝牙未开启")
            }
        } else {
            Toast.makeText(getContext(), "此设备目前不支持蓝牙功能", Toast.LENGTH_SHORT).show()
            callback.onFailure(FailureEnum.NOT_SUPPORT_BLE,"此设备目前不支持蓝牙功能")
        }
    }


    /**
     * 开始扫描
     */
    fun startScan(callback: BleCallBack):ZzMedBleManager{
        //扫描蓝牙
        BleManager.getInstance().scan(object : BleScanCallback() {
            val scanList = mutableListOf<BluetoothDevice>()

            override fun onScanStarted(success: Boolean) {
                Log.d("======", "开始扫描")
            }
            @SuppressLint("MissingPermission")
            override fun onLeScan(bleDevice: BleDevice) {}
            @SuppressLint("MissingPermission")
            override fun onScanning(bleDevice: BleDevice) {
                scanList.add(bleDevice.device)
                callback.scanList(scanList)

                Log.d("======设备名称", ""+bleDevice.name)
                if (mAddress.isNotEmpty() && bleDevice.mac!=null && bleDevice.mac == mAddress) {
                    Log.d("======", "扫描成功")
                    bleDevice.device.createBond()
                    stopScan()
                    delConnect(bleDevice.mac, callback)
                }

            }
            override fun onScanFinished(scanResultList: List<BleDevice>) {

            }
        })
        return this
    }

    /**
     * 停止搜索
     */
    fun stopScan() {
        if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING) {//判断是否正在搜索
            BleManager.getInstance().cancelScan()
        }
    }

    /**
     * 断开所有蓝牙连接
     */
    fun stopConnectBleAll(){
        BleManager.getInstance().disconnectAllDevice()
    }


    /**
     * 解除蓝牙绑定
     */
    fun stopBinding(address: String): Boolean {
        val bTAdapter = BluetoothAdapter.getDefaultAdapter()
        val devices: Set<*> = bTAdapter.bondedDevices
        val var3 = devices.iterator()
        while (var3.hasNext()) {
            val device = var3.next() as BluetoothDevice
            if (TextUtils.isEmpty(device.address)) continue
            if (!device.address.isNullOrEmpty() && device.address == address) {
                return BleUtils.unBond(device)
            }
        }
        return false
    }

    /**
     * 开始连接
     */
    fun delConnect(bleDevice: String?,callback: BleCallBack){
            BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
                override fun onStartConnect() {
                    Log.d("======", "开始连接")
                }
                override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                    callback.onFailure(FailureEnum.CONNECT_FAIL,exception.toString())
                    Log.d("======", "连接失败$exception")

                }

                @SuppressLint("NewApi", "MissingPermission")
                override fun onConnectSuccess(
                    bleDevice: BleDevice,
                    gatt: BluetoothGatt,
                    status: Int,
                ) {

                    Log.d("======", "连接成功")
                    callback.onConnectSuccess(bleDevice.device,bleDevice.mac,bleDevice.name)

                    //在BleGattCallback中onConnectSuccess回调调用
                    if (gatt.getService(UUID.fromString(mServiceUUID)) == null) {
                        //判断service是否为空
                        Handler(Looper.getMainLooper()).postDelayed({
                            gatt.discoverServices()
                        }, 500)
                        return
                    }

                    bindBleDevice = bleDevice.mac
                    BleManagerNotify(bleDevice,callback)
//                    Handler(Looper.getMainLooper()).postDelayed({
//
//                    }, 500)
                }

                override fun onDisConnected(
                    isActiveDisConnected: Boolean,
                    bleDevice: BleDevice,
                    gatt: BluetoothGatt,
                    status: Int,
                ) {
                }
            })
    }

    //重新连接
    @SuppressLint("MissingPermission")
    fun bleConnectAgain(){
        if (bindBleDevice.isNotEmpty()){
            val device = BleManager.getInstance().bluetoothAdapter.getRemoteDevice(bindBleDevice)
            if (device.address!=null && device.address == mAddress) {//判断是否是正确的设备
                stopScan()
                delConnect(bindBleDevice, callback)
            }
        }
    }

    /**
     * 获取传递的数据
     */
    fun BleManagerNotify(bleDevice: BleDevice,callback: BleCallBack){
        BleManager.getInstance().indicate(
            bleDevice,
            mServiceUUID,
            mChartUUID,
            object : BleIndicateCallback() {
                override fun onIndicateSuccess() {
                    Log.d("======", "打开通知成功")
                }

                override fun onIndicateFailure(exception: BleException?) {
                    Log.d("======", "获取数据失败$exception")
                    callback.onFailure(FailureEnum.DATA_FAIL,exception.toString())
                    //获取数据之后重新扫描
                    startScan(callback)
                }

                override fun onCharacteristicChanged(data: ByteArray) {
                    // 打开通知后，设备发过来的数据将在这里出现
                    Log.d("======", "获取数据成功${data}")
                    var callData =  DataUtils.setData(mContext,data)
                    callback.onDataResult(callData)
                    //获取数据之后重新扫描
                    startScan(callback)
                }
            })
    }
}