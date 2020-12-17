package com.pagatodoholdings.posandroid.secciones.retrofit;

import java.math.BigDecimal;

public class DatosDesfogue {

    private String fecha;
    private BigDecimal importe;
    private Integer clicod;
    private long idreembolso;

    // Getter Methods

    public String getFecha() {
        return fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public float getClicod() {
        return clicod;
    }

    public long getIdreembolso() {
        return idreembolso;
    }

    // Setter Methods

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public void setClicod(int clicod) {
        this.clicod = clicod;
    }
}
