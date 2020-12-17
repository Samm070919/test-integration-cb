package com.pagatodoholdings.posandroid.secciones.retrofit;

/**
 * @author fmendozag
 */

public class DatosCuentaBancaria {

    private String nombre;
    private String docid;
    private String ctabancaria;
    private boolean ctavalidada;
    private int bancocod;
    private String banco;
    private int tipocta;
    private String tipoctadescripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getCtabancaria() {
        return ctabancaria;
    }

    public void setCtabancaria(String ctabancaria) {
        this.ctabancaria = ctabancaria;
    }

    public boolean isCtavalidada() {
        return ctavalidada;
    }

    public void setCtavalidada(boolean ctavalidada) {
        this.ctavalidada = ctavalidada;
    }

    public int getBancocod() {
        return bancocod;
    }

    public void setBancocod(int bancocod) {
        this.bancocod = bancocod;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public int getTipocta() {
        return tipocta;
    }

    public void setTipocta(int tipocta) {
        this.tipocta = tipocta;
    }

    public String getTipoctadescripcion() {
        return tipoctadescripcion;
    }

    public void setTipoctadescripcion(String tipoctadescripcion) {
        this.tipoctadescripcion = tipoctadescripcion;
    }
}

