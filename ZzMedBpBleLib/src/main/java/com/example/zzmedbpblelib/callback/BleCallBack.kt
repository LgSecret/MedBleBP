package com.example.zzmedbpblelib.callback

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import com.example.zzmedbpblelib.utils.BpDataResult

interface BleCallBack {
    //没有打开蓝牙
   fun onFailure(error: FailureEnum, errorStr: String)
   //扫描列表
   fun scanList(mDeviceList: List<BluetoothDevice>)
   //连接成功
   fun onConnectSuccess(mDevice: BluetoothDevice,mAddress: String,mDeviceName:String)
   //获取数据
   fun onDataResult(data: BpDataResult)
}