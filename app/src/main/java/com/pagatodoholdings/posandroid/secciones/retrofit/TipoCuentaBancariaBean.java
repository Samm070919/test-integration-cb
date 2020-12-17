package com.pagatodoholdings.posandroid.secciones.retrofit;

public class TipoCuentaBancariaBean {
    private int tipocta;
    private String descripcion;

    public TipoCuentaBancariaBean(int tipocta, String descripcion) {
        this.tipocta = tipocta;
        this.descripcion = descripcion;
    }

    public int getTipocta() {
        return tipocta;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
