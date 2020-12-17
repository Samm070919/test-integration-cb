package com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author fmendozag
 */

public class ImporteKit {

    private String importe = "0";
    private int gastosEnvio = 0;

    public String getImporte() {
        return (new BigDecimal(importe)).setScale(2, RoundingMode.CEILING).toString();
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getGastosEnvio() {
        return (new BigDecimal(gastosEnvio)).setScale(2, RoundingMode.CEILING).toString();
    }

    public void setGastosEnvio(int gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }
}

