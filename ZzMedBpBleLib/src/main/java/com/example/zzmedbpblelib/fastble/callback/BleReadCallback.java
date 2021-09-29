package com.example.zzmedbpblelib.fastble.callback;


import com.example.zzmedbpblelib.fastble.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
