package com.pagatodoholdings.posandroid.secciones.sales.binding;

public class TitleFrag {

    private String title;
    private String subtitle;

    public TitleFrag(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public static TitleFrag getInstance(String titleFrag, String subtitle){
        return new TitleFrag(titleFrag,subtitle);
    }

    public String getTitleFrag() {
        return title;
    }

    public void setTitleFrag(String titleFrag) {
        this.title = titleFrag;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
