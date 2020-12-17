package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DatosPagoQr {

    @SerializedName("data")
    private PagoQrData data;
    private ArrayList<ErrorList> errorList;
    private String message;
    private boolean success;

    public PagoQrData getData() { return data; }

    public String getMessage() { return message; }

    public boolean getSuccess() { return success; }

    public List<ErrorList> getErrorList() { return errorList; }
}
