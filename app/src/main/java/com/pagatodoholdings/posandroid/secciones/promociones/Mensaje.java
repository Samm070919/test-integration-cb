package com.pagatodoholdings.posandroid.secciones.promociones;


import java.io.Serializable;


public class Mensaje extends Documento implements Serializable {
    private String enlace="";
    private String token="";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id="";

    @Override
    public String getTitulo() {
        return super.getTitulo();
    }

    @Override
    public void setTitulo(String titulo) {
        super.setTitulo(titulo);
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Mensaje) {
            Mensaje promocion = (Mensaje)obj;
            return this.id.equals(promocion.id);
        } else {
            return false;
        }
    }
}
