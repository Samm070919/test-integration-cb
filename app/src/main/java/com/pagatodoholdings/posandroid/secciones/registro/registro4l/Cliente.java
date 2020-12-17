package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cliente {

     @SerializedName("serie")
     @Expose
    private String serie;

     @SerializedName("nombre")
     @Expose
    private String  nombre;

     @SerializedName("apellido1")
     @Expose
    private String apellido1;

     @SerializedName("apellido2")
     @Expose
    private String apellido2;

     @SerializedName("id")
     @Expose
    private String id;

     @SerializedName("fechaNacimiento")
     @Expose
    private String fechaNacimiento;

     @SerializedName("genero")
     @Expose
    private  String genero;

     @SerializedName("email")
     @Expose
    private  String email;

     @SerializedName("pais")
     @Expose
    private String pais;

     @SerializedName("telefono")
     @Expose
    private String telefono;

     @SerializedName("password")
     @Expose
    private String password;

     @SerializedName("longitud")
     @Expose
    private String longitud;

    @SerializedName("latitud")
    @Expose
    private String latitud;

    @SerializedName("tipo")
    @Expose
    private String tipo;

    public String getSerie() {
        return serie;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getId() {
        return id;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public String getEmail() {
        return email;
    }

    public String getPais() {
        return pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getPassword() {
        return password;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
