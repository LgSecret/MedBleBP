package com.example.zzmedbpblelib.utils

import android.app.Application
import androidx.core.content.FileProvider
import com.example.zzmedbpblelib.ZzMedBleManager

class ZzMedBleInitProvider : FileProvider(){
    override fun onCreate(): Boolean {
        //初始化
        ZzMedBleManager.init(context?.applicationContext as Application)
        return true
    }
}