package com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref;

import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ES_IMPUESTO;

public class IvaSwitchSettings extends SwitchSettingsBtn {

    public static IvaSwitchSettings getInstance(Context context, View.OnClickListener listener,
                                                CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        SettingsPref pref = SettingsPref.getInstance();
        return new IvaSwitchSettings(new SwitchnData(context.getResources().getString(R.string.impuestos),pref.getDataBoolean(ES_IMPUESTO,false),
                "0%","5%","19%"),listener,onCheckedChangeListener);

    }

    private IvaSwitchSettings(SwitchnData switchBtnData, View.OnClickListener listener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        super(switchBtnData, listener, onCheckedChangeListener);
    }


}
