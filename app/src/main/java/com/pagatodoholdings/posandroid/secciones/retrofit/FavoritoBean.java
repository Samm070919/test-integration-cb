package com.pagatodoholdings.posandroid.secciones.retrofit;

public class FavoritoBean {

    private String alias;
    private Integer diaInicio;
    private String procod;
    private String periodo;
    private String fecha;
    private String estado;

    public FavoritoBean() {
    }

    public FavoritoBean(String alias, Integer diaInicio, String procod, String periodo) {
        this.alias = alias;
        this.diaInicio = diaInicio;
        this.procod = procod;
        this.periodo = periodo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getDiaInicio() {
        return diaInicio;
    }

    public void setDiaInicio(Integer diaInicio) {
        this.diaInicio = diaInicio;
    }

    public String getProcod() {
        return procod;
    }

    public void setProcod(String procod) {
        this.procod = procod;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
