package com.pagatodoholdings.posandroid.secciones.productos;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

public interface ProductoSeleccionado {
    void notificaProductoSeleccionado(Menu menu);

    void notificaErrorProducto();
}
