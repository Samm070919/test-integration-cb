package com.pagatodoholdings.posandroid.secciones.home;//NOPMD

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.pagatodo.notifications.NotificacionesDialogFragment;
import com.pagatodo.notifications.NotificacionesProvider;
import com.pagatodo.qposlib.broadcasts.BroadcastManager;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.DialogoSeleccionOperaciones;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController;
import com.pagatodoholdings.posandroid.databinding.ActivityHomeBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.general.interfaces.RetrocesoListener;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;
import com.pagatodoholdings.posandroid.secciones.acquiring.AdqRouter;
import com.pagatodoholdings.posandroid.secciones.acquiring.ChargeActivityListner;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeFragment;
import com.pagatodoholdings.posandroid.secciones.acquiring.settings.SettingFragment;
import com.pagatodoholdings.posandroid.secciones.calendar.fragments.CalendarPresentationFragment;
import com.pagatodoholdings.posandroid.secciones.calendar.fragments.MyRemindersCalendarFragments;
import com.pagatodoholdings.posandroid.secciones.formularios.FormularioFragmentFactory;
import com.pagatodoholdings.posandroid.secciones.formularios.FormularioGenerico;
import com.pagatodoholdings.posandroid.secciones.kit.KitDatosEnvioFragment;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarFragment;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuAyudaFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionSubVincular;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuDongleVinculadoFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuReporteVentasFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuSeguridadFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria.ConfigMenuInfoBancariaAndCardsFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria.ConfigMenuInfoBancariaFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_legal.ConfigMenuLegalFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mi_cuenta.ConfigMenuMiCuentaFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mis_datos.ConfigMenuMisDatosFragment;
import com.pagatodoholdings.posandroid.secciones.money_in.MoneyInMenuFragment;
import com.pagatodoholdings.posandroid.secciones.money_out.MoneyOutFragment;
import com.pagatodoholdings.posandroid.secciones.movimientos.MisMovimientosDialog;
import com.pagatodoholdings.posandroid.secciones.premium.PremiumFragment;
import com.pagatodoholdings.posandroid.secciones.productos.MenuProductosFragment;
import com.pagatodoholdings.posandroid.secciones.productos.MenuProductosListener;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionesProvider;
import com.pagatodoholdings.posandroid.secciones.promociones.view.PromocionesFragment;
import com.pagatodoholdings.posandroid.secciones.qrcode.CodigoQrFragment;
import com.pagatodoholdings.posandroid.secciones.qrcode.DefaultQrFragment;
import com.pagatodoholdings.posandroid.secciones.qrcode.PagoQrFragment;
import com.pagatodoholdings.posandroid.secciones.reportes.MisReportesFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosService;
import com.pagatodoholdings.posandroid.secciones.retrofit.SaldoBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.SaldosResponseBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TransactionService;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.secciones.ticket.EmailTicketFragment;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.ManejadorFragments;
import com.pagatodoholdings.posandroid.utils.NotificationsUtils;
import com.pagatodoholdings.posandroid.utils.Utilities;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.oper.TipoOperacion;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pagatodo.sigmalib.NivelMenu.SEGUNDO_NIVEL;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DB_NAME;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.FECHA_RECORDATORIO_NO_PROGRAM;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NOMBRE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.SUPER_APP_ACCOUNT;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIEMPO_INACTIVIDAD;
import static com.pagatodoholdings.posandroid.analytics.Event.EVENT_SELECTED_MENU_CATEGORIAS;
import static com.pagatodoholdings.posandroid.analytics.Event.EVENT_SELECTED_PRODUCT;
import static com.pagatodoholdings.posandroid.analytics.Event.EVENT_SELECTED_SUB_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.RC_SCAN;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_COMPRA_KIT;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_CODE_ADQ;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_DATOS_NEGOCIO_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_REGISTERPROFESIONISTA_BY_BUTTON;
import static com.pagatodoholdings.posandroid.utils.Constantes.RESULT_CODE_ADQ;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.enums.Constantes.ADQUIRENCIA;
import static com.pagatodoholdings.posandroid.utils.enums.Constantes.CUPO;
import static net.fullcarga.android.api.oper.TipoOperacion.INIT_LIST;

@SuppressWarnings("PMD.GodClass")
public class HomeActivity extends AbstractActivity implements ChargeActivityListner { //NOSONAR //NOPMD
    //----------UI-------------------------------------------------------
    private ActivityHomeBinding binding;
    private AdqRouter router;

    public ActivityHomeBinding getBinding() {
        return binding;
    }

