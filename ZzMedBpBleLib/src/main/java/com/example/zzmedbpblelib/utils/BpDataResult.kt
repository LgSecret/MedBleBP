package com.example.zzmedbpblelib.utils

data class BpDataResult(
    var systolic: Short, //高压
    var diastolic: Short, //低压
    var pulse: Short,  //脉搏
    var movement:Boolean,  //身体移动状态
    var irregular:Boolean, //心率不齐状态
    var start_time:String) //测量时间
