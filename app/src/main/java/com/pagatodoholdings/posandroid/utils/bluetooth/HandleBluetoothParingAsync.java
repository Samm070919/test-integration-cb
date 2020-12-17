package com.pagatodoholdings.posandroid.utils.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.lang.reflect.Method;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class HandleBluetoothParingAsync extends AsyncTask<BluetoothDevice, Void, Boolean> {

    private static final String TAG = HandleBluetoothParingAsync.class.getSimpleName();

    private OnProcessFinishedListener taskFinishedCallback;
    private BluetoothSocket socket;
    private boolean isPairing = false;

    public interface OnProcessFinishedListener {
        void processResult(boolean isSuccessful);
    }

    HandleBluetoothParingAsync() {
    }

    void setOnProcessFinishedListener(OnProcessFinishedListener callback){
        taskFinishedCallback = callback;
    }

    void pairDevice(BluetoothDevice device){
        isPairing = true;
        execute(device);
    }

    void unpairDevice(BluetoothDevice device){
        isPairing = false;
        execute(device);
    }

    @Override
    protected Boolean doInBackground(BluetoothDevice... bluetoothDevices) {
            try {
                BluetoothDevice device = bluetoothDevices[0];

                if (isPairing) {
                    final Method bluetooth = device.getClass().getMethod("createRfcommSocket", int.class);
                    socket = (BluetoothSocket) bluetooth.invoke(device, 1);
                    socket.connect();
                } else {
                    Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                }

            } catch (Exception exe) {
                LOGGER.throwing(TAG,1,exe,exe.getMessage());

            }

        if (socket != null){
            return socket.isConnected();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (taskFinishedCallback != null) {
            taskFinishedCallback.processResult(aBoolean);
        }
    }
}
