package com.pagatodoholdings.posandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.multidex.MultiDexApplication;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.BarcodeFormat;
import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.qposlib.SunmiPosManager;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.ApiLogger;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.manager.AppBuildManager;
import com.pagatodoholdings.posandroid.manager.AppConfigManager;
import com.pagatodoholdings.posandroid.manager.AppPreferenceManager;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosUsuarioRegistroCliente;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.StanProviderMock;
import com.pagatodoholdings.posandroid.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;

import io.fabric.sdk.android.Fabric;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DB_NAME;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.STAN;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIEMPO_INACTIVIDAD;
import static com.pagatodoholdings.posandroid.utils.Constantes.PREFERENCE_SETTINGS;
import static com.pagatodoholdings.posandroid.utils.Constantes.REGISTRO_EXTERNO;
import static com.pagatodoholdings.posandroid.utils.Constantes.TIEMPO_INACTIVIDAD_DEFAULT;

public class MposApplication extends MultiDexApplication {

    public static final String PREFERENCE_LOCAL = "PREFERENCE_LOCAL";
    private static final String TAG = MposApplication.class.getSimpleName();
    private static MposApplication mInstance;
    public static final long VERSION_REALM = 1;
    public static final Boolean MIGRATION = false;
    private TipoConfiguracion tipoConfiguracion;
    private BuildManager buildManager;
    private ConfigManager configManager;
    private PreferenceManager preferenceManager;
    private SharedPreferences localPref;
    private ApiData apiInstance;
    private DatosLogin datosLogin;
    private DatosNegocio datosNegocio;
    private Boolean isDebug = null;
    private Bitmap qrCode;
    private String menuFlag;
    private String menuTitle;
    private String androidId;
    private Boolean dailyDialogIsActive;
    private int tipodoc;

    public int getTipodoc() {
        return tipodoc;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }


