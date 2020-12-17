package com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity;

import java.io.Serializable;

/**
 * @author fmendozag
 */

public class InfoCompraKitCoF implements Serializable {

    private String authorizationcode;
    private String created;
    private String description;
    private String numero4ultimos;
    private Double amount = 0.0;
    private Double importesaldo = 0.0;
    private Double comision = 0.0;
    private int status;
    private String transactionid;



    public String getAuthorizationcode() {
        return authorizationcode;
    }

    public void setAuthorizationcode(String authorizationcode) {
        this.authorizationcode = authorizationcode;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescripcion() {
        return description;
    }

    public void setDescripcion(String descripcion) {
        this.description = descripcion;
    }

    public String getNumero4ultimos() {
        return numero4ultimos;
    }

    public void setNumero4ultimos(String numero4ultimos) {
        this.numero4ultimos = numero4ultimos;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getImportesaldo() {
        return importesaldo;
    }

    public void setImportesaldo(Double importesaldo) {
        this.importesaldo = importesaldo;
    }

    public Double getComision() {
        return comision;
    }

    public void setComision(Double comision) {
        this.comision = comision;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }
}

