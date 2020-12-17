package com.pagatodoholdings.posandroid.secciones.ticket.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmailTicket {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("operacion")
    @Expose
    private String operacion;
    @SerializedName("pci")
    @Expose
    private boolean pci;
    @SerializedName("refLocal")
    @Expose
    private String refLocal;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public boolean getPci() {
        return pci;
    }

    public void setPci(boolean pci) {
        this.pci = pci;
    }

    public String getRefLocal() {
        return refLocal;
    }

    public void setRefLocal(String refLocal) {
        this.refLocal = refLocal;
    }
}
