package com.pagatodoholdings.posandroid.secciones.productos;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.reportetrx.ReporteVentasInteractor;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.animaciones.AnimacionAlfa;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionSubVincular;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuReporteVentasFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mis_datos.ConfigMenuMisDatosFragment;
import com.pagatodoholdings.posandroid.secciones.money_in.MoneyInMenuFragment;
import com.pagatodoholdings.posandroid.secciones.money_out.MoneyOutFragment;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;
import com.pagatodoholdings.posandroid.secciones.reportes.MisReportesFragment;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Utilities;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TURNO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.VISTA_MENU_PRODUCTOS;

public class MenuProductosFragment extends AbstractFragment implements ProductoSeleccionado {
    private HomeActivity homeActivity;
    private NivelMenu nivelActual;
    private Menu menuSeleccionado;
    private MenuProductosListener menuProductosListener;
    private RecyclerView rvMenuProductos;
    private CardView cardCalendarContainer;
    private CardView cardCategoriesContainer;
    private LinearLayout linearLayoutCalendar;
    private CardView cardViewTab;
    private View promocional;
    private boolean mIsListViewShowing;
    private static final String LEVEL_KEY = "levelkey";
    private static final String MENU_KEY = "menu_key";
    private static final String MENU_TITLE_KEY = "menu_title_key";
    private TextView mTvChangeView;
    private BotonClickUnico registroShopper;
    private BotonClickUnico vincularDongle;
    private LinearLayout sinDongle;
    private String mMenuTitle;
    private List<Menu> itemsMenu;
    private SearchView searchView;
    private TextView txtRecargas, txtPaquetes, txtPines;
    private TextView txtTitle;
    private boolean isRecarga = false, isPaquete = false, isPin = false;
    private boolean isRecargasPines = false, isPadoDeServicios = false;
    private List<Menu> listaItemsNivel;
    private Button btnGoCalendar;


