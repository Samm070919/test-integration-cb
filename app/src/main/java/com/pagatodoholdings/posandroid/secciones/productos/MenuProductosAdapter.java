package com.pagatodoholdings.posandroid.secciones.productos;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractHolder;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

import org.json.JSONArray;

import java.util.List;

public class MenuProductosAdapter extends RecyclerView.Adapter<MenuProductosAdapter.MenuLevelHolder> {

    private static final String TAG = MenuProductosAdapter.class.getName();

    OnFailureListener onFailureListener;

    private final List<Menu> itemsMenu;
    private final ProductoSeleccionado productoSeleccionado;
    private final NivelMenu nivelMenu;
    private long tiempoUltimoClick;
    private JSONArray arrayProduct;
    private int mLayoutId;
    private int flag = 0;
    private Activity homeActivity;

    MenuProductosAdapter(
            final NivelMenu nivel,
            final List<Menu> itemsMenu,
            final ProductoSeleccionado viewCallback,
            final Activity homeActivity
    ) {
        this(nivel, itemsMenu, viewCallback, R.layout.item_rv_menu_productos_adapter, homeActivity);
    }

    MenuProductosAdapter(
            final NivelMenu nivel,
            final List<Menu> itemsMenu,
            final ProductoSeleccionado viewCallback,
            int layoutId,
            Activity home
    ) {
        this.itemsMenu = itemsMenu;
        this.productoSeleccionado = viewCallback;
        this.nivelMenu = nivel;
        this.mLayoutId = layoutId;
        onFailureListener = exc -> {
            Logger.LOGGER.throwing(TAG, 1, exc, "Error al acceder a la base de datos");
            productoSeleccionado.notificaErrorProducto();
        };
        this.homeActivity = home;
    }

    @Override
    public MenuLevelHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(mLayoutId, parent, false);

