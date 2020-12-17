package com.pagatodoholdings.posandroid.secciones.promociones;


import java.io.Serializable;


public class Promocion extends Documento implements Serializable {


    private String codigo="";
    private String tipoCodigo="";
    private String fechaFin;
    private String fechaInicio;


    @Override
    public String getTitulo() {
        return super.getTitulo();
    }

    @Override
    public void setTitulo(String titulo) {
        super.setTitulo(titulo);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipoCodigo() {
        return tipoCodigo;
    }

    public void setTipoCodigo(String tipoCodigo) {
        this.tipoCodigo = tipoCodigo;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Promocion) {
            Promocion promocion = (Promocion)obj;
            return super.getId().equals(promocion.getId());
        } else {
            return false;
        }
    }
}
