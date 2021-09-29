package com.example.zzmedbpblelib.fastble.callback;


import com.example.zzmedbpblelib.fastble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}