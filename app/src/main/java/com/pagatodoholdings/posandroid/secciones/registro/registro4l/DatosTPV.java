package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatosTPV {

    @SerializedName("tpvcod")
    @Expose
    private String tpvcod;

    @SerializedName("clicod")
    @Expose
    private Integer clicod;

    @SerializedName("tpvpassword")
    @Expose
    private String tpvpasswordip;

    @SerializedName("tpvpasswordpci")
    @Expose
    private String tpvpasswordpci;

    @SerializedName("qr")
    @Expose
    private String qr;

    public String getTpvcod() {
        return tpvcod;
    }

    public Integer getClicod() {
        return clicod;
    }

    public String getTpvpasswordip() {
        return tpvpasswordip;
    }

    public String getTpvpasswordpci() {
        return tpvpasswordpci;
    }

    public String getQr() { return qr; }

}
