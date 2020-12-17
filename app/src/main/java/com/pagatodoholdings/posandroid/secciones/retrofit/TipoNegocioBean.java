package com.pagatodoholdings.posandroid.secciones.retrofit;

public class TipoNegocioBean {
    private int tipnegocio;
    private String descripcion;

    public int getTipnegocio() {
        return tipnegocio;
    }

    public void setTipnegocio(int tipnegocio) {
        this.tipnegocio = tipnegocio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
