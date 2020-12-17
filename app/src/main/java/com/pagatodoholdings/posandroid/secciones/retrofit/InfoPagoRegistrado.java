package com.pagatodoholdings.posandroid.secciones.retrofit;

public class InfoPagoRegistrado {

    public InfoPagoRegistrado(){
        //Empty
    }

    private String estadopsedesc;
    private String fechainsert ;
    private String fecharespuesta;
    private int idpagopse ;
    private String importe;
    private int tipo;
    private String transaccionid;
    private String url;
    int idpago;
    String estadodesc;
    String idpedido;

    public int getIdPago() {
        return idpago;
    }

    public void setIdPago(int idPago) {
        this.idpago = idPago;
    }

    public String getEstadodesc() {
        return estadodesc;
    }

    public void setEstadodesc(String estadodesc) {
        this.estadodesc = estadodesc;
    }

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    private class Tipo {
        private int codigo;
        private String descripcion;

        public int getCodigo() {
            return codigo;
        }

        public void setCodigo(int codigo) {
            this.codigo = codigo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }


    public String getEstadopsedesc() {
        return estadopsedesc;
    }

    public void setEstadopsedesc(String estadopsedesc) {
        this.estadopsedesc = estadopsedesc;
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

    public int getIdpagopse() {
        return idpagopse;
    }

    public void setIdpagopse(int idpagopse) {
        this.idpagopse = idpagopse;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getTransaccionid() {
        return transaccionid;
    }

    public void setTransaccionid(String transaccionid) {
        this.transaccionid = transaccionid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
