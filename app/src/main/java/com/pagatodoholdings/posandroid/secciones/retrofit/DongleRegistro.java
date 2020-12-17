package com.pagatodoholdings.posandroid.secciones.retrofit;

public class DongleRegistro {

    private String dongleserie;

    private String email;

    private String tpvcod;

    public DongleRegistro(String dongleserie, String email, String tpvcod) {
        this.dongleserie = dongleserie;
        this.email = email;
        this.tpvcod = tpvcod;
    }

    public String getDongleserie() {
        return dongleserie;
    }

    public void setDongleserie(String dongleserie) {
        this.dongleserie = dongleserie;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTpvcod() {
        return tpvcod;
    }

    public void setTpvcod(String tpvcod) {
        this.tpvcod = tpvcod;
    }

    @Override
    public String toString() {
        return "DongleRegistro{" +
                "dongleserie='" + dongleserie + '\'' +
                ", email='" + email + '\'' +
                ", tpvcod='" + tpvcod + '\'' +
                '}';
    }
}
