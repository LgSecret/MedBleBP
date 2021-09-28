package com.example.zzmedbpblelib.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

object  DataUtils {
    /**
     * 设置数据
     */
    fun setData(context:Context?,data: ByteArray):BpDataResult{
        var idx = 0
        val buf = ByteArray(2)
        val flags = data[idx++]

        // 0: mmHg	1: kPa

        // 0: mmHg	1: kPa
        val kPa: Boolean = flags and 0x01 > 0
        // 0: No Timestamp info 1: With Timestamp info
        // 0: No Timestamp info 1: With Timestamp info
        val timestampFlag: Boolean = flags and 0x02 > 0
        // 0: No PlseRate info 1: With PulseRate info
        // 0: No PlseRate info 1: With PulseRate info
        val pulseRateFlag: Boolean = flags and 0x04 > 0
        // 0: No UserID info 1: With UserID info
        // 0: No UserID info 1: With UserID info
        val userIdFlag: Boolean = flags and 0x08 > 0
        // 0: No MeasurementStatus info 1: With MeasurementStatus info
        // 0: No MeasurementStatus info 1: With MeasurementStatus info
        val measurementStatusFlag: Boolean = flags and 0x10 > 0

        // Set BloodPressureMeasurement unit

        // Set BloodPressureMeasurement unit
        val unit: String
        unit = if (kPa) {
            "kPa"
        } else {
            "mmHg"
        }

        // Parse Blood Pressure Measurement

        // Parse Blood Pressure Measurement
        var systolicVal: Short = 0
        var diastolicVal: Short = 0
        var meanApVal: Short = 0

        System.arraycopy(data, idx, buf, 0, 2)
        idx += 2
        var byteBuffer = ByteBuffer.wrap(buf)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        systolicVal = byteBuffer.getShort()

        System.arraycopy(data, idx, buf, 0, 2)
        idx += 2
        byteBuffer = ByteBuffer.wrap(buf)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        diastolicVal = byteBuffer.getShort()

        System.arraycopy(data, idx, buf, 0, 2)
        idx += 2
        byteBuffer = ByteBuffer.wrap(buf)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        meanApVal = byteBuffer.getShort()

        Log.d("======", "systolicValue:$systolicVal $unit")
        Log.d("======", "systolicValue:$diastolicVal $unit")
        Log.d("======", "systolicValue:$meanApVal $unit")

        // Parse Timestamp

        // Parse Timestamp
        var timestampStr = "----"
        var dateStr = "--"
        var timeStr = "--"
        if (timestampFlag) {
            System.arraycopy(data, idx, buf, 0, 2)
            idx += 2
            byteBuffer = ByteBuffer.wrap(buf)
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
            val year: Int = byteBuffer.getShort().toInt()
            val month = data[idx++].toInt()
            val day = data[idx++].toInt()
            val hour = data[idx++].toInt()
            val min = data[idx++].toInt()
            val sec = data[idx++].toInt()
            dateStr = String.format(Locale.US, "%1$04d-%2$02d-%3$02d", year, month, day)
            timeStr = String.format(Locale.US, "%1$02d:%2$02d:%3$02d", hour, min, sec)
            timestampStr = "$dateStr $timeStr"
            Log.d("======", "Timestamp Data:$timestampStr")
        }

        // Parse PulseRate

        // Parse PulseRate
        var pulseRateVal: Short = 0
        var pulseRateStr = "----"
        if (pulseRateFlag) {
            System.arraycopy(data, idx, buf, 0, 2)
            idx += 2
            byteBuffer = ByteBuffer.wrap(buf)
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
            pulseRateVal = byteBuffer.getShort()
            pulseRateStr = java.lang.Short.toString(pulseRateVal)
            Log.d( "======","PulseRate Data:$pulseRateStr")
        }

        // Parse UserID

        // Parse UserID
        var userIDVal = 0
        var userIDStr = "----"
        if (userIdFlag) {
            userIDVal = data[idx++].toInt()
            userIDStr = userIDVal.toString()
            Log.d("======", "UserID Data:$userIDStr")
        }

        // Parse Measurement Status

        // Parse Measurement Status
        var measurementStatusVal = 0
        var measurementStatusStr = "----"
        var movement = false
        var irregular = false
        if (measurementStatusFlag) {
            System.arraycopy(data, idx, buf, 0, 2)
            idx += 2
            byteBuffer = ByteBuffer.wrap(buf)
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
            measurementStatusVal = byteBuffer.getShort().toInt()
            measurementStatusStr = String.format(Locale.US, "%1$04x",
                measurementStatusVal.toShort())
            Log.d("======", "MeasurementStatus Data:$measurementStatusStr")
            movement = measurementStatusVal and 0x0001 != 0
            irregular = measurementStatusVal and 0x0004 != 0
        }

        // Output to History

        // Output to History
        val entry = (timestampStr
                + "systolicVal," + systolicVal
                + "diastolicVal," + diastolicVal
                + "meanApVal," + meanApVal
                + "pulseRateStr," + pulseRateStr
                + "measurementStatusStr," + String.format(Locale.US, "%1$02x", flags) + "," + measurementStatusStr)
        Log.d("======", entry)
        Toast.makeText(context, "获取测量数据+++++$entry", Toast.LENGTH_LONG).show()

        // Output log for data aggregation
        // AppLog format: ## For aggregation ## timestamp(date), timestamp(time), systolic, diastolic, meanAP, current date time

        // Output log for data aggregation
        // AppLog format: ## For aggregation ## timestamp(date), timestamp(time), systolic, diastolic, meanAP, current date time
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var agg = "## For aggregation ## "
        agg += "$dateStr,$timeStr"
        agg += ",$systolicVal,$diastolicVal,$meanApVal"
        agg += "," + sdf.format(c.time)
        Log.d("======",agg)

        return BpDataResult(systolicVal,diastolicVal,pulseRateStr.toShort(),movement,irregular,timestampStr)
    }
}