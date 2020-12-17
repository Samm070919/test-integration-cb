package com.pagatodoholdings.posandroid.secciones.retrofit;

public class QrWalletPago {
    private Integer amount;
    private String concepto;
    private String qrdata;

    public QrWalletPago(Integer amount, String concepto, String qrdata) {
        this.amount = amount;
        this.concepto = concepto;
        this.qrdata = qrdata;
    }

    public long getAmount() { return amount; }
    public void setAmount(Integer value) { this.amount = value; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String value) { this.concepto = value; }

    public String getQrdata() { return qrdata; }
    public void setQrdata(String value) { this.qrdata = value; }
}
