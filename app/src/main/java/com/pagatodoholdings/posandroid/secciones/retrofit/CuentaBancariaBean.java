package com.pagatodoholdings.posandroid.secciones.retrofit;

public class CuentaBancariaBean {
    private int bancocod;
    private String descripcion;

    public CuentaBancariaBean(int bancocod, String descripcion) {
        this.bancocod = bancocod;
        this.descripcion = descripcion;
    }

    public int getBancocod() {
        return bancocod;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
