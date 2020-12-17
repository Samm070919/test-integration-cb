package com.pagatodoholdings.posandroid.secciones.retrofit;

import java.io.Serializable;
import java.math.BigDecimal;

public class SaldoBean implements Serializable {
    private Integer bolsa;
    private String descripcion;
    private BigDecimal saldo;


    public Integer getBolsa() {
        return bolsa;
    }

    public void setBolsa(Integer bolsa) {
        this.bolsa = bolsa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
