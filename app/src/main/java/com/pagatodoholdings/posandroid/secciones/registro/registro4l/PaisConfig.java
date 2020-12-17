package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaisConfig {
    @SerializedName("codigo")
    @Expose
    private String codigo;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("puerto")
    @Expose
    private int puerto;
    @SerializedName("urlwsmpos")
    @Expose
    private String urlwsmpos;
    @SerializedName("urlqr")
    @Expose
    private String urlqr;
    @SerializedName("urlwalletpublica")
    @Expose
    private String urlwalletpublica;
    @SerializedName("urlcnp")
    @Expose
    private String urlcnp;

    public String getCodigo() {
        return codigo;
    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getUrlcnp() {
        return urlcnp;
    }

    public String getUrlqr() {
        return urlqr;
    }

    public String getUrlwalletpublica() {
        return urlwalletpublica;
    }

    public String getUrlwsmpos() {
        return urlwsmpos;
    }
}