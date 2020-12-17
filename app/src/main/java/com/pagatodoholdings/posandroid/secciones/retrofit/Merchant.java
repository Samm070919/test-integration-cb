package com.pagatodoholdings.posandroid.secciones.retrofit;

public class Merchant {

    Integer clicod;

    String email;

    public Merchant(Integer clicod, String email) {
        this.clicod = clicod;
        this.email = email;
    }

    public Integer getClicod() {
        return clicod;
    }

    public void setClicod(Integer clicod) {
        this.clicod = clicod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
