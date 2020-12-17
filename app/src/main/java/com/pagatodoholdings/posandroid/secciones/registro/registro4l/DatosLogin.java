package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatosLogin {

    private DatosTPV datosTpv;
    @SerializedName("pais")
    private Pais pais;
    @SerializedName("pci")
    private Pais pci;
    private Cliente cliente;

    @SerializedName("token")
    @Expose
    private String token;

    public DatosLogin(Pais pais, DatosTPV datosTpv, Pais pci, Cliente cliente, String token) {
        this.pais = pais;
        this.datosTpv = datosTpv;
        this.pci = pci;
        this.cliente = cliente;
        this.token = token;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public DatosTPV getDatosTpv() {
        return datosTpv;
    }

    public void setDatosTpv(DatosTPV datosTpv) {
        this.datosTpv = datosTpv;
    }

    public Pais getPci() {
        return pci;
    }

    public void setPci(Pais pci) {
        this.pci = pci;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Cliente getCliente() {
        return cliente;
    }
}
