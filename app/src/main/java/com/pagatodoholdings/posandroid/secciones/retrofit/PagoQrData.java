package com.pagatodoholdings.posandroid.secciones.retrofit;

public class PagoQrData {
    private String alias;
    private String merchant;
    private String plate;
    private String referenceNumber;
    private String transactionIdentifier;
    private String nombre;

    public String getAlias() { return alias; }

    public String getMerchant() { return merchant; }

    public String getPlate() { return plate; }

    public String getReferenceNumber() { return referenceNumber; }

    public String getTransactionIdentifier() { return transactionIdentifier; }

    public String getNombre() {
        return nombre;
    }
}
