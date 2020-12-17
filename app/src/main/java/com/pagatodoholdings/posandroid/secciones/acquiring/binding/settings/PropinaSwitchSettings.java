package com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref;

import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ES_PROPINA;

public class PropinaSwitchSettings extends SwitchSettingsBtn {

    public static PropinaSwitchSettings getInstance(Context context, View.OnClickListener listener,
                                                    CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        SettingsPref pref = SettingsPref.getInstance();
        return new PropinaSwitchSettings(new SwitchnData(context.getResources().getString(R.string.propinas),pref.getDataBoolean(ES_PROPINA,false),
                "8%","10%","15%"),listener,onCheckedChangeListener);

    }

    private PropinaSwitchSettings(SwitchnData switchBtnData, View.OnClickListener listener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        super(switchBtnData, listener, onCheckedChangeListener);
    }

}
