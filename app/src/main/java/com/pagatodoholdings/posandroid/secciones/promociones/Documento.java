package com.pagatodoholdings.posandroid.secciones.promociones;


import java.io.Serializable;


public class Documento implements Serializable {

    private String titulo ="";
    private String imagen="";
    private String mensaje="";
    private String id="";
    private boolean isInbox=false;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isInbox() {
        return isInbox;
    }

    public void setInbox(boolean inbox) {
        isInbox = inbox;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Documento) {
            Documento promocion = (Documento)obj;
            return this.id.equals(promocion.id);
        } else {
            return false;
        }
    }
}
