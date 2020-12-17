package com.pagatodoholdings.posandroid.secciones.retrofit;

public class AltaTarjetaCoFBean {
    private String cvv;
    private String fecha;
    private String numero;
    private String nombre;

    public AltaTarjetaCoFBean(String cvv, String fecha, String nombre, String numero) {
        this.cvv = cvv;
        this.fecha = fecha;
        this.nombre = nombre;
        this.numero = numero;
    }


    public String getCvv() {
        return cvv;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }
}
