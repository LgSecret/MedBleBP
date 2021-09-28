package com.example.zzmedbpblelib.callback

enum class FailureEnum {
    NOT_SUPPORT_BLE,//本设备不支持蓝牙
    NOT_OPEN_BLE,//没有打开蓝牙
    CONNECT_FAIL,//连接失败
    DATA_FAIL //获取数据失败
}