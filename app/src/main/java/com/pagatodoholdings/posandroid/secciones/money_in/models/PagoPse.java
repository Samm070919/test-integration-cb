
package com.pagatodoholdings.posandroid.secciones.money_in.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PagoPse {

    @SerializedName("idpagopse")
    @Expose
    private Integer idpagopse;
    @SerializedName("clicod")
    @Expose
    private Integer clicod;
    @SerializedName("tpvcod")
    @Expose
    private String tpvcod;
    @SerializedName("importe")
    @Expose
    private Integer importe;
    @SerializedName("referencia1")
    @Expose
    private String referencia1;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("fechainsert")
    @Expose
    private String fechainsert;
    @SerializedName("fecharespuesta")
    @Expose
    private String fecharespuesta;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("estadopsedesc")
    @Expose
    private String estadopsedesc;

    public Integer getIdpagopse() {
        return idpagopse;
    }

    public void setIdpagopse(Integer idpagopse) {
        this.idpagopse = idpagopse;
    }

    public Integer getClicod() {
        return clicod;
    }

    public void setClicod(Integer clicod) {
        this.clicod = clicod;
    }

    public String getTpvcod() {
        return tpvcod;
    }

    public void setTpvcod(String tpvcod) {
        this.tpvcod = tpvcod;
    }

    public Integer getImporte() {
        return importe;
    }

    public void setImporte(Integer importe) {
        this.importe = importe;
    }

    public String getReferencia1() {
        return referencia1;
    }

    public void setReferencia1(String referencia1) {
        this.referencia1 = referencia1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFechainsert() {
        return fechainsert;
    }

    public void setFechainsert(String fechainsert) {
        this.fechainsert = fechainsert;
    }

    public String getFecharespuesta() {
        return fecharespuesta;
    }

    public void setFecharespuesta(String fecharespuesta) {
        this.fecharespuesta = fecharespuesta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstadopsedesc() {
        return estadopsedesc;
    }

    public void setEstadopsedesc(String estadopsedesc) {
        this.estadopsedesc = estadopsedesc;
    }
}
