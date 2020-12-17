package com.pagatodoholdings.posandroid.secciones.formularios;

import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeFragment;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.formulario.Formulario;

public final class FormularioFragmentFactory {

    private static final String TAG = FormularioFragmentFactory.class.getSimpleName();

    private FormularioFragmentFactory() {
    }

    public static AbstractFragment build(final NivelMenu nivelMenu, final Menu menu, final Operaciones operacion, final Formulario formulario) {
        final Productos producto = SigmaBdManager.getProducto(operacion, new OnFailureListener.BasicOnFailureListener(TAG, "Error al Consultar la Base de Datos de los Productos"));
        if (producto.getPci() == 1 ) {
            return ChargeFragment.newInstance();
        }
        return FormularioGenerico.newInstance(operacion, nivelMenu,formulario);
    }

    public static AbstractFragment build(final Productos producto) {
        if (producto.getPci() == 1 ) {
            return ChargeFragment.newInstance();
        }
        return FormularioGenerico.newInstance(producto);
    }

}