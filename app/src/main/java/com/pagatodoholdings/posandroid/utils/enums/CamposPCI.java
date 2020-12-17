package com.pagatodoholdings.posandroid.utils.enums;

public enum CamposPCI {
    IMPORTE("Importe"),
    PROPINA("Propina"),
    IMPUESTO("Impuesto"),
    CASHBACK("Importe Retiro"),
    CVV("CVV"),
    BITMAP("BITMAP"),
    CP("Codigo Postal"),
    PAN("Últimos 4 Digitos de la Tarjeta"),
    CUOTAS_MES("Cuotas");


    private final String campo;

    CamposPCI(final String campoTipo) {
        this.campo = campoTipo;
    }

    public String campo() {
        return campo;
    }
}
