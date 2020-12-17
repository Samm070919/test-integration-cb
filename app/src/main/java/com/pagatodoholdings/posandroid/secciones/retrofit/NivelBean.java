package com.pagatodoholdings.posandroid.secciones.retrofit;

/**
 * @author dsoria
 */

public class NivelBean {

    private Integer nivelcod;

    private String descripcion;

    public NivelBean(Integer nivelcod, String descripcion) {
        this.nivelcod = nivelcod;
        this.descripcion = descripcion;
    }

    public Integer getNivelcod() {
        return nivelcod;
    }

    public void setNivelcod(final Integer nivelcod) {
        this.nivelcod = nivelcod;
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
