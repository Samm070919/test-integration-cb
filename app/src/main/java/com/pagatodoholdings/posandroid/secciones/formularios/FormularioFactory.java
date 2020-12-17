package com.pagatodoholdings.posandroid.secciones.formularios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FormularioLayout;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FormularioLayoutNew;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FormularioPCILayout;

import net.fullcarga.android.api.formulario.Formulario;

public final class FormularioFactory {

    private FormularioFactory() {
    }

    public static View build(final Context context, final Formulario formulario) {
        final FormularioLayout formularioLayout = (FormularioLayout) LayoutInflater.from(context).inflate(R.layout.layout_formulario, null);
        formularioLayout.initUI();
        formularioLayout.initData(formulario);
        return formularioLayout;
    }

    public static View build(final Context context, final Formulario formulario,String tipo){
        final FormularioLayoutNew formularioLayout = (FormularioLayoutNew) LayoutInflater.from(context).inflate(R.layout.layout_formulario, null);
        formularioLayout.initUI();
        formularioLayout.initData(formulario,tipo);
        return formularioLayout;
    }

    public static View build(final Context context, final int Perfil) {
        final FormularioPCILayout formularioPCILayout = (FormularioPCILayout) LayoutInflater.from(context).inflate(R.layout.layout_formulario_pci, null);
        formularioPCILayout.initUI();
        formularioPCILayout.initData(Perfil);
        return formularioPCILayout;
    }
}
