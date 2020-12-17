package com.pagatodoholdings.posandroid.manager;

import android.annotation.SuppressLint;
import android.os.Build;
import androidx.annotation.NonNull;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;

import net.fullcarga.android.api.util.StringUtil;

import java.util.Locale;
import java.util.UUID;

public class AppBuildManager implements BuildManager {

    @Override
    @SuppressLint("HardwareIds")
    @SuppressWarnings("deprecation")
    public String getSerial() throws SecurityException { //NOPMD //NOSONAR hay que lanzar al excepcion runtime para que android gestione los permisos

        String serie;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                serie = MposApplication.getInstance().getAndroidId();
            }else{
                serie = Build.getSerial();
                if(serie==null || "unknown".equals(serie)){
                    serie = getString();
                }
            }
        } else {
            serie = Build.SERIAL; //NOPMD //NOSONAR es necesario usar esta forma @deprecated para compatibilidad con versiones anteriores
        }

        return StringUtil.fillLeft(serie, '0', 9);
    }

    @NonNull
    private String getString() {
        final UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","").substring(0,11).toUpperCase(Locale.getDefault());
    }

    @Override
    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    @Override
    public String getBrand() {
        return Build.BRAND;
    }

    @Override
    public String getProduct() {
        return Build.PRODUCT;
    }

    @Override
    public String getModel() {
        return Build.MODEL;
    }

    @Override
    public String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }
}