        return new MenuLevelHolder(view);
    }

    @Override
    public void onBindViewHolder(final MenuLevelHolder holder, final int position) {

        final Menu selectedMenu = itemsMenu.get(position);
        holder.onBind(selectedMenu);
        holder.getMainView().setTag(selectedMenu);
        holder.getMainView().setOnClickListener(view -> {
            if (position == 7 &&
                    ((MposApplication) homeActivity.getApplication())
                            .getMenuFlag()
                            .equals("C1")
            ) {
                ((MposApplication) homeActivity.getApplication()).setMenuFlag("C2");
                productoSeleccionado.notificaProductoSeleccionado(
                        new Menu(
                                "02050000",
                                "Pago de Servicios",
                                null,
                                203
                        )
                );
                Log.d("MENU", Integer.toString(position));
            } else {
                if (SystemClock.elapsedRealtime() - tiempoUltimoClick <
                        Constantes.TIEMPO_ENTRE_CLICKS) {
                    return;
                }
                tiempoUltimoClick = SystemClock.elapsedRealtime();
                productoSeleccionado.notificaProductoSeleccionado(selectedMenu);
            }
        });

        final String iconPath = getMenuIconPath(selectedMenu, nivelMenu);

        if (!"".equals(iconPath)) {
            holder.setIcon(iconPath);
        } else {
            holder.setIcon(Utilities.getAttributeReference(R.attr.fullcarga));
        }
    }

    @Override
    public int getItemCount() {
        return arrayProduct != null ? arrayProduct.length() : itemsMenu.size();
    }

    public String getMenuIconPath(final Menu currentMenu, final NivelMenu menuLevel) {
        if (currentMenu.getIcono() != null) {
            return SigmaBdManager.obtenIcono(currentMenu, onFailureListener);
        } else {
            final List<Menu> uMenuItems = getItemsForIconPath(currentMenu, menuLevel);
            for (final Menu pivot : uMenuItems) {
                if (pivot.getIcono() != null && pivot.getIcono() != 0) {
                    return SigmaBdManager.obtenIcono(pivot, onFailureListener);
                }
            }
        }
        return "";
    }

    private List<Menu> getItemsForIconPath(final Menu selectedMenu, final NivelMenu menuLevel) {
        return SigmaBdManager.getItemsPathIconos(selectedMenu, menuLevel, onFailureListener);
    }

    public enum TitulosFijos {
        ADD_PROF(""),
        ENT_DINERO("Rellena tu Saldo"),
        SAL_DINERO("Salida de Dinero"),
        QR("Cobra y Paga con QR"),
        PAGOPROV("Pago a Proveedores"),
        MOV("Mis Movimientos"),
        VENTASDIA("Ventas del Día"),
        CALC("Calculadora"),
        MI_TARJETA("Mi Tarjeta"),
        BLOQUEO("Bloquear Tarjeta"),
        MI_CUENTA("Mi Cuenta"),

        /* Adquirencia */
        VENTA_RAPIDA("Venta Rapida"),
        COMPRA_MPOS("Compra tu MPos"),
        VENTAS("Mis Ventas"),
        NEGOCIO("Mi Negocio"),
        CONFIGURACION_MPOS("Configuración de MPos"),
        REPORTES("Mis Reportes");

        private final String titulo;
        TitulosFijos(final String titulo) {
            this.titulo = titulo;
        }

        public String getTitulo() {
            return titulo;
        }
    }

    class MenuLevelHolder extends AbstractHolder<Menu> {

        private final TextView txtName;
        private final ImageView imageView;

        public MenuLevelHolder(final View itemView) {
            super(itemView);
            txtName = mainView.findViewById(R.id.nombre_producto);
            imageView = mainView.findViewById(R.id.icono_producto);
        }

        @Override
        public void onBind(final Menu menu) {
            if ((nivelMenu != NivelMenu.CUARTO_NIVEL)) {
                flag++;
                String titulo = menu.getTexto();
                if (menu.getTexto().toLowerCase().contains("pfija")) {
                    boolean matchFound = false;
                    for (TitulosFijos s : (TitulosFijos.values())) {
                        if (menu.getTexto().toUpperCase().contains(s.name())) {
                            titulo = s.getTitulo();
                            matchFound = true;
                            break;
                        }
                    }

                    if (!matchFound) {
                        int colIdx = menu.getTexto().indexOf(':') + 1;
                        titulo = menu.getTexto().substring(colIdx);
                    }
                }
                if (txtName != null) {
                    if (flag == 8 && ((MposApplication) homeActivity.getApplication())
                            .getMenuFlag().equals("C1")
                    ) {
                        txtName.setText(R.string.ver_todas);
                    } else if (((MposApplication) homeActivity.getApplication())
                            .getMenuFlag().equals("C2")
                    ) {
                        txtName.setVisibility(View.GONE);
                        imageView.getLayoutParams().height = 200;
                        imageView.getLayoutParams().width = 200;
                        imageView.requestLayout();
                    } else {
                        txtName.setText(titulo);
                    }
                }
            } else if ((nivelMenu == NivelMenu.CUARTO_NIVEL) &&
                    ((MposApplication) homeActivity.getApplication())
                            .getMenuFlag() != null
            ) {
                if (((MposApplication) homeActivity.getApplication())
                        .getMenuFlag()
                        .equals("adapter")
                ) {
                    String titulo = menu.getTexto();
                    txtName.setText(UtilsKt.capitalizeWords(titulo));
                } else {
                    txtName.setVisibility(View.GONE);
                    imageView.getLayoutParams().height = 200;
                    imageView.getLayoutParams().width = 200;
                    imageView.requestLayout();
                }

            }
        }

        public void setIcon(final String filePathOne) {
            if (flag == 8 &&
                    ((MposApplication) homeActivity.getApplication())
                            .getMenuFlag()
                            .equals("C1")
            ) {
                imageView.setImageResource(R.drawable.ic_ver_todas);
            } else {
                final Resources resources = getMainView().getResources();
                final int width = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100,
                        resources.getDisplayMetrics()
                );
                final int height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100,
                        resources.getDisplayMetrics()
                );
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                if (StorageUtil.validarArchivo(StorageUtil.getStoragePath() + filePathOne)) {
                    final Bitmap bitmapOne =
                            Bitmap.createScaledBitmap(
                                    BitmapFactory.decodeFile(
                                            StorageUtil.getStoragePath() + filePathOne,
                                            options
                                    ),
                                    width,
                                    height,
                                    true
                            );
                    imageView.setImageBitmap(bitmapOne);
                } else {
                    imageView.setImageResource(Utilities.getAttributeReference(R.attr.fullcarga));
                }
            }
        }

        public void setIcon(final int resource) {
            imageView.setImageResource(resource);
        }
    }
}
