package com.example.zzmedble

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BleListAdapter : BaseQuickAdapter<BluetoothDevice,BaseViewHolder>(R.layout.item_ble){
    override fun convert(holder: BaseViewHolder, item: BluetoothDevice) {
        holder.apply {
            item.run {
                setText(R.id.name,name)
                setText(R.id.mac,address)
            }
        }
    }
}