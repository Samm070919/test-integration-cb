package com.pagatodoholdings.posandroid.secciones.retrofit;

import java.io.Serializable;

public class AltaCuentaBean implements Serializable {
    private Integer bancocod;
    private String ctabancaria;
    private Integer tipocta;

    public AltaCuentaBean(Integer bancocod, String ctabancaria, Integer tipocta) {
        this.bancocod = bancocod;
        this.ctabancaria = ctabancaria;
        this.tipocta = tipocta;
    }

    public Integer getBancocod() {
        return bancocod;
    }

    public String getCtabancaria() {
        return ctabancaria;
    }

    public Integer getTipocta() {
        return tipocta;
    }
}
