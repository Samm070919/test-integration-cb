package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.pagatodoholdings.posandroid.secciones.retrofit.DatosPersonales;

public class DatosUsuarioRegistroCliente extends DatosPersonales {

    private static DatosUsuarioRegistroCliente instace = null;

    /**
     * Variables
     **/

    private String password;
    private Integer tipdoc ;
    private String modelo= "MODELO_LOGICO_WALLET";

    public static DatosUsuarioRegistroCliente getInstace() {
        if (instace == null) {
            instace = new DatosUsuarioRegistroCliente();
            return instace;
        }
        return instace;
    }

    private DatosUsuarioRegistroCliente() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTipdoc() {
        return tipdoc;
    }

    public void setTipdoc(Integer tipdoc) {
        this.tipdoc = tipdoc;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
