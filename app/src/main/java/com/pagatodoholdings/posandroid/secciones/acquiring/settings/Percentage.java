package com.pagatodoholdings.posandroid.secciones.acquiring.settings;

public class Percentage {

    private String mPercentage;
    private String label;

    public Percentage(String mPercentage, String label) {
        this.mPercentage = mPercentage;
        this.label = label;
    }

    public String getmPercentage() {
        return mPercentage;
    }

    public void setmPercentage(String mPercentage) {
        this.mPercentage = mPercentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