    public void setTipodoc(int tipodoc) {
        this.tipodoc = tipodoc;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public void setDatosNegocio(final DatosNegocio datosNegocio) {
        this.datosNegocio = datosNegocio;
    }

    public String getMenuFlag() {
        return menuFlag;
    }

    public void setMenuFlag(String menuFlag) {
        this.menuFlag = menuFlag;
    }

    public DatosNegocio getDatosNegocio() {
        return datosNegocio;
    }

    public DatosLogin getDatosLogin() {
        return datosLogin;
    }

    public void setDatosLogin(DatosLogin datosLogin) {

        apiInstance.setIpServer(datosLogin.getPais().getIp());
        apiInstance.setPuerto(datosLogin.getPais().getPuerto());
        apiInstance.setPaisCode("0" + datosLogin.getPais().getCodigo());
        apiInstance.setNumSerie(DatosUsuarioRegistroCliente.getInstace().getSerie());
        this.datosLogin = datosLogin;
        if (datosLogin.getDatosTpv() != null && datosLogin.getDatosTpv().getQr() != null && !datosLogin.getDatosTpv().getQr().isEmpty()) {
            this.qrCode = Utilities.encodeAsBitmap(datosLogin.getDatosTpv().getQr(), BarcodeFormat.QR_CODE, 800, 800);
        }
    }

    public Boolean getDailyDialogIsActive() {
        return dailyDialogIsActive;
    }

    public void setDailyDialogIsActive(Boolean dailyDialogIsActive) {
        this.dailyDialogIsActive = dailyDialogIsActive;
    }

    private boolean isSunmiDevice;

    private boolean hasPrinter;
    private boolean pci = true;
    private int notisNoLeidas;

    public MposApplication() {
        //not override method
    }

    private static void initialize(final MposApplication mposApplication) {
        mInstance = mposApplication;

    }

    public static MposApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable throwable) {
                Log.e(TAG, "setDefaultUncaughtExceptionHandler: ", throwable);
                Crashlytics.logException(throwable);
            }
        });
        Fabric.with(this, new Crashlytics());
        initialize(this);
        ApiLogger.configureApiLoger(Logger.LOGGER);
        walletaddjust();
        initDriver();
        apiInstance = ApiData.APIDATA;
        buildManager = new AppBuildManager();
        preferenceManager = new AppPreferenceManager(this.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE));
        localPref = getSharedPreferences(PREFERENCE_LOCAL, Context.MODE_PRIVATE);
        configManager = new AppConfigManager();
        tipoConfiguracion = TipoConfiguracion.getInstance(REGISTRO_EXTERNO);
        setTiempoLogout();
        try {
            initApiInstance();
        } catch (IOException exe) {
            Logger.LOGGER.throwing(TAG, 1, exe, exe.getCause().toString());
        }
        setSunmiDevice(SunmiPosManager.isSunmiDevice());
    }

    public TipoConfiguracion getTipoConfiguracion() {
        return tipoConfiguracion;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BuildManager getBuildManager() {
        return buildManager;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public SharedPreferences getLocalPref() {
        return localPref;
    }

    private void initDriver() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        } catch (Exception exc) {
            Logger.LOGGER.throwing(TAG, 1, exc, "Error al Cargar el Driver");
        }
    }

    /**
     * QA Con Datos en DURO Descomentar Linea
     *
     * @return
     */
    public boolean isBuildDebug() {
        if (isDebug == null) {
            isDebug = "debug".equals(getConfigManager().getBuildType())
            //   || "qa".equals(getConfigManager().getBuildType()  )
            ;
        }
        return isDebug;
    }

    public AbstractDongle getPreferedDongle() {
        return PosInstance.getInstance().getDongle();
    }

    public boolean isHasPrinter() {
        return hasPrinter;
    }

    public void setHasPrinter(final boolean hasPrinter) {
        this.hasPrinter = hasPrinter;
    }

    public int getOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public int getRotation(final Context context) {
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_270:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            default:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
    }

    public boolean isPci() {
        return pci;
    }

    public void setPci(final boolean pci) {
        this.pci = pci;
    }

    public Boolean hasNotificaciones() {
        return notisNoLeidas > 0;
    }

    public void setNotisNoLeidas(final int notisNoLeidas) {
        this.notisNoLeidas = notisNoLeidas;
    }

    private void setTiempoLogout() {
        if (!preferenceManager.isExiste(TIEMPO_INACTIVIDAD)) {
            preferenceManager.setValue(TIEMPO_INACTIVIDAD, TIEMPO_INACTIVIDAD_DEFAULT);
        }
    }

    public void setSunmiDevice(boolean sunmiDevice) {
        isSunmiDevice = sunmiDevice;
    }

    public boolean isSunmiDevice() {
        return isSunmiDevice;
    }

    public Bitmap getQrCode() {
        return qrCode;
    }

    /**
     * Api Instance ..
     * Inicializacion privada de la Instancia de SigmaMannager
     */
    private void initApiInstance() throws IOException {
        apiInstance.setAppcontext(this);
        apiInstance.setAppID(BuildConfig.APPLICATION_ID);
        apiInstance.setIpServer(configManager.getIpServer());
        apiInstance.setNameRsa(configManager.getNameRsa());
        apiInstance.setNumSerie(tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));
        apiInstance.setSigmaPath(MposApplication.getInstance().getFilesDir().getCanonicalPath() + File.separator);
        apiInstance.setPaisCode(configManager.getPaisCode());
        apiInstance.setPuerto(configManager.getPuerto());
        apiInstance.setVersionBdApp(configManager.getVersionName());
        apiInstance.setClaveByte(configManager.getClaveHexLocal());
        apiInstance.setClaveBytePCI(configManager.getClaveHexPci());
        apiInstance.setNameRsaPCI(configManager.getNameRsaPci());
        apiInstance.setStan(preferenceManager.getValue(Constantes.Preferencia.STAN, "0"));
        apiInstance.setSigmaDBName(preferenceManager.getValue(DB_NAME, ".db"));
        apiInstance.setSigmaDBName(configManager.getVersionName());
        PosInstance.getInstance().setAppContext(this);
        PosInstance.getInstance().setColorTema(configManager.getColorAplication());
    }

    private void walletaddjust() {

        String appToken = BuildConfig.APP_TOKEN_ADJUST;
        String environment = "release".equals(BuildConfig.BUILD_TYPE) ? AdjustConfig.ENVIRONMENT_PRODUCTION : AdjustConfig.ENVIRONMENT_SANDBOX;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }


    //Adding Android Adjust Session

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            //Implementation
        }

        @Override
        public void onActivityStarted(Activity activity) {
            //No implementation
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {
            //No implementation
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            //No implementation
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //No implementation
        }

        //...
    }

    public static void saveStanToPreferenceManager() {
        final Long stan = new StanProviderMock().getNextStan();
        MposApplication.getInstance().getPreferenceManager().setValue(STAN, String.valueOf(stan));
    }

}
