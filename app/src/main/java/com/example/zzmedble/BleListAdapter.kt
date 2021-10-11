package com.example.zzmedble

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BleListAdapter : BaseQuickAdapter<BleDataBean,BaseViewHolder>(R.layout.item_ble){
    override fun convert(holder: BaseViewHolder, item: BleDataBean) {
        holder.apply {
            item.run {
                setText(R.id.name,"BDA===:${address.replace(":","")}")
                setText(R.id.mac,address)
            }
        }
    }
}