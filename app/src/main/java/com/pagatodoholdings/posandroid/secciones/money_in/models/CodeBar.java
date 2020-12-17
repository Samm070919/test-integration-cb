package com.pagatodoholdings.posandroid.secciones.money_in.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CodeBar {
    @SerializedName("codBarras")
    @Expose
    private String codBarras;

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }
}
