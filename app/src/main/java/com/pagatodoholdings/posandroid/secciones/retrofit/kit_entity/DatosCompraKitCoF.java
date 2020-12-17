package com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity;

/**
 * @author fmendozag
 */

public class DatosCompraKitCoF {

    private String cvv;
    private String importe;
    private String idpago;
    private int idtarjeta;
    private String ip;

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getIdPago() {
        return idpago;
    }

    public void setIdPago(String idPago) {
        this.idpago = idPago;
    }

    public int getIdtarjeta() {
        return idtarjeta;
    }

    public void setIdtarjeta(int idtarjeta) {
        this.idtarjeta = idtarjeta;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

