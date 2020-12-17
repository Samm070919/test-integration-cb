package com.pagatodoholdings.posandroid.utils;

import java.util.List;

public class UpdateVentasObjectResponse {

    private final int code;
    private final String msg;
    private final List<Integer> procesadas;

    public UpdateVentasObjectResponse(final int code, final String message,  final List<Integer> procesadas){

        this.code = code;
        this.msg = message;
        this.procesadas = procesadas;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public List<Integer> getListTransacciones(){

        return procesadas;
    }

    public String getNoZync(){

        return String.valueOf(procesadas.size());
    }

    @Override
    public String toString() {
        return "UpdateVentasObjectResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", procesadas=" + procesadas +
                '}';
    }
}
