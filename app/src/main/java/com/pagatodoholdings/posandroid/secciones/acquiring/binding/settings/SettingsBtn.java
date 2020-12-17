package com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings;

import android.view.View;
import android.widget.CompoundButton;

public abstract class SettingsBtn {

    protected SwitchnData switchBtnData;
    protected ArrowData arrowData;
    protected View.OnClickListener listener;
    protected CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public SettingsBtn(SwitchnData switchBtnData, View.OnClickListener listener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.switchBtnData = switchBtnData;
        this.listener = listener;
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public SettingsBtn(ArrowData arrowData) {
        this.arrowData = arrowData;
    }

    public SwitchnData getSwitchBtnData() {
        return switchBtnData;
    }

    public void setSwitchBtnData(SwitchnData switchBtnData) {
        this.switchBtnData = switchBtnData;
    }

    public ArrowData getArrowData() {
        return arrowData;
    }



    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public static final class SwitchnData{
        private String titleSwitch;
        private boolean checked;
        private String perHigh;
        private String perMiddle;
        private String perLow;

        SwitchnData(String titleSwitch, boolean checked, String perHigh, String perMiddle, String perLow) {
            this.titleSwitch = titleSwitch;
            this.checked = checked;
            this.perHigh = perHigh;
            this.perMiddle = perMiddle;
            this.perLow = perLow;
        }

        public String getTitleSwitch() {
            return titleSwitch;
        }

        public boolean getChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getPerHigh() {
            return perHigh;
        }

        public String getPerMiddle() {
            return perMiddle;
        }

        public String getPerLow() {
            return perLow;
        }

    }

    public static final class ArrowData {
        private String titleArrow;

        public ArrowData(String titleArrow) {
            this.titleArrow = titleArrow;
        }

        public String getTitleArrow() {
            return titleArrow;
        }
    }
}
