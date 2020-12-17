package com.pagatodoholdings.posandroid.secciones.acquiring;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.pagatodoholdings.posandroid.MposApplication;

public class SettingsPref {

    private SharedPreferences pref;
    private SharedPreferences.Editor prefsEditor;
    public static final String IMPUESTOS_H = "IMPUESTOS_H";
    public static final String IMPUESTOS_M = "IMPUESTOS_M";
    public static final String IMPUESTOS_L = "IMPUESTOS_L";
    public static final String PROPINA_H = "PROPINA_H";
    public static final String PROPINA_M = "PROPINA_M";
    public static final String PROPINA_L = "PROPINA_L";
    public static final String IMPUESTOS_SELECTED = "IMPUESTOS_SELECTED";
    public static final String PROPINA_SELECTED = "PROPINA_SELECTED";
    public static final String ES_IMPUESTO = "ES_IMPUESTO";
    public static final String ES_PROPINA = "ES_PROstatic PINA";
    public static final String ESTA_AUTORIZADO = "ESTA_AUTORIZADO";

    @SuppressLint("CommitPrefEdits")
    private SettingsPref() {
        pref = MposApplication.getInstance().getLocalPref();
        prefsEditor = pref.edit();

    }

    public static SettingsPref getInstance(){
        return new SettingsPref();
    }

    public void saveData(String key, String value){
          prefsEditor.putString(key,value);
          prefsEditor.commit();
    }

    public void saveData(String key, boolean value){
        prefsEditor.putBoolean(key,value);
        prefsEditor.commit();
    }

    public boolean getDataBoolean(String key, boolean value){
        return pref.getBoolean(key,value);
    }

    public String getData(String key){
        return pref.getString(key,"");
    }

    public String getDataPer(String key){
        return pref.getString(key,"0%");
    }





}
