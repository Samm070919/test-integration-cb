package com.pagatodoholdings.posandroid.utils.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.HashSet;
import java.util.Set;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class BluetoothManagerUtil {

    private static final String TAG = BluetoothManagerUtil.class.getSimpleName();
    private static BluetoothManagerUtil mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevicesListListener bluetoothDevicesListCallback;
    private BluetoothDiscoveryFinished mBluetoothDiscoveryFinishedCallback;
    private Set<BluetoothDevice> pairedDevices;
    private boolean isDeviceSearchBeingStopped = false;

    private static final int REQUEST_COARSE_LOCATION = 199307191;

    public interface BluetoothDevicesListListener {
        void deviceFound(BluetoothDevice device);

        /**
         * cuando se inicie una nueva búsqueda, se deberán borrar todos los dispositivos que ya fueron encontrados
         */
        void clearList();
    }

    public interface BluetoothDiscoveryFinished {
        void onDiscoveryFinished();
    }

    public interface OnPairedDeviceListener {
        void pairingProcessFinished(boolean isDevicePaired);
    }

    public interface BluetoothConnectedDevicesUpdateListener {
        void onConnectedDevicesUpdate(Set<BluetoothDevice> connectedDevices);
    }

    /**
     * para realizar cualquier accion de bluetooth, necesitas obtener el adaptador a través de este
     * método
     *
     * @return
     */
    public static BluetoothManagerUtil getBluetoothAdapter() {
        if (mAdapter == null) {
            mAdapter = new BluetoothManagerUtil();
        }
        return mAdapter;
    }

    /**
     * Tienes que llamar este método para permitir que el adaptador bluetooth haga operaciones
     *
     * @param context
     * @param callback
     */
    public void startBluetoothManagement(Context context, BluetoothDevicesListListener callback) {
        this.bluetoothDevicesListCallback = callback;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }

        registerBroadcastReceiver(context);
        startSearchBluetoothDevices();
    }

    private BluetoothManagerUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setBluetoothEnable(boolean enable) {
        if (enable) {
            mBluetoothAdapter.enable();
        } else {
            mBluetoothAdapter.disable();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void registerBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.registerReceiver(mReceiver, filter, Context.RECEIVER_VISIBLE_TO_INSTANT_APPS);
        }else{
            context.registerReceiver(mReceiver, filter);
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> set = new HashSet<>();
        if (mBluetoothAdapter != null) {
            set = mBluetoothAdapter.getBondedDevices();
        }
        return set;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                newDeviceFound(device);
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        startSearchBluetoothDevices();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        stopSearchingBluetoothDevices();
                        break;
                    default:
                        break;
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && mBluetoothDiscoveryFinishedCallback != null) {
                mBluetoothDiscoveryFinishedCallback.onDiscoveryFinished();
            }
        }

        private void newDeviceFound(BluetoothDevice device) {
            if (isDeviceSearchBeingStopped) {
                return;
            }
            if (pairedDevices.add(device)) {
                bluetoothDevicesListCallback.deviceFound(device);
            }
        }
    };

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Esto hace que el módulo de bluetooth busque dispositivos, como el nombre lo indica
     * Si quieres recibir un aviso de que ya terminó de buscar, usa el método público implementando
     * la interfaz
     */
    public void startSearchBluetoothDevices() {
        if (bluetoothDevicesListCallback != null) {
            bluetoothDevicesListCallback.clearList();
        }

        isDeviceSearchBeingStopped = false;
        pairedDevices = new HashSet<>();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * Usa este método si quieres saber cuando se termine de buscar dispositivos
     * @param callback
     */
    public void setBluetoothDiscoveryFinishedListener(BluetoothDiscoveryFinished callback){
        this.mBluetoothDiscoveryFinishedCallback = callback;
    }

    private void stopSearchingBluetoothDevices() {
        isDeviceSearchBeingStopped = true;

        mBluetoothAdapter.cancelDiscovery();
    }

    public void stopBluetoothManagement(Context context) {
        context.unregisterReceiver(mReceiver);
        stopSearchingBluetoothDevices();
    }

    public void pairDevice(final BluetoothDevice device, final OnPairedDeviceListener callback) {
        stopSearchingBluetoothDevices();
        HandleBluetoothParingAsync asyncBluetoothPairing = new HandleBluetoothParingAsync();
        asyncBluetoothPairing.setOnProcessFinishedListener(callback::pairingProcessFinished);

        asyncBluetoothPairing.pairDevice(device);
    }

    public void disconnectDevice(){
        try {
            mBluetoothAdapter.disable();
        }catch (Exception exe){
            LOGGER.throwing(TAG,1,exe,exe.getCause().toString());
        }
    }

    public void forgetPairedDevice(BluetoothDevice device) {
        HandleBluetoothParingAsync asyncBluetoothPairing = new HandleBluetoothParingAsync();
        asyncBluetoothPairing.setOnProcessFinishedListener(isSuccessful -> startSearchBluetoothDevices());
        asyncBluetoothPairing.unpairDevice(device);
    }

    public void restartDevicesSearch() {
        stopSearchingBluetoothDevices();
        startSearchBluetoothDevices();
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }
}
