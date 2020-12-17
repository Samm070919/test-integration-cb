package com.pagatodoholdings.posandroid.secciones.retrofit;

import java.io.Serializable;

public class DatosTarjetaCoFBean implements Serializable {
    private int idtarjeta;
    private String nombre;
    private String numero4ultimos;
    private String fecha;
    private String marca;

    public int getIdtarjeta() {
        return idtarjeta;
    }

    public void setIdtarjeta(int idtarjeta) {
        this.idtarjeta = idtarjeta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero4ultimos() {
        return numero4ultimos;
    }

    public void setNumero4ultimos(String numero4ultimos) {
        this.numero4ultimos = numero4ultimos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
}
