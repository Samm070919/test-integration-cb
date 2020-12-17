package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

public class DatosNegocio {

    private static DatosNegocio instance = null;

    private Integer clicod;

    private String direccion;

    private String email;

    private String id;

    private Integer nivel2;

    private String rs;

    private Integer tiponegocio;

    private  Integer  tipdoc =  1;

    public static DatosNegocio getInstance() {

        if (instance == null) {
            instance = new DatosNegocio();
        }
        return instance;
    }

    public Integer getTipdoc() {
        return tipdoc;
    }

    public void setTipdoc(Integer tipdoc) {
        this.tipdoc = tipdoc;
    }

    public DatosNegocio(
            Integer clicod,
            String direccion,
            String email,
            String id,
            Integer nivel2,
            String rs,
            Integer tiponegocio) {
        this.clicod = clicod;
        this.direccion = direccion;
        this.email = email;
        this.id = id;
        this.nivel2 = nivel2;
        this.rs = rs;
        this.tiponegocio = tiponegocio;
    }

    private DatosNegocio() {
    }

    public Integer getClicod() {
        return clicod;
    }

    public void setClicod(Integer clicod) {
        this.clicod = clicod;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getNivel2() {
        return nivel2;
    }

    public void setNivel2(Integer nivel2) {
        this.nivel2 = nivel2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public Integer getTiponegocio() {
        return tiponegocio;
    }

    public void setTiponegocio(Integer tiponegocio) {
        this.tiponegocio = tiponegocio;
    }
}