    public static MenuProductosFragment newInstance(final NivelMenu nivel, final Menu menu) {
        final MenuProductosFragment fragment = new MenuProductosFragment();
        final Bundle args = new Bundle();
        args.putSerializable(LEVEL_KEY, nivel);
        args.putString(MENU_KEY, new Gson().toJson(menu));
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuProductosFragment newInstance(
            final NivelMenu nivel,
            final Menu menu,
            String title
    ) {
        MenuProductosFragment menuProductos = newInstance(nivel, menu);
        Bundle args = menuProductos.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(MENU_TITLE_KEY, title);
        menuProductos.setArguments(args);
        return menuProductos;
    }

    public void setListener(final MenuProductosListener listener) {
        menuProductosListener = listener;
    }

    public void closeFragment(final CloseFragmentListener listener) {
        new AnimacionAlfa(
                rvMenuProductos,
                0,
                250,
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(final Animator animator) {
                        //NOT IMPLEMENTED
                    }

                    @Override
                    public void onAnimationEnd(final Animator animator) {
                        listener.onFragmentClosed();
                    }

                    @Override
                    public void onAnimationCancel(final Animator animator) {
                        //No se usa
                    }

                    @Override
                    public void onAnimationRepeat(final Animator animator) {
                        //No se usa
                    }
                });
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            homeActivity = (HomeActivity) getActivity();
            if (getArguments().containsKey(LEVEL_KEY)) {
                nivelActual = (NivelMenu) getArguments().getSerializable(LEVEL_KEY);
            }
            if (getArguments().containsKey(MENU_KEY)) {
                menuSeleccionado =
                        new Gson().fromJson(getArguments().getString(MENU_KEY), Menu.class);
            }
            if (getArguments().containsKey(MENU_TITLE_KEY)) {
                mMenuTitle = getArguments().getString(MENU_TITLE_KEY);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        int layoutId;
        switch (nivelActual) {
            case TERCER_NIVEL:
            case CUARTO_NIVEL:
                layoutId = R.layout.menu_productos_lvl_3_fragment;
                break;
            default:
                layoutId = R.layout.menu_productos_fragment;
                break;
        }


        View view = inflater.inflate(layoutId, container, false);

        if (mMenuTitle == null) {
            mMenuTitle = ((MposApplication) homeActivity.getApplication()).getMenuTitle();
        } else if (nivelActual == NivelMenu.TERCER_NIVEL) {
            ((MposApplication) homeActivity.getApplication()).setMenuTitle(mMenuTitle);
        }

        if ((nivelActual == NivelMenu.TERCER_NIVEL) || (nivelActual == NivelMenu.CUARTO_NIVEL)) {
            TextView title = view.findViewById(R.id.tv_titulo_menu);
            if (title != null && mMenuTitle != null) {
                final char[] delimiters = {' ', '_'};
                String titulo = WordUtils.capitalizeFully(mMenuTitle, delimiters);
                title.setText(titulo);
            }

            mTvChangeView = view.findViewById(R.id.tv_mostrar_lista);

            mIsListViewShowing = getViewPreference() == 1;
            changeButtonText();

            mTvChangeView.setOnClickListener(v -> {
                if (rvMenuProductos != null) {
                    if (mIsListViewShowing) {
                        showGridView();
                    } else {
                        showListView();
                    }
                    changeButtonText();
                }
            });

            TextView aumentaIngresos = view.findViewById(R.id.tv_aumentar_ingresos);
            if (MposApplication
                    .getInstance()
                    .getDatosLogin()
                    .getCliente()
                    .getTipo()
                    .equals("PROFESIONIST")
            ) {
                LinearLayout btns = view.findViewById(R.id.btns);
                btns.setVisibility(View.VISIBLE);
                aumentaIngresos.setVisibility(View.VISIBLE);
                aumentaIngresos.setEnabled(true);
                aumentaIngresos.setOnClickListener(v -> {
                });
            }
        }

        return view;
    }

    private void changeButtonText() {
        if (mIsListViewShowing) {
            SpannableString text = new SpannableString("Mostrar Mosaico");
            text.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            mTvChangeView.setText(text);
        } else {
            SpannableString text = new SpannableString("Mostrar Lista");
            text.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            mTvChangeView.setText(text);
        }
    }

    private int getViewPreference() {
        return preferenceManager.getValue(VISTA_MENU_PRODUCTOS);
    }

    /**
     * Guardar la ultima vista del usuario
     *
     * @param value 0 = mosaico, 1 = lista
     */
    private void saveViewPreference(int value) {
        preferenceManager.setValue(VISTA_MENU_PRODUCTOS, value);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMenuProductos = view.findViewById(R.id.rv_home_menu_productos_categoria);
        cardCalendarContainer = view.findViewById(R.id.cardViewCalendarContainer);
        cardCategoriesContainer = view.findViewById((R.id.cardViewCategoriasContainer));
        cardViewTab = view.findViewById(R.id.cardViewTab);
        searchView = view.findViewById(R.id.searchView);
        linearLayoutCalendar = view.findViewById(R.id.linearLayoutCalendar);
        txtRecargas = view.findViewById(R.id.textViewRecargas);
        txtPaquetes = view.findViewById(R.id.textViewPaquetes);
        txtPines = view.findViewById(R.id.textViewPines);
        btnGoCalendar = view.findViewById(R.id.btn_go_calendar);

        initToolbar(view);
        ArrayList menuSearch = new ArrayList<Menu>();

        if (nivelActual == NivelMenu.SEGUNDO_NIVEL) {
            promocional = view.findViewById(R.id.promocional_profesionista);
            registroShopper = promocional.findViewById(R.id.btn_registrar_profesionista);
            vincularDongle = promocional.findViewById(R.id.btn_vincular_dongle);
            sinDongle = promocional.findViewById(R.id.ll_sin_dongle);
        }

        itemsMenu = getItemList(
                menuSeleccionado,
                nivelActual,
                new BasicOnFailureListener(TAG, "ErrorAlConsultar")
        );

//        if (menuSeleccionado.getTexto().contains(ADQUIRENCIA)) {
//            configMenuAdquirencia();
//
//        } else {
        if (rvMenuProductos != null) {

            if (nivelActual == NivelMenu.TERCER_NIVEL) {

                if (mMenuTitle
                        .toLowerCase()
                        .equals("recargas y pines")
                ) {
                    isRecargasPines = true;
                    isPadoDeServicios = false;
                    cardViewTab.setVisibility(View.VISIBLE);

                    ((MposApplication) homeActivity.getApplication()).setMenuFlag("C2");

                    fillRechargesMenu();
                    changeTxtAndBackgroundColor(
                            txtRecargas,
                            R.color.colorWhite,
                            R.color.blue_ya_ganaste
                    );
                    isRecarga = true;
                    cardCalendarContainer.setVisibility(View.VISIBLE);

                    goToFourthLevel(
                            getItemList(
                                    new Menu(
                                            itemsMenu.get(0).getNivel(),
                                            itemsMenu.get(0).getTexto(),
                                            itemsMenu.get(0).getProducto(),
                                            itemsMenu.get(0).getIcono()
                                    ),
                                    NivelMenu.CUARTO_NIVEL,
                                    new BasicOnFailureListener(TAG, "ErrorAlConsultar")
                            )
                    );
                } else if (mMenuTitle
                        .toLowerCase()
                        .equals("pago de servicios")
                ) {
                    isPadoDeServicios = true;
                    isRecargasPines = false;
                    if (itemsMenu.size() > 8 &&
                            ((MposApplication) homeActivity.getApplication())
                                    .getMenuFlag()
                                    .equals("C1")
                    ) {
                        ((MposApplication) homeActivity.getApplication()).setMenuFlag("C1");
                        fillRecycler(
                                2,
                                MenuProductosFragment.this,
                                itemsMenu.subList(0, 8)
                        );
                    } else {
                        fillRecycler(
                                2,
                                MenuProductosFragment.this,
                                itemsMenu
                        );

                        ((MposApplication) homeActivity.getApplication()).setMenuFlag("C3");
                    }
                }
            } else if (nivelActual == NivelMenu.CUARTO_NIVEL) {
                cardCalendarContainer.setVisibility(View.GONE);
                cardCategoriesContainer.setVisibility(View.VISIBLE);
                if (itemsMenu.size() > 9) {
                    fillRecycler(
                            3,
                            MenuProductosFragment.this,
                            itemsMenu.subList(0, 9)
                    );
                } else {
                    fillRecycler(
                            3,
                            MenuProductosFragment.this,
                            itemsMenu
                    );
                }
            } else {

                ((MposApplication) homeActivity.getApplication()).setMenuFlag("C1");
                fillRecycler(3, this, itemsMenu);
                muestraEstiloMenu();
            }
        }
//        }

        if (cardCategoriesContainer != null) {
            cardCategoriesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fillLineRecycler();
                }
            });
        }

        if (cardViewTab != null) {
            txtRecargas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRecarga = true;
                    isPaquete = false;
                    isPin = false;
                    changeTxtAndBackgroundColor(
                            txtRecargas,
                            R.color.colorWhite,
                            R.color.blue_ya_ganaste
                    );

                    if (listaItemsNivel != null && !listaItemsNivel.isEmpty()) {
                        listaItemsNivel.clear();
                    }
                    listaItemsNivel = getItemList(
                            new Menu(
                                    itemsMenu.get(0).getNivel(),
                                    itemsMenu.get(0).getTexto(),
                                    itemsMenu.get(0).getProducto(),
                                    itemsMenu.get(0).getIcono()
                            ),
                            NivelMenu.CUARTO_NIVEL,
                            new BasicOnFailureListener(TAG, "ErrorAlConsultar")
                    );
                    goToFourthLevel(listaItemsNivel);

                    changeTxtAndBackgroundColor(txtPaquetes, R.color.black, R.color.colorWhite);
                    changeTxtAndBackgroundColor(txtPines, R.color.black, R.color.colorWhite);
                }
            });

            txtPaquetes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRecarga = false;
                    isPaquete = true;
                    isPin = false;
                    changeTxtAndBackgroundColor(
                            txtPaquetes,
                            R.color.colorWhite,
                            R.color.blue_ya_ganaste
                    );

                    if (listaItemsNivel != null && !listaItemsNivel.isEmpty()) {
                        listaItemsNivel.clear();
                    }
                    listaItemsNivel = getItemList(
                            new Menu(
                                    itemsMenu.get(1).getNivel(),
                                    itemsMenu.get(1).getTexto(),
                                    itemsMenu.get(1).getProducto(),
                                    itemsMenu.get(1).getIcono()
                            ),
                            NivelMenu.CUARTO_NIVEL,
                            new BasicOnFailureListener(TAG, "ErrorAlConsultar")
                    );
                    goToFourthLevel(listaItemsNivel);

                    changeTxtAndBackgroundColor(txtRecargas, R.color.black, R.color.colorWhite);
                    changeTxtAndBackgroundColor(txtPines, R.color.black, R.color.colorWhite);
                }
            });

            txtPines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRecarga = false;
                    isPaquete = false;
                    isPin = true;
                    changeTxtAndBackgroundColor(
                            txtPines,
                            R.color.colorWhite,
                            R.color.blue_ya_ganaste
                    );

                    if (listaItemsNivel != null && !listaItemsNivel.isEmpty()) {
                        listaItemsNivel.clear();
                    }

                    listaItemsNivel = getItemList(
                            new Menu(
                                    itemsMenu.get(2).getNivel(),
                                    itemsMenu.get(2).getTexto(),
                                    itemsMenu.get(2).getProducto(),
                                    itemsMenu.get(2).getIcono()
                            ),
                            NivelMenu.CUARTO_NIVEL,
                            new BasicOnFailureListener(TAG, "ErrorAlConsultar")
                    );
                    goToFourthLevel(listaItemsNivel);

                    changeTxtAndBackgroundColor(txtPaquetes, R.color.black, R.color.colorWhite);
                    changeTxtAndBackgroundColor(txtRecargas, R.color.black, R.color.colorWhite);
                }
            });
        }

        if (searchView != null) {
            searchView.setIconifiedByDefault(false);
            searchView.setOnCloseListener(() -> true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                private String producto = "";


                @Override
                public boolean onQueryTextSubmit(String query) {
                    producto = query;
                    menuSearch.clear();
                    if (isRecargasPines) {
                        if (isRecarga) {
                            listaItemsNivel = getItemList(
                                    new Menu(
                                            itemsMenu.get(0).getNivel(),
                                            itemsMenu.get(0).getTexto(),
                                            itemsMenu.get(0).getProducto(),
                                            itemsMenu.get(0).getIcono()
                                    ),
                                    NivelMenu.CUARTO_NIVEL,
                                    new BasicOnFailureListener(TAG, "ErrorAlConsultar")
                            );
                        }
                        for (Menu menu : listaItemsNivel) {
                            Log.e("items", menu.getTexto());
                            if (menu.getTexto() != null) {
                                if (menu.getTexto().toLowerCase().contains(query.toLowerCase())) {
                                    menuSearch.add(menu);
                                }
                            }
                        }
                    } else {
                        for (Menu menu : itemsMenu) {
                            Log.e("items", menu.getTexto());
                            if (menu.getTexto() != null) {
                                if (menu.getTexto().toLowerCase().contains(query.toLowerCase())) {
                                    menuSearch.add(menu);
                                }
                            }
                        }
                    }

                    if (menuSearch.size() > 0) {
                        fillSearchRecycler(menuSearch);
                    } else if (menuSearch.size() == 0) {
                        Toast.makeText(homeActivity, R.string.No_se_encuentra, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    producto = newText;
                    if (producto.isEmpty()) {
                        if (isRecargasPines) {
                            goToFourthLevel(listaItemsNivel);
                        } else if (isPadoDeServicios && nivelActual == NivelMenu.TERCER_NIVEL) {
                            goToThirdLevel(itemsMenu);
                        } else {
                            goToFourthLevel(itemsMenu);
                        }
                    }
                    return false;
                }

            });
        }

        if (linearLayoutCalendar != null) {
            //btnGoCalendar.setOnClickListener(view12 -> homeActivity.loadCalendarPresentation());
            btnGoCalendar.setOnClickListener(view12 -> homeActivity.goToCalendarFromProducts());
        }

    }


    private List<Menu> getItemList(
            Menu menuSeleccionado,
            NivelMenu nivel,
            BasicOnFailureListener failure
    ) {
        return Utilities.getItemsDeNivel(menuSeleccionado, nivel, failure);
    }

    private void goToFourthLevel(List<Menu> menus) {
        if (itemsMenu.size() > 9) {
            fillRecycler(
                    3,
                    MenuProductosFragment.this,
                    menus.subList(0, 9)
            );
        } else {
            fillRecycler(
                    3,
                    MenuProductosFragment.this,
                    menus
            );
        }
    }

    private void goToThirdLevel(List<Menu> menus) {
        if (itemsMenu.size() > 9) {
            fillRecycler(
                    2,
                    MenuProductosFragment.this,
                    menus.subList(0, 9)
            );
        } else {
            fillRecycler(
                    2,
                    MenuProductosFragment.this,
                    menus
            );
        }
    }

    private void fillRechargesMenu() {
        txtRecargas.setText(itemsMenu.get(0).getTexto());
        txtPaquetes.setText(itemsMenu.get(1).getTexto());
        txtPines.setText(itemsMenu.get(2).getTexto());
    }

    private void changeTxtAndBackgroundColor(TextView txt, int colorTxt, int colorBackground) {
        txt.setTextColor(getResources().getColor(colorTxt));
        txt.setBackgroundColor(getResources().getColor(colorBackground));
    }

    private void fillRecycler(
            int spanCount,
            ProductoSeleccionado productoSeleccionado,
            List<Menu> subMenu
    ) {
        rvMenuProductos.setLayoutManager(
                new GridLayoutManager(
                        getActivity(),
                        spanCount
                )
        );
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        subMenu,
                        productoSeleccionado,
                        homeActivity
                )
        );
    }

    private void fillLineRecycler() {
        itemsMenu =
                Utilities.getItemsDeNivel(
                        menuSeleccionado,
                        NivelMenu.CUARTO_NIVEL,
                        new BasicOnFailureListener(
                                TAG,
                                "ErrorAlConslultar"
                        )
                );

        rvMenuProductos.setLayoutManager(
                new LinearLayoutManager(getActivity())
        );
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        itemsMenu,
                        MenuProductosFragment.this,
                        R.layout.item_rv_menu_productos_adapter_3_list,
                        homeActivity
                )
        );
    }

    private void fillSearchRecycler(List<Menu> menu) {
        rvMenuProductos.setLayoutManager(
                new LinearLayoutManager(getActivity())
        );
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        menu,
                        MenuProductosFragment.this,
                        R.layout.item_rv_menu_productos_adapter_3_list,
                        homeActivity
                )
        );
    }

    private void configMenuAdquirencia() {
        if (itemsMenu.get(0).getTexto().contains("pfija") &&
                itemsMenu.get(0)
                        .getTexto()
                        .contains(MenuProductosAdapter.TitulosFijos.ADD_PROF.name())) {
            rvMenuProductos.setVisibility(View.GONE);
            promocional.setVisibility(View.VISIBLE);
            sinDongle.setVisibility(View.GONE);
            registroShopper.setVisibility(View.VISIBLE);
            registroShopper
                    .setOnClickListener(v ->
                            activity.cambiaDeActividadSinCerrar(RegistroDatosNegocio.class)
                    );
        } else {
            if (preferenceManager.isExiste(DONGLE_VINCULADO)) {
                if (rvMenuProductos != null) {
                    rvMenuProductos.setLayoutManager(
                            new GridLayoutManager(
                                    getActivity(),
                                    3
                            )
                    );
                    rvMenuProductos.setAdapter(
                            new MenuProductosAdapter(
                                    nivelActual,
                                    itemsMenu,
                                    this,
                                    homeActivity
                            )
                    );
                    muestraEstiloMenu();
                }
            } else {
                rvMenuProductos.setVisibility(View.GONE);
                promocional.setVisibility(View.VISIBLE);
                sinDongle.setVisibility(View.VISIBLE);
                registroShopper.setVisibility(View.GONE);
                vincularDongle.setOnClickListener(v -> {
                    HomeActivity activity = (HomeActivity) getActivity();
                    final ConfigMenuConfiguracionSubVincular fragmentSubvincular =
                            new ConfigMenuConfiguracionSubVincular();
                    fragmentSubvincular.setListener(activity);
                    activity.cambiaPantallaCompleta(
                            View.GONE,
                            activity.generaListener(fragmentSubvincular)
                    );
                });
            }
        }
    }

    private void initToolbar(View view) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar_menu);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_icono_back);
            homeActivity.setSupportActionBar(toolbar);
            homeActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar.setNavigationOnClickListener(v -> isTomandoBack());
        }
    }

    private void muestraEstiloMenu() {
        switch (nivelActual) {
            case TERCER_NIVEL:
            case CUARTO_NIVEL:
                int menuToShow = getViewPreference();
                if (menuToShow == 0) {
                    showGridView();
                } else {
                    showListView();
                }

                break;
            default:
                showMainMenuView();
                break;
        }
    }

    private void showMainMenuView() {
        rvMenuProductos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        itemsMenu,
                        this,
                        homeActivity
                )
        );
    }

    private void showGridView() {
        mIsListViewShowing = false;
        saveViewPreference(0);
        rvMenuProductos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        itemsMenu,
                        this,
                        R.layout.item_rv_menu_productos_adapter_3,
                        homeActivity
                )
        );
    }

    private void showListView() {
        mIsListViewShowing = true;
        saveViewPreference(1);
        rvMenuProductos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMenuProductos.setAdapter(
                new MenuProductosAdapter(
                        nivelActual,
                        itemsMenu,
                        MenuProductosFragment.this,
                        R.layout.item_rv_menu_productos_adapter_3_list,
                        homeActivity
                )
        );
    }

    @Override
    public void notificaProductoSeleccionado(final Menu menu) {
        String menuTextoString = menu.getTexto().toUpperCase();
        if (menuTextoString.contains("PFIJA")) {
            String ultimoCierreTurno = MposApplication.getInstance().getPreferenceManager().getValue(TURNO).toString();

            // Sección de ADQ

            if (menuSeleccionado.getTexto().equalsIgnoreCase("Adquirencia")) {
                if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.CONFIGURACION_MPOS.getTitulo().toUpperCase())) {
                    if (preferenceManager.isExiste(DONGLE_VINCULADO) &&
                            MposApplication.getInstance().getDatosNegocio() != null &&
                            MposApplication.getInstance().getDatosNegocio().getTipnegocio() != 0) {
                        homeActivity.cargarFragmentSettings();
                    } else {
                        homeActivity.cargarComprarKit();
                    }
                } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.COMPRA_MPOS.getTitulo().toUpperCase())) {
                    homeActivity.cargarComprarKit();
                } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.VENTA_RAPIDA.getTitulo().toUpperCase())) {
                    if (preferenceManager.isExiste(DONGLE_VINCULADO) &&
                            MposApplication.getInstance().getDatosNegocio() != null &&
                            MposApplication.getInstance().getDatosNegocio().getTipnegocio() != 0) {
                        homeActivity.cargarFragmentCobrar();
                    } else {
                        homeActivity.cargarComprarKit();
                    }
                } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.REPORTES.getTitulo().toUpperCase())) {
                    homeActivity.loadMyReports(new MisReportesFragment());
                } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.NEGOCIO.getTitulo().toUpperCase())) {
                    homeActivity.loadMyBusiness(new ConfigMenuMisDatosFragment());
                } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.VENTAS.getTitulo().toUpperCase())) {

                    homeActivity.loadMyMovients(ConfigMenuReporteVentasFragment.getInstance(
                            homeActivity.getSaldo(),
                            ReporteVentasInteractor.TipoReporte.REPORTE_PCI,
                            "2",
                            ultimoCierreTurno
                    ));
                }
            } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.MOV.name())) {
                if (menuSeleccionado.getTexto().equals("CUPO")) {
                    homeActivity.loadMyMovients(ConfigMenuReporteVentasFragment.getInstance(
                            homeActivity.getSaldo(),
                            ReporteVentasInteractor.TipoReporte.REPORTE_VENTAS,
                            "2",
                            ultimoCierreTurno
                    ));
                }
            } else if (
                    menuTextoString.contains(MenuProductosAdapter.TitulosFijos.VENTASDIA.name())
            ) {

                homeActivity.loadMyMovients(ConfigMenuReporteVentasFragment.getInstance(
                        homeActivity.getSaldo(),
                        ReporteVentasInteractor.TipoReporte.REPORTE_PCI_DIA,
                        "2",
                        ultimoCierreTurno
                ));

            } else if (menuTextoString.contains(MenuProductosAdapter.TitulosFijos.QR.name())) {
                homeActivity.loadQrDefault();
                /*
                homeActivity.startActivityForResult(
                        new Intent(getActivity(),
                                CodeScannerActivity.class
                        ),
                        RC_SCAN
                );

                 */
            } else if (
                    menuTextoString.contains(MenuProductosAdapter.TitulosFijos.ENT_DINERO.name())
            ) {
                final MoneyInMenuFragment moneyInMenuFragment = new MoneyInMenuFragment();
                moneyInMenuFragment.setListener(homeActivity);
                homeActivity.cargarFragmentMoneyIn();
            } else if (
                    menuTextoString.contains(MenuProductosAdapter.TitulosFijos.SAL_DINERO.name())
            ) {
                final MoneyOutFragment moneyOutFragment = new MoneyOutFragment();
                moneyOutFragment.setListener(homeActivity);
                homeActivity.cargarFragmentMoneyOut();
            }
        } else if (menu.getProducto() != null && !menu.getProducto().equals("")) {
            menuProductosListener.onProductoSeleccionado(nivelActual, menu);
        } else if (nivelActual == NivelMenu.SEGUNDO_NIVEL) {
            menuProductosListener.onSubMenuSeleccionado(
                    nivelActual.getNivelSiguiente(),
                    menu,
                    menuTextoString
            );
        } else if (nivelActual == NivelMenu.TERCER_NIVEL && (
                ((MposApplication) homeActivity.getApplication())
                        .getMenuFlag()
                        .equals("C1") ||
                        ((MposApplication) homeActivity.getApplication())
                                .getMenuFlag()
                                .equals("C3")
        )
        ) {
            if (((MposApplication) homeActivity.getApplication())
                    .getMenuFlag()
                    .equals("C3")
            ) {
                ((MposApplication) homeActivity.getApplication()).setMenuFlag("C1");
            }

            menuProductosListener.onSubMenuSeleccionado(
                    nivelActual.getNivelSiguiente(),
                    menu,
                    menuTextoString
            );
        } else if (nivelActual == NivelMenu.TERCER_NIVEL &&
                ((MposApplication) homeActivity.getApplication()).getMenuFlag().equals("C2")
        ) {
            menuProductosListener.onSubMenuSeleccionado(
                    nivelActual,
                    menu,
                    menuTextoString
            );

        }/*else if(menu.getTexto().toLowerCase().equals("recargas")){
            homeActivity.cargaFragmentRecargasYPines();
        }*/
    }

    @Override
    public void notificaErrorProducto() {
        menuProductosListener.onProductoError(nivelActual, menuSeleccionado);
    }

    @Override
    protected boolean isTomandoBack() {
        if (menuProductosListener != null) {
            if (!nivelActual.tieneNivelPrevio()) {
                return false;
            } else {
                menuProductosListener.onRetroceso(nivelActual.getNivelPrevio());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onFailure(final Throwable throwable) {
        //Nothing
    }

    public interface CloseFragmentListener {
        void onFragmentClosed();
    }


    private void cargarFragmentKit() {
        /*final KitDatosEnvioFragment kitDatosEnvioFragment = new KitDatosEnvioFragment();
        homeActivity.cargarFragments(View.GONE, homeActivity.generaListener(kitDatosEnvioFragment));*/
        Intent intent = new Intent(getActivity(), KitSolicitarActivity.class);
        intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_COMPRA_KIT_BY_MENU);
        startActivityForResult(intent, 4);
    }


}