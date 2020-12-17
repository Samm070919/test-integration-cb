package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pais {

    @SerializedName("codigo")
    @Expose
    private String codigo;

    @SerializedName("ip")
    @Expose
    private String ip;

    @SerializedName("puerto")
    @Expose
    private Integer puerto;

    @SerializedName("urlqr")
    @Expose
    private String urlqr;

    @SerializedName("urlwalletpublica")
    @Expose
    private String urlwalletpublica;

    @SerializedName("urlwsmpos")
    @Expose
    private String urlwsmpos;

    @SerializedName("urlcnp")
    @Expose
    private String urlcnp;


    public String getCodigo() {
        return codigo;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPuerto() {
        return puerto;
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

    public String getUrlcnp() {
        return urlcnp;
    }
}
