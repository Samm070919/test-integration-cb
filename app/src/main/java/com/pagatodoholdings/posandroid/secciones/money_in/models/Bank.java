package com.pagatodoholdings.posandroid.secciones.money_in.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bank implements Serializable {
    @SerializedName("bancocod")
    @Expose
    private int bancocod;
    @SerializedName("convenio")
    @Expose
    private String convenio;
    @SerializedName("cuenta")
    @Expose
    private String cuenta;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("icono")
    @Expose
    private String icono;
    @SerializedName("codigo")
    @Expose
    private String codigo;

    public Bank() {
        //NOT IMPLEMENTED
    }

    public int getBancocod() {
        return bancocod;
    }

    public void setBancocod(int bancocod) {
        this.bancocod = bancocod;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
