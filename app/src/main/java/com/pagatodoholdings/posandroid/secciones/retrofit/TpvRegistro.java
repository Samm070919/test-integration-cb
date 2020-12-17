package com.pagatodoholdings.posandroid.secciones.retrofit;

public class TpvRegistro {

    private String email;

    private String modelo;
    private String password;

    private String serie;

    public TpvRegistro(String email, String password, String serie) {
        this.email = email;
        this.password = password;
        this.serie = serie;
    }


    public TpvRegistro(String email, String modelo, String password, String serie) {
        this.email = email;
        this.modelo = modelo;
        this.password = password;
        this.serie = serie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return "TpvRegistro{" +
                "email='" + email + '\'' +
                ", modelo='" + modelo + '\'' +
                ", password='" + password + '\'' +
                ", serie='" + serie + '\'' +
                '}';
    }
}
