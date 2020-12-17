package com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author fmendozag
 */

public class DatosNegocio {

    private String rs;
    private Long tipnegocio = 0L;
    private String descripcion;
    private Long tipdoc = 0L;
    private String docid;
    private String direccion;
    private String dongleserie;
    private String codpostal;
    @SerializedName("nivel0")
    private DatosNivel nivel0;
    @SerializedName("nivel1")
    private DatosNivel nivel1;
    @SerializedName("nivel2")
    private DatosNivel nivel2;
    @SerializedName("nivel3")
    private DatosNivel nivel3;
    @SerializedName("nivel4")
    private DatosNivel nivel4;

    public String getRs() {
        return rs;
    }

    public void setRs(String rs)     {
        this.rs = rs;
    }

    public Long getTipnegocio() {
        return tipnegocio;
    }

    public void setTipnegocio(Long tipnegocio) {
        this.tipnegocio = tipnegocio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getTipdoc() {
        return tipdoc;
    }

    public void setTipdoc(Long tipdoc) {
        this.tipdoc = tipdoc;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDongleserie() {
        return dongleserie;
    }

    public void setDongleserie(String dongleserie) {
        this.dongleserie = dongleserie;
    }

    public String getCodpostal() {
        return codpostal;
    }

    public void setCodpostal(String codpostal) {
        this.codpostal = codpostal;
    }

    public DatosNivel getNivel0() {
        return nivel0;
    }

    public void setNivel0(DatosNivel nivel0) {
        this.nivel0 = nivel0;
    }

    public DatosNivel getNivel1() {
        return nivel1;
    }

    public void setNivel1(DatosNivel nivel1) {
        this.nivel1 = nivel1;
    }

    public DatosNivel getNivel2() {
        return nivel2;
    }

    public void setNivel2(DatosNivel nivel2) {
        this.nivel2 = nivel2;
    }

    public DatosNivel getNivel3() {
        return nivel3;
    }

    public void setNivel3(DatosNivel nivel3) {
        this.nivel3 = nivel3;
    }

    public DatosNivel getNivel4() {
        return nivel4;
    }

    public void setNivel4(DatosNivel nivel4) {
        this.nivel4 = nivel4;
    }
}

