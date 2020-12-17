package com.pagatodoholdings.posandroid.utils.enums;

import java.util.Map;

public class Evento {

    private final IdEvento idEvento;
    private final Map<IdEvento, Object> datosEvento;

    public Evento(final IdEvento idEvento, final Map<IdEvento, Object> datosEvento) {
        this.idEvento = idEvento;
        this.datosEvento = datosEvento;
    }

    public IdEvento getIdEvento() {
        return idEvento;
    }

    public Map<IdEvento, Object> getDatosEvento() {
        return datosEvento;
    }
}
