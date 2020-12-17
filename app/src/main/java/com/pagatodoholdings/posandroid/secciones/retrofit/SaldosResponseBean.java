package com.pagatodoholdings.posandroid.secciones.retrofit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SaldosResponseBean implements Serializable {
    public List<SaldoBean> getResponse() {
        return response;
    }

    public void setResponse(List<SaldoBean> response) {
        this.response = response;
    }

    private List<SaldoBean> response;
}
