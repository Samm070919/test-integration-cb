package com.pagatodoholdings.posandroid.secciones.retrofit;

public class    TipoDocBean {

    private Integer tipdoc;
    private String descripcion;

    public Integer getTipodoc() {
        return tipdoc;
    }

    public void setTipodoc(final Integer tipodoc) {
        this.tipdoc = tipodoc;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
