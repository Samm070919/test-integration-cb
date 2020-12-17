package com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fmendozag
 */

public class DatosCompraKit {


    private String importe = "";
    private String gastosEnvio = "";
    private String direccion;
    private String cp;
    private int nivel0;
    private int nivel1;
    private int nivel2;
    private int nivel3;
    private String formapago;
    private List<Productos> productos = new ArrayList<>();

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getDireccionCompleta() {
        return direccionCompleta;
    }

    public void setDireccionCompleta(String direccionCompleta) {
        this.direccionCompleta = direccionCompleta;
    }

    public void addNivel0ToDireccion(String element){
        this.direccionCompleta = element;
    }

    public void addNivel1ToDireccion(String element){
        this.direccionCompleta = this.direccionCompleta + ", " + element;
    }

    public void addNivel2ToDireccion(String element){
        this.direccionCompleta = this.direccionCompleta + ", " + element;
    }

    public void addNivel3ToDireccion(String element){
        this.direccionCompleta = this.direccionCompleta + ", " + element;
    }

    private String direccionCompleta;

    public String getFormapago() {
        return formapago;
    }

    public void setFormapago(String formapago) {
        this.formapago = formapago;
    }


    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getGastosEnvio() {
        return gastosEnvio;
    }

    public void setGastosEnvio(String gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodpostal() {
        return cp;
    }

    public void setCodpostal(String codpostal) {
        this.cp = codpostal;
    }

    public int getNivel0() {
        return nivel0;
    }

    public void setNivel0(int nivel0) {
        this.nivel0 = nivel0;
    }

    public int getNivel1() {
        return nivel1;
    }

    public void setNivel1(int nivel1) {
        this.nivel1 = nivel1;
    }

    public int getNivel2() {
        return nivel2;
    }

    public void setNivel2(int nivel2) {
        this.nivel2 = nivel2;
    }

    public int getNivel3() {
        return nivel3;
    }

    public void setNivel3(int nivel3) {
        this.nivel3 = nivel3;
    }

}

