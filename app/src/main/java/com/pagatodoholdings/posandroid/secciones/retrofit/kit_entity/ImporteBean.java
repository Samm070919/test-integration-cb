package com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fmendozag
 */

public class ImporteBean {

    private int nivel2 = 0;
    private int nivel3 = 0;

    private List<Productos> productos = new ArrayList<>();

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

    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }
}