    //-----Inst ----------------------------------------------------------
    private ManejadorFragments manejadorFragments;
    private MenuProductosListener menuProductosListener;
    private NumberFormat numberFormat;
    private Map<String, BigDecimal> saldos;
    private final List<Menu> listaMenus = new ArrayList<>();
    private CountDownTimer temporizador;
    private ViewPager vpProductos;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private Badge notificacionesBadge;
    private Badge promocionesBadge;
    private Locale locale = new Locale("es", "MX");
    private Date currentDay = new GregorianCalendar(locale).getTime();
    private String fechaInicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDay);
    private String fechaFin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDay);
    boolean recordatoriosPendientes = false;
    boolean isListServicesActivityRoot = false;
    //----- Var ----------------------------------------------------------
    private boolean temporizadorEjecutando;
    private long tiempoUltimoClick;
    int contenedor;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private BigDecimal saldo;
    private long mLastClickTime = 0;

    public ManejadorFragments getManejadorFragments() {
        return manejadorFragments;
    }

    public List<Menu> getListaMenus() {
        return listaMenus;
    }

    private List<FavoritoBean> listaFavoritos;

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.nav_cobrar:
                if (preferenceManager.isExiste(DONGLE_VINCULADO) &&
                        MposApplication.getInstance().getDatosNegocio() != null &&
                        MposApplication.getInstance().getDatosNegocio().getTipnegocio() != 0) {
                    cargarFragmentCobrar();
                } else {
                    cargarComprarKit();
                }
                return true;
            case R.id.nav_home:
                restauraHome();
                return true;
            case R.id.nav_calendario:
                goToCalendarFromProducts();
                return true;
            case R.id.nav_promos:
                promocionesBadge.hide(true);
                cargarFragmentPromociones();
                return true;
            case R.id.nav_premium:
                cargarFragmentPremium();
                return true;

            default:
        }
        return false;
    };

    public void goToCalendarFromProducts() {
        if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
            if (listaFavoritos != null && !listaFavoritos.isEmpty()) {
                loadMyRemindersCalendar();
            } else {
                loadCalendarPresentation();
            }
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public void showUpgradeProfesionist() {
        final ActivityRecibePagosTarjeta recibePagosTarjeta = new ActivityRecibePagosTarjeta();

        manejadorFragments.cargarFragment(recibePagosTarjeta, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        //   cambiaFullScreen(View.GONE, generaListener(recibePagosTarjeta));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SCAN) {
            if (data != null && data.hasExtra("DECODED")) {
                String qrData = data.getStringExtra("DECODED");
                final PagoQrFragment fragment = PagoQrFragment.newInstance(qrData);
                fragment.setListener(this);

                manejadorFragments.cargarFragment(fragment, contenedor);

                binding.container.setVisibility(View.GONE);

                if (binding.navBar.getVisibility() == View.GONE) {
                    binding.navBar.setVisibility(View.VISIBLE);
                }

                // cambiaFullScreen(View.GONE, generaListener(fragment));
            } else if (data != null && data.hasExtra("COBRAR")) {
                cargarFragmentQr();
            } else {
                restauraHome();
                restauraMenu();
            }
        } else if (requestCode == REQUEST_CODE_ADQ && resultCode == RESULT_CODE_ADQ) {
            restauraMenu();
            restauraHome();
        } else if (resultCode == REQUEST_ADD_CARD_BY_MENU) {
            //restauraHome();
        } else if (resultCode == REQUEST_DATOS_NEGOCIO_BY_MENU) {
            restauraHome();
        } else if (resultCode == REQUEST_ADD_CARD_BY_COMPRA_KIT) {
            restauraHome();
        } else if (resultCode == REQUEST_REGISTERPROFESIONISTA_BY_BUTTON) {
            restauraHome();
        }
    }

    @Override
    public void setTitle(String title) {
        this.binding.titleToolbar.setText(title);
        if (title.isEmpty()) {
            this.binding.titleToolbar.setVisibility(View.GONE);
        } else {
            this.binding.titleToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        this.binding.subtitleFrag.setText(subtitle);
        if (subtitle.isEmpty()) {
            this.binding.subtitleFrag.setVisibility(View.GONE);
        } else {
            this.binding.subtitleFrag.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void hideBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void hideToolbar() {
        this.binding.toolbar.setVisibility(View.GONE);
    }

    @Override
    public void showToolbar() {
        this.binding.toolbar.setVisibility(View.VISIBLE);
    }

    public NumberFormat getNumberFormat() {
        return numberFormat != null ? numberFormat : NumberFormat.getCurrencyInstance();
    }

    protected void initUi() {
        AsyntaskFavorites sendEmailAsyn = new AsyntaskFavorites();
        sendEmailAsyn.execute();
        inicializarDatosNegocio();

        // Inicializa variables yAxis vistas
        if (preferenceManager.isExiste(DB_NAME)) {
            numberFormat = SigmaBdManager.getNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, "Error al formatear saldo"));
            binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
            initDebugResources();
            setTituloBar();
            vpProductos = findViewById(R.id.vp_productos);

            // Inicializa variables yAxis vistas
            FragmentManager fragmentManager = getSupportFragmentManager();
            manejadorFragments = new ManejadorFragments(fragmentManager);
            menuProductosListener = new MenuProductosListener() {
                @Override
                public void onProductoSeleccionado(final NivelMenu nivelMenu, final Menu menu) {
                    firebaseAnalytics.logEvent(EVENT_SELECTED_PRODUCT.key + menu.getTexto(), null);
                    final Fragment actualFragment = getSupportFragmentManager().findFragmentById(contenedor);
                    if (actualFragment instanceof MenuProductosFragment) {
                        ((MenuProductosFragment) actualFragment).closeFragment(() -> cargaFormulario(nivelMenu, menu));
                    }
                }

                @Override
                public void onSubMenuSeleccionado(final NivelMenu nivelMenu, final Menu menu, final String menuTitle) {
                    firebaseAnalytics.logEvent(EVENT_SELECTED_SUB_MENU.key + menu.getTexto(), null);
                    cargaSubMenu(nivelMenu, menu, menuTitle);
                }

                @Override
                public void onProductoError(final NivelMenu nivelMenu, final Menu menu) {
                    despliegaModal(true, false, getString(R.string.app_name),
                            getString(R.string.error_menu), () -> {
                                if (nivelMenu.tieneNivelPrevio()) {
                                    retrocedeMenu(nivelMenu);
                                } else {
                                    cambiaDeActividad(LoginActivity.class);
                                }
                            });
                }

                @Override
                public void onRetroceso(final NivelMenu nivelMenu) {
                    retrocedeMenu(nivelMenu);
                }
            };

            contenedor = binding.flMainPantallaCompleta.getId();

            instanciarContadorLogOut(MposApplication.getInstance().getPreferenceManager().getValue(TIEMPO_INACTIVIDAD));
            iniciarContador();
            setReceivers();
            cargaMenuCategorias();
            getSaldoFromService();
            initNavBar();
            consultaNotificaciones();
            consultaPromos();
        } else {
            onActualizarProductos();
        }

        /*
         *
         * Se va a utilizar PREFERENCE_USER_KEY como preferencia temporal para saber si es la primera vez que ingresas
         * al Home para que te muestre el dialogo de Vincular/Comprar.
         *
         * NOTA: GENERAR SU PROPIA PREFERENCIA EN CONSTANTES
         *
         * */

        initAdq();
        hideBackButton();
        validateSuperAppAccount();
        binding.tvSaldo.setNewTextSizeIndividualMonto(16f, 26f, 16f);
        binding.imageViewHamburguer.setOnClickListener(v -> {
            cargarFragmentMiCuenta();
        });
        ApiData.APIDATA.setVersionEmv(BuildConfig.VERSION_NAME); // Set this value to get it shown at the ticket generator
    }

    private void validateSuperAppAccount() {
        if (!preferenceManager.isExiste(SUPER_APP_ACCOUNT)) {
            preferenceManager.setValue(SUPER_APP_ACCOUNT, "1");
        } else {
            preferenceManager.setValue(SUPER_APP_ACCOUNT, "0");
        }
    }

    public void showDialogBotonAceptar(int layout, String buttonTextVincular, String buttonTextComprar,
                                       ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final ImageView ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(view1 -> alertDialog.dismiss());
        final BotonClickUnico btnVincular = view.findViewById(R.id.btnVincular);
        btnVincular.setText(buttonTextVincular);
        btnVincular.setTextSize(14);
        btnVincular.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });

        final BotonClickUnico btnComprar = view.findViewById(R.id.btnComprar);
        btnComprar.setText(buttonTextComprar);
        btnComprar.setTextSize(14);
        btnComprar.setOnClickListener(view1 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    public void showDialogReminders(int layout, String btnTextCancel, String btnTextSuccess, int imageResources,
                                    String title, String body,
                                    ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final ImageView ivImage = view.findViewById(R.id.ivReminders);
        ivImage.setImageResource(imageResources);
        final TextView tvTitle = view.findViewById(R.id.tvTitleReminder);
        final TextView tvBody = view.findViewById(R.id.tvBodyReminder);
        tvTitle.setText(title);
        tvBody.setText(body);
        final BotonClickUnico btnCancel = view.findViewById(R.id.btnCancelarReminder);
        btnCancel.setText(btnTextCancel);
        btnCancel.setTextSize(14);
        btnCancel.setOnClickListener(view1 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });

        final BotonClickUnico btnSuccess = view.findViewById(R.id.btnSuccessReminder);
        btnSuccess.setText(btnTextSuccess);
        btnSuccess.setTextSize(14);
        btnSuccess.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    public void initAdq() {
        router = new AdqRouter(this);
        setSupportActionBar(this.binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public AdqRouter getRouter() {
        return router;
    }

    public void closeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (fragment.getParentFragment() != null) {
            ((DialogFragment) fragment.getParentFragment()).dismiss();
        } else {
            regresaMenu();
        }
    }

    private void inicializarDatosNegocio() {
        if (MposApplication.getInstance().getDatosNegocio() == null || MposApplication.getInstance().getDatosNegocio().getTipnegocio() == 0) {
            consumirServicioDatosNegocio();
        }
    }

    private void initDebugResources() {
        if (binding != null && MposApplication.getInstance().isBuildDebug()) {
//            String tpv = ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod();
            //binding.tvCodVersion.setText(tpv);
            //binding.tvCodVersion.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTituloBar() {
        binding.titleToolbar.setText("Hola " + MposApplication.getInstance().getPreferenceManager().getValue(NOMBRE, ""));
    }

    private void initNavBar() {
        bottomNavigationViewEx = binding.getRoot().findViewById(R.id.nav_bar);
        bottomNavigationViewEx.setSelectedItemId(isListServicesActivityRoot ? R.id.nav_calendario : R.id.nav_home);
        int centerPosition = 2;
        int size = 36;
        bottomNavigationViewEx.setIconSizeAt(centerPosition, size, size);
        bottomNavigationViewEx.setTextSize(10);
        bottomNavigationViewEx.setIconMarginTop(centerPosition, BottomNavigationViewEx.dp2px(this, 12));
        bottomNavigationViewEx.setItemBackground(centerPosition, R.color.colorWhite);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(itemSelectedListener);
        binding.navBarInbox.setOnClickListener(view -> {
            final NotificacionesDialogFragment notificaciones = new NotificacionesDialogFragment();
            notificaciones.show(getSupportFragmentManager(), "NotificacionesDialogFragment");
        });
        notificacionesBadge = new QBadgeView(this).setGravityOffset(-2, -4, true).bindTarget(binding.navBarInbox);
        promocionesBadge = new QBadgeView(this).setGravityOffset(0, -1, true).bindTarget(binding.getRoot().findViewById(R.id.nav_promos));

    }

    private int indexCategoria(final String categoria) {
        int iCategoria = 1;
        List<Menu> categorias = SigmaBdManager.getCategorias(throwable -> {
            cambiaDeActividad(LoginActivity.class);
            LOGGER.throwing(TAG, 1, throwable, "Error al acceder a la base de datos");
        });
        for (Menu m : categorias) {
            if (m.getTexto().equals(categoria)) {
                return categorias.indexOf(m);
            }
        }
        return iCategoria;
    }

    public void consultaNotificaciones() {

        final NotificacionesProvider.NotificacionCallback notificacionCallback =
                new NotificacionesProvider.NotificacionCallback() {
            @Override
            public void onUpdate(Integer number) {
                if (number == 1) {
                    notificacionesBadge.setBadgeText("+1");
                } else {
                    notificacionesBadge.hide(true);
                }
            }
        };

        com.pagatodo.notifications.AbstractDialogFragment.initIdentity(
                getString(
                        R.string.wallet_notificacion_path,
                        BuildConfig.APPLICATION_ID.substring(
                                0,
                                BuildConfig.APPLICATION_ID.length() - 3
                        ),
                        MposApplication.getInstance().getDatosLogin().getCliente().getPais()
                ),
                ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod()
        );

        NotificacionesProvider
                .getInstance(getApplicationContext())
                .setNotificacionCallback(notificacionCallback);

    }

    public void consultaPromos() {
        final PromocionesProvider.PromocionCallback promocionCallback =
                new PromocionesProvider.PromocionCallback() {
            @Override
            public void onUpdate(Integer notificacionesCount) {
                if (notificacionesCount > 0) {
                    promocionesBadge.setBadgeText("+1");
                } else {
                    promocionesBadge.hide(true);
                }
            }
        };

        PromocionesProvider.getInstance(
                getApplicationContext(),
                getString(
                        R.string.firestore_cupones,
                        BuildConfig.APPLICATION_ID.substring(
                                0,
                                BuildConfig.APPLICATION_ID.length() - 3
                        ),
                MposApplication.getInstance().getDatosLogin().getCliente().getPais(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod())).setPromocionCallback(promocionCallback);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            iniciarContador();
        }
        return super.dispatchTouchEvent(event);
    }

    public void iniciarContador() {
        if (SystemClock.elapsedRealtime() - tiempoUltimoClick < 1000) {
            return;
        }
        tiempoUltimoClick = SystemClock.elapsedRealtime();
        if (temporizador != null) {
            if (!temporizadorEjecutando) {
                temporizadorEjecutando = true;
                temporizador.start();
            } else {
                temporizador.cancel();
                temporizador.start();
            }
        }
    }

    public void instanciarContadorLogOut(final long time) {
        final long TIEMPO = time;
        final long INTERVALO = 1000;

        temporizador = new CountDownTimer(TIEMPO, INTERVALO) {
            @Override
            public void onTick(final long time) {
                LOGGER.fine("TIEMPO INACTIVIDAD", String.valueOf(time / INTERVALO));
            }

            @Override
            public void onFinish() {
                temporizadorEjecutando = false;
                final TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.ic_logo, typedValue, true);
                NotificationsUtils
                        .construirNotificacion(HomeActivity.this,
                                typedValue.resourceId, getString(R.string.sesion_finalizada),
                                getString(R.string.identificate));

                finishApp();
            }
        };
    }

    public void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.cancel();
        }
    }

    private void setAgenteSaldo(final CurrencyImport cash) {
        runOnUiThread(() -> {
            binding.tvSaldo.setMonto(cash);
        });
    }

    private void cargaMenuCategorias() {
        final List<Menu> menuCategories = SigmaBdManager.getCategorias(throwable -> cambiaDeActividad(LoginActivity.class));

        binding.indicador.initializeIndicators(menuCategories.size());
        final CarruselHomePageAdapter carruselHomePageAdapter = new CarruselHomePageAdapter(menuCategories);
        vpProductos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastPage = -1;

            @Override
            public void onPageScrolled(final int index, final float value, final int index1) {
                //No implementation
            }

            @Override
            public void onPageSelected(final int index) {
                binding.balanceContainer.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
                goToOnPageSelected(index, lastPage, menuCategories);
                binding.ivPestana.setVisibility(index == 1 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(final int index) {
                //No implementation
            }
        });
        vpProductos.setAdapter(carruselHomePageAdapter);
        vpProductos.setCurrentItem(indexCategoria(CUPO));
    }

    private void goToOnPageSelected(int index, int lastPage, List<Menu> menuCategories) {
//        if (index == 2){
//            showCobrar();
//        } else {
//            if (index > lastPage) {
//                new Handler().postDelayed(() -> cargaFragmentoCategoriaMenu(menuCategories.size() > index ? menuCategories.get(index) : new Menu()), 1);
//                binding.indicador.animateStateChange(index);
//            } else {
        new Handler().postDelayed(() -> cargaFragmentoCategoriaMenu(menuCategories.size() > index ? menuCategories.get(index) : new Menu()), 11);
        binding.indicador.animateStateChange(index);
//            }
//        }
    }

    private void retrocedeMenu(final NivelMenu nivelMenu) {
        getListaMenus().remove(getListaMenus().size() - 1);
        final MenuProductosFragment menuProductosFragment = MenuProductosFragment.newInstance(nivelMenu, getListaMenus().get(getListaMenus().size() - 1));
        menuProductosFragment.setListener(menuProductosListener);
        manejadorFragments.cargarFragment(menuProductosFragment, contenedor);
        if (!nivelMenu.tieneNivelPrevio()) {
            cambiaFullScreen(View.VISIBLE, new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(final Transition transition) {
                    //No implementation
                }

                @Override
                public void onTransitionEnd(final Transition transition) {
                    //NO implementado
                    restauraMenu();
                }

                @Override
                public void onTransitionCancel(final Transition transition) {
                    //NO implementado
                }

                @Override
                public void onTransitionPause(final Transition transition) {
                    //NO implementado
                }

                @Override
                public void onTransitionResume(final Transition transition) {
                    //NO implementado
                }
            });
        }
    }

    private void cargaFragmentoCategoriaMenu(final Menu menu) {
        firebaseAnalytics.logEvent(EVENT_SELECTED_MENU_CATEGORIAS.key + menu.getTexto(), null);
        final MenuProductosFragment menuProductosFragment = MenuProductosFragment.newInstance(SEGUNDO_NIVEL, menu);
        menuProductosFragment.setListener(menuProductosListener);
        manejadorFragments.cargarFragment(menuProductosFragment, contenedor);
        getListaMenus().clear();
        getListaMenus().add(menu);
    }

    private void cargaSubMenu(final NivelMenu nivelMenu, final Menu menu, final String menuTitle) {
        final MenuProductosFragment menuProductosFragment = MenuProductosFragment.newInstance(nivelMenu, menu, menuTitle);
        lauchMenuProductosFragment(menu, menuProductosFragment);
    }

    private void lauchMenuProductosFragment(Menu menu, MenuProductosFragment menuProductosFragment) {
        menuProductosFragment.setListener(menuProductosListener);
        getListaMenus().add(menu);

        manejadorFragments.cargarFragment(menuProductosFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        // cambiaFullScreen(View.GONE, generaListener(menuProductosFragment));
    }

    private void consumirServicioDatosNegocio() {
        //Cargar Datos Negocio
        MisDatosService misDatosService = new MisDatosService();
        misDatosService.getDatosNegocio(new RetrofitListener<DatosNegocio>() {
                                            @Override
                                            public void showMessage(String message) {
                                                //No implementation
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                LOGGER.throwing("DATOS NEGOCIO", 1, throwable, "Error al consultar los Datos Negocio");
                                            }

                                            @Override
                                            public void onSuccess(DatosNegocio datosNegocio) {
                                                MposApplication.getInstance().setDatosNegocio(datosNegocio);
                                            }
                                        }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());

    }


    private class AsyntaskFavorites extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            getFavourites();
            return true;
        }
    }

    public void getFavourites() {
        FavouritesServices service = new FavouritesServices();
        service.getFavoritos(new RetrofitListener<List<FavoritoBean>>() {
                                 @Override
                                 public void showMessage(String s) {/*No implementation*/ }

                                 @Override
                                 public void onFailure(Throwable throwable) {
                                     LOGGER.throwing("OBTENER FAVORITOS", 1, throwable, "Error al consultar los Favoritos");
                                     getRemindersForToday();
                                 }

                                 @Override
                                 public void onSuccess(List<FavoritoBean> favoritoBeans) {
                                     listaFavoritos = favoritoBeans;
                                     getRemindersForToday();
                                 }
                             }, MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                ApiData.APIDATA.getDatosSesion().getIdSesion());
    }

    public void getRemindersForToday() {
        FavouritesServices services = new FavouritesServices();
        services.getFechasFavoritos(new RetrofitListener<List<FavoritoBean>>() {
                                        @Override
                                        public void showMessage(String s) {
                                            //No implementation
                                        }

                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            LOGGER.throwing("OBTENER FAVORITOS", 1, throwable, "Error al consultar los Favoritos");
                                        }

                                        @Override
                                        public void onSuccess(List<FavoritoBean> favoritoBeans) {
                                            if (!reloadCalendarModule()) {
                                                showReminder(favoritoBeans);
                                            } else {
                                                goToCalendarFromProducts();
                                            }
                                        }
                                    }, fechaInicio, fechaFin, MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                ApiData.APIDATA.getDatosSesion().getIdSesion());
    }

    private void showReminder(List<FavoritoBean> favoritoBeans) {
        if (favoritoBeans != null && !favoritoBeans.isEmpty()) {
            for (FavoritoBean f : favoritoBeans) {
                if (f.getEstado().equals("PENDIENTE")) {
                    recordatoriosPendientes = true;
                }
            }//end for
            showPendingReminders();
        } else {
            showProgramsReminderDefault();
        }
    }

    private void showPendingReminders() {
        if (recordatoriosPendientes && !(preferenceManager.getValue(FECHA_RECORDATORIO_NO_PROGRAM, "").equals(fechaInicio))) {
            preferenceManager.setValue(FECHA_RECORDATORIO_NO_PROGRAM, fechaInicio);
            showDialogReminders(R.layout.dialog_reminders, getString(R.string.cancelar), "Pagar", R.drawable.ic_ana_bandera_amarilla,
                    "¡Tienes Pagos Pendientes!", "Evita Cualquier Tipo de Recargos", new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                        @Override
                        public void onCancel() {/*NO implementation*/}

                        @Override
                        public void onAccept() {
                            loadMyRemindersCalendar();
                        }
                    });
        } else {
            showProgramsReminderDefault();
        }
    }

    private Boolean reloadCalendarModule() {
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {
            isListServicesActivityRoot = parametros.getBoolean("isListServicesActiviyRoot");
            initNavBar();
            return isListServicesActivityRoot;
        } else {
            return false;
        }
    }

    private void showProgramsReminderDefault() {
        if (!(preferenceManager.getValue(FECHA_RECORDATORIO_NO_PROGRAM, "").equals(fechaInicio))) {
            preferenceManager.setValue(FECHA_RECORDATORIO_NO_PROGRAM, fechaInicio);
            showDialogReminders(R.layout.dialog_reminders, getString(R.string.cancelar), "Programar", R.drawable.ic_ana_calendar,
                    "Sin Pagos Programados Hoy", "¿Quieres Registrar Uno Nuevo?", new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                        @Override
                        public void onCancel() {/*NO implementation*/}

                        @Override
                        public void onAccept() {
                            loadMyRemindersCalendar();
                        }
                    });
        }
    }

    public List<FavoritoBean> getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(List<FavoritoBean> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    public void cargarFragmentSalir() {
        showDialog(R.layout.layout_cerrar_sesion, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
            @Override
            public void onCancel() {
                //No implementation
            }

            @Override
            public void onAccept() {
                HomeActivity.this.finishApp();
            }
        });
    }

    private void restauraMenu() {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(null);
        bottomNavigationViewEx.setSelectedItemId(R.id.nav_home);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(itemSelectedListener);
    }

    private void cargaFormulario(final NivelMenu nivelMenu, final Menu menu) {
        final List<Operaciones> operaciones = SigmaBdManager
                .getOperacionesVisiblesPorProducto(
                        menu.getProducto(),
                        new OnFailureListener.BasicOnFailureListener(
                                TAG,
                                "Error al consultar operaciones"
                        )
                );
        final RetrocesoListener retrocesoListener = nivelMenu1 -> regresaMenu();
        if (operaciones.isEmpty()) {
            despliegaModal(
                    true,
                    false,
                    getString(R.string.operacion),
                    getString(R.string.sin_operaciones),
                    this::restauraMenu
            );
        } else if (operaciones.size() == 1) {
            final AbstractFragment formularioFragment =
                    FormularioFragmentFactory.build(
                            nivelMenu,
                            menu,
                            operaciones.get(0),
                            SigmaBdManager.getFormulario(
                                    operaciones.get(0),
                                    new OnFailureListener.BasicOnFailureListener(
                                            TAG,
                                            "Error al obtener formulario"
                                    )
                            )
                    );
            formularioFragment.setRetrocesoListener(retrocesoListener);
            cambiaFormularioFullScreen(View.GONE, generaListener(formularioFragment));
        } else if (operaciones.size() > 1) {
            final DialogoSeleccionOperaciones dialogoSeleccionOperaciones =
                    new DialogoSeleccionOperaciones();
            dialogoSeleccionOperaciones.setOperaciones(operaciones, operacion -> {
                final AbstractFragment formularioFragment =
                        FormularioFragmentFactory.build(
                                nivelMenu,
                                menu,
                                operacion,
                                SigmaBdManager.getFormulario(
                                        operacion,
                                        new OnFailureListener.BasicOnFailureListener(
                                                TAG,
                                                "Error al obtener formulario"
                                        )
                                )
                        );
                formularioFragment.setRetrocesoListener(retrocesoListener);
                cambiaFormularioFullScreen(View.GONE, generaListener(formularioFragment));
            });
            dialogoSeleccionOperaciones.show(getSupportFragmentManager(), "seleccion_operacion");
        }
    }

    public String getStringSaldo() {
        final StringBuilder cadenaSaldo = new StringBuilder();

        for (final Map.Entry<String, BigDecimal> entry : saldos.entrySet()) {
            cadenaSaldo.append(getString(R.string.etiq_saldo_modal_entry, entry.getKey(), getNumberFormat().format(entry.getValue())));
        }
        return cadenaSaldo.toString();
    }

    public BigDecimal getSaldo() {
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    public void setSaldo(BigDecimal saldo) {
        runOnUiThread(() -> this.saldo = saldo);
    }

    public void getSaldoFromService() {
        final String tpvcod = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        TransactionService transactionService = new TransactionService();
        transactionService.getSaldo(ApiData.APIDATA.getDatosSesion().getIdSesion(), tpvcod, new RetrofitListener<SaldosResponseBean>() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(HomeActivity.this, "message error", Toast.LENGTH_SHORT).show();
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.operacion_pci), getString(R.string.error_tipoOperacion), () -> {
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(HomeActivity.this, "message error", Toast.LENGTH_SHORT).show();
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.operacion_pci), getString(R.string.error_tipoOperacion), () -> {
                });
            }

            @Override
            public void onSuccess(SaldosResponseBean result) {
                AdjustEvent adjustEvent = new AdjustEvent("bhpt8c");
                Adjust.trackEvent(adjustEvent);

                BigDecimal total = BigDecimal.ZERO;
                for (final SaldoBean decimal : result.getResponse()) {
                    total = total.add(decimal.getSaldo());
                }

                setAgenteSaldo(Utilities.getFormatedImportObject(total));
                setSaldo(total);
                ocultaProgressDialog();
            }
        });
    }


    private void validateOperation(final boolean mostrarDialogo) {
        if (mostrarDialogo) {
            showSaldo(false, getString(R.string.etiq_saldo_modal), new MontoViewController(HomeActivity.this, getStringSaldo(), "General", ContextCompat.getColor(getApplicationContext(), R.color.coloproducttext)), () -> {
                if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                    ejecutaOperacionSiguiente();
                }
            });
        } else {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                ejecutaOperacionSiguiente();
            }
        }
    }

    public void cambiaPantallaCompleta(final int visibilidad, final Transition.TransitionListener listener) {
        final AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
        autoTransition.addListener(listener);
        TransitionManager.beginDelayedTransition(binding.flMainPantallaCompleta, autoTransition);
        binding.container.setVisibility(visibilidad);
    }

    public void cambiaFullScreen(final int visibilidad, final Transition.TransitionListener listener) {
        final AutoTransition autoTransition = new AutoTransition();

        autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
        autoTransition.addListener(listener);
        TransitionManager.beginDelayedTransition(binding.flMainPantallaCompleta, autoTransition);

        binding.container.setVisibility(visibilidad);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }
    }

    public void cambiaFormularioFullScreen(final int visibilidad, final Transition.TransitionListener listener) {
        final AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
        autoTransition.addListener(listener);
        TransitionManager.beginDelayedTransition(binding.flMainPantallaCompleta, autoTransition);
        binding.container.setVisibility(visibilidad);
    }

    public Transition.TransitionListener generaListener(final AbstractFragment formularioFragment) {

        return new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(final Transition transition) {
                //No implementado
            }

            @Override
            public void onTransitionEnd(final Transition transition) {
                manejadorFragments.cargarFragment(formularioFragment, contenedor);
            }

            @Override
            public void onTransitionCancel(final Transition transition) {
//No implementado
            }

            @Override
            public void onTransitionPause(final Transition transition) {
//No implementado
            }

            @Override
            public void onTransitionResume(final Transition transition) {
//No implementado
            }
        };
    }

    private void ejecutaOperacionSiguiente() {
        if (ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion() != null) {
            TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                    ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                    .realizarOperacion();
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                muestraProgressDialog(getString(R.string.cargando));
                TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                        ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                        .realizarOperacion();
            }
        } else {
            cargaMenuCategorias();
        }
    }

    public void cargarFragmentCobrar() { //Fragmento Cobrar
        final ChargeFragment chargeFragment = ChargeFragment.newInstance();
        chargeFragment.setListener(this);

        manejadorFragments.cargarFragment(chargeFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }
    }

    public void cargarFragmentSettings() { // Fragmento Configuracion
        final SettingFragment settingFragment = SettingFragment.newInstance();
        settingFragment.setListener(this);
        //   cambiaFullScreen(View.GONE, generaListener(settingFragment));

        manejadorFragments.cargarFragment(settingFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }
    }

    public void cargarFragmentMiCuenta() {  //  Cargar Fragmento Mi Cuenta
        final ConfigMenuMiCuentaFragment fragmentMicuenta = new ConfigMenuMiCuentaFragment();
        fragmentMicuenta.setListener(this);

        manejadorFragments.cargarFragment(fragmentMicuenta, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

    }

    public void cargarComprarKit() {
        Intent intent = new Intent(this, KitSolicitarActivity.class);
        intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_COMPRA_KIT_BY_COBRAR);
        startActivityForResult(intent, 4);
    }

    public void cargarFragmentKitDatosEnvio() {
        final KitDatosEnvioFragment kitDatosEnvioFragment = new KitDatosEnvioFragment();

        manejadorFragments.cargarFragment(kitDatosEnvioFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        // cambiaFullScreen(View.GONE, generaListener(kitDatosEnvioFragment));
    }

    public void cargarFragmentPromociones() {
        final PromocionesFragment fragmentPromociones = new PromocionesFragment();

        manejadorFragments.cargarFragment(fragmentPromociones, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        //   cambiaFullScreen(View.GONE, generaListener(fragmentPromociones));
    }

    public void cargarFragmentDetalleMis(MisMovimientosDialog movimientosDialog) {
        manejadorFragments.cargarFragmentWithBackstack(movimientosDialog, contenedor);
    }

    public void cargarFragmentCuentaBancariaAndCards(ConfigMenuInfoBancariaAndCardsFragment infoBancariaFragment) {
        manejadorFragments.cargarFragmentWithBackstack(infoBancariaFragment, contenedor);
    }

    public void cargarFragmentCuentaBancaria(ConfigMenuInfoBancariaFragment infoBancariaFragment) {
        manejadorFragments.cargarFragmentWithBackstack(infoBancariaFragment, contenedor);
    }

    public void loadCalendarPresentation() {
        final CalendarPresentationFragment calendarFragment = new CalendarPresentationFragment();
        cambiaFormularioFullScreen(View.GONE, generaListener(calendarFragment));
    }

    public void loadMyRemindersCalendar() {
        final MyRemindersCalendarFragments myReminders = new MyRemindersCalendarFragments();
        myReminders.setHomeActivity(this);
        cambiaFormularioFullScreen(View.GONE, generaListener(myReminders));
    }

    public void loadQrDefault() {
        final DefaultQrFragment defaultQrFragment = new DefaultQrFragment();
        cambiaFormularioFullScreen(View.GONE, generaListener(defaultQrFragment));
    }

    public void loadMyMovients(final ConfigMenuReporteVentasFragment ReporteFragment) {
        cambiaFormularioFullScreen(View.GONE, generaListener(ReporteFragment));
    }

    public void loadMyReports(final MisReportesFragment misReportesFragment) {
//        misReportesFragment.setListener
        cambiaFormularioFullScreen(View.GONE, generaListener(misReportesFragment));
    }

    public void loadMyBusiness(final ConfigMenuMisDatosFragment configMenuMisDatosFragment) {
        configMenuMisDatosFragment.setListener(this);
        cambiaFormularioFullScreen(View.GONE, generaListener(configMenuMisDatosFragment));
    }

    public void cargarFragmentMoneyIn() {
        final MoneyInMenuFragment moneyInMenuFragment = new MoneyInMenuFragment();
        moneyInMenuFragment.setListener(this);

        manejadorFragments.cargarFragment(moneyInMenuFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        //   cambiaFullScreen(View.GONE, generaListener(moneyInMenuFragment));
    }

    public void cargarFragmentQr() {
        final CodigoQrFragment fragment = new CodigoQrFragment();
        fragment.setListener(this);

        manejadorFragments.cargarFragment(fragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        //cambiaFullScreen(View.GONE, generaListener(fragment));
    }

    public void cargarFragmentMoneyOut() {
        final MoneyOutFragment moneyOutFragment = new MoneyOutFragment();
        moneyOutFragment.setListener(this);

        manejadorFragments.cargarFragment(moneyOutFragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }


        //  cambiaFullScreen(View.GONE, generaListener(moneyOutFragment));
    }

    public void cargarFragmentSubVincular() {
        final ConfigMenuConfiguracionSubVincular fragmentSubvincular = new ConfigMenuConfiguracionSubVincular();
        fragmentSubvincular.setListener(this);
        fragmentSubvincular.setNombreTransaccion(ADQUIRENCIA);

        manejadorFragments.cargarFragment(fragmentSubvincular, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }

        // cambiaFullScreen(View.GONE, generaListener(fragmentSubvincular));
    }

    public void cargarFragments(final int visibilidad, final Transition.TransitionListener listener) {
        final AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
        autoTransition.addListener(listener);
        TransitionManager.beginDelayedTransition(binding.flMainPantallaCompleta, autoTransition);
//        binding.toolbar.setVisibility(visibilidad);
    }

    public void cargarFragmentsCuenta(final int visibilidad, final Transition.TransitionListener listener) {
        cargarFragments(visibilidad, listener);
    }

    //----------Override Methods-------------------------------------------------------
    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        final Fragment actualFragment = getSupportFragmentManager().findFragmentById(contenedor);
        if (actualFragment instanceof FormularioGenerico ||

                actualFragment instanceof EmailTicketFragment ||
                actualFragment instanceof CodigoQrFragment ||
                actualFragment instanceof ChargeFragment ||
                actualFragment instanceof KitDatosEnvioFragment) {
            regresaMenu();
        } else if (actualFragment instanceof ConfigMenuMiCuentaFragment ||
                actualFragment instanceof ConfigMenuAyudaFragment ||
                actualFragment instanceof ConfigMenuMisDatosFragment ||
                actualFragment instanceof ConfigMenuConfiguracionFragment ||
                actualFragment instanceof ConfigMenuLegalFragment ||
                actualFragment instanceof ConfigMenuSeguridadFragment ||
                actualFragment instanceof ConfigMenuConfiguracionSubVincular ||
                actualFragment instanceof ConfigMenuDongleVinculadoFragment ||
                actualFragment instanceof MoneyOutFragment) {
            //No handled
        } else if (actualFragment instanceof KitSolicitarFragment) {
            cargarFragmentKitDatosEnvio();
        } else if (actualFragment instanceof ConfigMenuInfoBancariaAndCardsFragment ||
                actualFragment instanceof ConfigMenuInfoBancariaFragment) {
            cargarFragmentCuentaBancariaAndCards(new ConfigMenuInfoBancariaAndCardsFragment());
        } else {
            cargarFragmentSalir();
        }
    }

    public void regresaMenu() {
        ocultaProgressDialog();
        TipoOperacion tipoOperacionNext = ApiData.APIDATA.getDatosOperacionSiguiente() != null ? ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion() : null;
        if (tipoOperacionNext != null && (tipoOperacionNext != TipoOperacion.ICONOS_LIST || tipoOperacionNext != TipoOperacion.LOGOS_LIST || tipoOperacionNext != TipoOperacion.INIT_LIST)) {
            ApiData.APIDATA.setDatosOperacionSiguiente(null);
        }

        cambiaFullScreen(View.VISIBLE, new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(final Transition transition) {
                restauraMenu();
            }

            @Override
            public void onTransitionEnd(final Transition transition) {
                //NO implementado
            }

            @Override
            public void onTransitionCancel(final Transition transition) {
                //NO implementado
            }

            @Override
            public void onTransitionPause(final Transition transition) {
                //NO implementado
            }

            @Override
            public void onTransitionResume(final Transition transition) {
                //NO implementado
            }
        });

        if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
            ejecutaOperacionSiguiente();
        } else {
            cargaMenuCategorias();
            getSaldoFromService();
        }
        restauraMenu();
    }


    public void onActualizarProductos() {
        detenerTemporizador();
        this.actualizarProductos(getResources().getString(R.string.actualizando));
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(INIT_LIST, abstractRespuesta -> {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                        ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                        .realizarOperacion();
            } else {
                runOnUiThread(() -> {
                    HomeActivity.this.ocultaProgressDialog();
                    LOGGER.fine(TAG, "ACTUALIZAR MENU");
                    restauraHome();
                });
            }
        }, throwable -> {
            HomeActivity.this.ocultaProgressDialog();
            LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
            despliegaModal(true, false, getString(R.string.error), throwable.getMessage(), null);
        });
        transaccion.realizarOperacion();
    }


    @Override
    protected void onDestroy() {
        unregisterReceivers();
        detenerTemporizador();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void unregisterReceivers() {
        if (broadcastManager != null) {
            try {
                unregisterReceiver(broadcastManager);
            } catch (Exception exe) {
                LOGGER.throwing(TAG, 1, exe, exe.getMessage());
            }
        }
    }

    private void setReceivers() {
        broadcastManager = new BroadcastManager(this, this);
        try {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(broadcastManager, filter);
        } catch (Exception ex) {
            LOGGER.throwing(TAG, 1, ex, "Error al ingresar los Brodcast");
            despliegaModal(true, false, getString(R.string.error), ex.getMessage(), null);
        }
    }

    @Override
    public void restauraHome() {
        final Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void cargarFragmentPremium() {
        final PremiumFragment fragment = new PremiumFragment();
        fragment.setListener(this);

        manejadorFragments.cargarFragment(fragment, contenedor);

        binding.container.setVisibility(View.GONE);

        if (binding.navBar.getVisibility() == View.GONE) {
            binding.navBar.setVisibility(View.VISIBLE);
        }
    }
}
