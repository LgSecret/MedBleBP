package com.example.zzmedbpblelib.fastble.callback;


import com.example.zzmedbpblelib.fastble.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
