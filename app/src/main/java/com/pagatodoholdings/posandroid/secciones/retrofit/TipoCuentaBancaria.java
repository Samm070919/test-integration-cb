package com.pagatodoholdings.posandroid.secciones.retrofit;

public class TipoCuentaBancaria {
    private int tipoCta;
    private String descripcion;

    public TipoCuentaBancaria(int tipoCta, String descripcion) {
        this.tipoCta = tipoCta;
        this.descripcion = descripcion;
    }

    public int getTipoCta() {
        return tipoCta;
    }

    public void setTipoCta(int tipoCta) {
        this.tipoCta = tipoCta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
