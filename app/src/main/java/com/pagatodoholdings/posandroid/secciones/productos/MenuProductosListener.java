package com.pagatodoholdings.posandroid.secciones.productos;

import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodoholdings.posandroid.general.interfaces.RetrocesoListener;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

public interface MenuProductosListener extends RetrocesoListener {
    void onProductoSeleccionado(NivelMenu nivelMenu, Menu menu);

    void onSubMenuSeleccionado(NivelMenu nivelMenu, Menu menu, String menuTitle);

    void onProductoError(NivelMenu nivelMenu, Menu menu);
}
