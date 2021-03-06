package com.example.zzmedbpblelib.fastble.callback;


import com.example.zzmedbpblelib.fastble.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
