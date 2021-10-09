package com.example.zzmedbpblelib.utils;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

public class BleUtils {

    public static boolean unBond(BluetoothDevice bletooth) {
        if (bletooth != null && bletooth.getBondState() == 12) {
            try {
                Method removeBond = BluetoothDevice.class.getMethod("removeBond", new Class[0]);
                return ((Boolean) removeBond.invoke(bletooth, new Object[0])).booleanValue();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
        return false;
    }
}
