package com.pagatodoholdings.posandroid.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.reportetrx.BDManager;
import com.pagatodo.sigmalib.reportetrx.TransaccionesBD;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.enums.MediosPago;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.bd.sigma.manager.BdSigmaManager;
import net.fullcarga.android.api.data.DatosConexion;
import net.fullcarga.android.api.data.DatosTPV;
import net.fullcarga.android.api.data.VersionProtocolo;
import net.fullcarga.android.api.oper.TipoOperacion;
import net.fullcarga.android.api.sesion.DatosSesion;
import net.fullcarga.android.api.sesion.SessionFactory;
import net.fullcarga.android.api.util.HexUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.STAN;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TURNO;
import static com.pagatodoholdings.posandroid.MposApplication.saveStanToPreferenceManager;
import static com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_X;
import static com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_Z;
import static com.pagatodoholdings.posandroid.utils.Constantes.DEVOLUCION;
import static com.pagatodoholdings.posandroid.utils.Constantes.PREFIJOIMAGEN;
import static com.pagatodoholdings.posandroid.utils.Constantes.VENTA;

public final class Utilities {
    private static final String TAG = Utilities.class.getSimpleName();
    private static final String ANALYTICS_SEPARADOR = "_";

    //Localization
    private static final int GPS_REQUEST = 1111;
    private static Location location;

    public static int getAttributeReference(final int attr) {
        final TypedArray typedArray = MposApplication.getInstance().obtainStyledAttributes(
                Constantes.COLOR_APP_AZUL.equals(MposApplication.getInstance().getConfigManager().getColorAplication()) ? R.style.AppThemeBlue : R.style.AppThemeRed,
                new int[]{attr});
        return typedArray.getResourceId(0, 1);
    }

    public static Drawable getIcono(final String pathIcono) {
        Drawable icono = ResourcesCompat.getDrawable(MposApplication.getInstance().getResources(), getAttributeReference(R.attr.fullcarga), null);
        if (!("").equals(pathIcono)) {
            icono = Drawable.createFromPath(StorageUtil.getStoragePath() + pathIcono);
        }
        return icono;
    }

    public static void analyticsLogEventVenta(final Activity activity, final FirebaseAnalytics firebaseAnalytics) {
        if (activity instanceof HomeActivity) {
            final List<Menu> menus = ((HomeActivity) activity).getListaMenus();
            if (menus != null) {
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Event.EVENT_VENTA);
                for (final Menu menu : menus) {
                    stringBuilder.append(ANALYTICS_SEPARADOR).append(menu.getTexto());
                    if (menus.indexOf(menu) == 0) {
                        firebaseAnalytics.logEvent(stringBuilder.toString(), null);
                    }
                }
                firebaseAnalytics.logEvent(stringBuilder.toString(), null);
            }
        }
    }

    public static String obtenerMensajeError(Throwable throwable) {
        try {
            JSONObject jsonObject = new JSONObject(throwable.getMessage());
            String message = jsonObject.getString("message");
            return message.isEmpty()? jsonObject.getString("error") : message;
        } catch (JSONException e) {
            Logger.LOGGER.throwing("Mensaje error", 1, throwable, "JSONException: " + throwable.getMessage());
            return throwable.getClass().getSimpleName();
        }
    }

    public static void hideSoftKeyboard(final Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(final Activity activity, View view) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean takeScreenshot(final View view) {
        Date now = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss");
        String timeStamp = hourdateFormat.format(now);
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            File sharefile = new File(getGalleryPath() + "toshare_" + timeStamp + ".png");
            try (FileOutputStream out = new FileOutputStream(sharefile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
            } catch (IOException e) {
                Logger.LOGGER.info(TAG, String.valueOf(e.getMessage()));
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Captura"); //this.getString(R.string.picture_title));
            values.put(MediaStore.Images.Media.DESCRIPTION, "Imagen Guardada"); //this.getString(R.string.picture_description));
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, sharefile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, sharefile.getName());
            values.put("_data", sharefile.getAbsolutePath());

            ContentResolver cr =
                    MposApplication.getInstance().getApplicationContext().
                            getContentResolver();

            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//            Uri imageUri = FileProvider.getUriForFile(
//                    view.getContext(),
//                    "com.pagatodoholdings.posandroid.provider",
//                    sharefile);
//            Intent share = new Intent(android.content.Intent.ACTION_SEND);
//            share.setType("image/*");
//            share.putExtra(Intent.EXTRA_STREAM, imageUri);
//
//            view.getContext().startActivity(Intent.createChooser(share,
//                    "Share This Image with"));

            return true;

        } catch (Exception exception) {
            // Several error may come out with file handling or DOM
            Logger.LOGGER.throwing(TAG, 1, exception, exception.getMessage());
            return false;
        }
    }

    private static String getGalleryPath() {
        return "." + Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/";
    }

    public static boolean isGpsEnabled(final Activity activity) {
        int gpsSignal;
        try {
            gpsSignal = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsSignal == 0) {
                return false;
            }
        } catch (Settings.SettingNotFoundException exec) {
            Logger.LOGGER.throwing(TAG, 1, exec, exec.getMessage());
            return false;
        }
        return true;
    }

    public static Location obtenerUbicacion(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        LocationListener mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //No implementation
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //No implementation
            }

            @Override
            public void onProviderEnabled(String provider) {
                //No implementation
            }

            @Override
            public void onProviderDisabled(String provider) {
                //No implementation
            }
        };

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            habilitarUbicacion(activity);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mlocListener);
            //Existe GPS_PROVIDER obtiene ubicación
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) { //Trata con NETWORK_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, mlocListener);
                //Existe NETWORK_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) { //Trata con PASSIVE_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 0, mlocListener);
                //Existe PASSIVE_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }


        return location;
    }

    public static void habilitarUbicacion(final Activity activity) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, GPS_REQUEST);
    }

    public static String getStringFromRawFile(final int rawFileId) {
        Resources resources = MposApplication.getInstance().getResources();
        InputStream inputStream = resources.openRawResource(rawFileId);
        String myText = convertStreamToString(inputStream);
        try {
            inputStream.close();
            return myText;
        } catch (IOException exception) {
            Logger.LOGGER.throwing(TAG, 1, exception, exception.getMessage());
            return "";
        }

    }

    public static String convertStreamToString(final InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int res = 0;
        try {
            res = inputStream.read();

            while (res != -1) {
                baos.write(res);
                res = inputStream.read();
            }
            return baos.toString();

        } catch (IOException exception) {
            Logger.LOGGER.throwing(TAG, 1, exception, exception.getMessage());
            return "";
        }
    }

    public static Bitmap encodeAsBitmap(final String contents, final BarcodeFormat format, final int width, final int height) {
        if (contents.isEmpty() || format == null) {
            return null;
        }

        try {
            Map<EncodeHintType, Object> hints = null;
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            final MultiFormatWriter writer = new MultiFormatWriter();
            final BitMatrix bitMatrix = writer.encode(contents, format, width, height, hints);
            final Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitMatrix.getWidth(); i++) {
                for (int j = 0; j < bitMatrix.getHeight(); j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            java.util.logging.Logger.getLogger(e.getMessage());
            return null;
        }
    }

    public static Bitmap getProfilePic() {
        final File imgFile = new File(StorageUtil.getStoragePath() + PREFIJOIMAGEN);
        final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        return adjustImage(myBitmap, StorageUtil.getStoragePath() + PREFIJOIMAGEN);
    }

    public static Bitmap adjustImage(Bitmap myBitmap, String profileImagePath) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(profileImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
        } catch (Exception e) {
            return myBitmap;
        }
        return Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
    }

    public static TipoOperacion getTipoOperacion(final Operaciones operacion) {
        switch (operacion.getOperacion()) {
            case VENTA:
                return TipoOperacion.PCI_VENTA;
            case CONSULTA_X:
                return TipoOperacion.PCI_CONSULTA_X;
            case CONSULTA_Z:
                return TipoOperacion.PCI_CONSULTA_Z;
            case DEVOLUCION:
                return TipoOperacion.PCI_DEVOLUCION;
            default:
                return null;
        }
    }

    public static boolean isNumeric(@NotNull String montoServicio) {
        return false;
    }

    public enum TipoUsuario {
        SHOP("SHOPPER"),
        PROF("PROFESIONISTA"),
        MERC("MERCHANT");


        private String tipo;

        TipoUsuario(String tipo) {
            this.tipo = tipo;
        }

        public String getTipo() {
            return tipo;
        }
    }


    public static List<Menu> getItemsDeNivel(
            final Menu menu,
            final NivelMenu nivel,
            final OnFailureListener onFailureListener
    ) {
        if (menu.getNivel() != null) {
            return SigmaBdManager.getItemsPorNivel(nivel, menu, onFailureListener);
        } else {
            return Collections.emptyList();
        }
    }

    public static String getFormatedImport(final BigDecimal sImport) {
        final NumberFormat dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener numberFormat"));
        final String parametroFijo = SigmaBdManager.getParametroFijo("0024", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroFijo"));
        final String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroMoneda"));
        final String posicionMoneda = SigmaBdManager.getParametroFijo("0022", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener posicionMoneda"));


        String sMoneda = !parametroMoneda.trim().equals("") ?
                parametroMoneda :
                Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getCurrency();

        String sFormatedImport = dbnumberformat.format(sImport);
        String sCents = sFormatedImport.indexOf(parametroFijo) != -1 ? sFormatedImport.substring(sFormatedImport.indexOf(parametroFijo)) : "00";
        if (dbnumberformat.getMaximumFractionDigits() == 0) {
            sCents = "";
        }
        String sInteger = sFormatedImport.indexOf(parametroFijo) != -1 ? sFormatedImport.replace(sCents, "") : sFormatedImport;

        String sFinalImport = sInteger + sCents;


        if ("0".equals(posicionMoneda)) {
            sFinalImport = sMoneda + sFinalImport;
        } else {
            sFinalImport = sFinalImport + " " + sMoneda;
        }

        return sFinalImport;
    }

    public static CurrencyImport getFormatedImportObject(final BigDecimal sImport) {
        final NumberFormat dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener numberFormat"));
        final String parametroDecimales = SigmaBdManager.getParametroFijo("0024", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroFijo"));
        final String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroMoneda"));
        final String posicionMoneda = SigmaBdManager.getParametroFijo("0022", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener posicionMoneda"));


        String sMoneda = !parametroMoneda.trim().equals("") ?
                parametroMoneda :
                Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getCurrency();

        String sFormatedImport = dbnumberformat.format(sImport);
        String sCents = sFormatedImport.indexOf(parametroDecimales) != -1 ? sFormatedImport.substring(sFormatedImport.indexOf(parametroDecimales)) : "00";
        if (dbnumberformat.getMaximumFractionDigits() == 0) {
            sCents = "";
        }
        String sInteger = sFormatedImport.indexOf(parametroDecimales) != -1 ? sFormatedImport.replace(sCents, "") : sFormatedImport;

        CurrencyImport currencyImport = new CurrencyImport(
                sInteger,
                sCents,
                sMoneda,
                posicionMoneda
        );

        return currencyImport;
    }

    public static boolean guardarSigmaTransacciones(final TransaccionesBD transacciones, final Operaciones operaciones, final int tipoTarjeta) {
        final Productos obtenerElProducto = SigmaBdManager.getProducto(operaciones, new OnFailureListener.BasicOnFailureListener(TAG, "Error al Obtener el Producto"));
        transacciones.setFecha(new Date().getTime());
        transacciones.setCodigooper(operaciones.getOperacion());
        transacciones.setProcod(operaciones.getProducto());
        transacciones.setDescmenu(operaciones.getVisibleMenu().toString());
        transacciones.setMoneda(SigmaBdManager.getLiteralMoneda(new OnFailureListener.BasicOnFailureListener(TAG, "Error al recibir el tipo de moneda")));
        transacciones.setStan(Integer.valueOf(MposApplication.getInstance().getPreferenceManager().getValue(STAN, "0")));
        transacciones.setTipoTarjeta(tipoTarjeta);
        transacciones.setTurno(MposApplication.getInstance().getPreferenceManager().getValue(TURNO));
        transacciones.setContabilizar(obtenerElProducto.getCierreTurno());
        transacciones.setZincronizada(0);
        saveStanToPreferenceManager();

        return BDManager.insertTransaccion(MposApplication.getInstance().getContentResolver(), transacciones);
    }

    public static boolean guardarSigmaTransacciones(String importe, String ref, String refCte) {
        final TransaccionesBD transacciones = new TransaccionesBD();
        transacciones.setFecha(new Date().getTime());
        transacciones.setImporte(importe);
        transacciones.setReflocal(ref);
        transacciones.setRefcliente(refCte);
        transacciones.setMoneda(SigmaBdManager.getLiteralMoneda(new OnFailureListener.BasicOnFailureListener(TAG, "Error al recibir el tipo de moneda")));
        transacciones.setStan(Integer.valueOf(MposApplication.getInstance().getPreferenceManager().getValue(STAN, "0")));
        transacciones.setDescproducto("Pago con QR");
        transacciones.setTipoTarjeta(1);
        transacciones.setMedioPago(MediosPago.EFECTIVO);
        transacciones.setTurno(MposApplication.getInstance().getPreferenceManager().getValue(TURNO));
        transacciones.setContabilizar(0);
        transacciones.setZincronizada(0);
        saveStanToPreferenceManager();
        return BDManager.insertTransaccion(MposApplication.getInstance().getContentResolver(), transacciones);
    }

    public static boolean guardarSigmaTransacciones(String importe, String ref, String refCte, String refRem, String procod, String descProducto) {
        final TransaccionesBD transacciones = new TransaccionesBD();
        transacciones.setFecha(new Date().getTime());
        transacciones.setImporte(importe);
        transacciones.setReflocal(ref);
        transacciones.setRefcliente(refCte);
        transacciones.setRefremota(refRem);
        transacciones.setMoneda(SigmaBdManager.getLiteralMoneda(new OnFailureListener.BasicOnFailureListener(TAG, "Error al recibir el tipo de moneda")));
        transacciones.setStan(Integer.valueOf(MposApplication.getInstance().getPreferenceManager().getValue(STAN, "0")));
        transacciones.setProcod(procod);
        transacciones.setDescproducto(descProducto);
        transacciones.setMedioPago(MediosPago.OTRO);
        transacciones.setTurno(MposApplication.getInstance().getPreferenceManager().getValue(TURNO));
        transacciones.setContabilizar(0);
        transacciones.setZincronizada(0);
        saveStanToPreferenceManager();
        return BDManager.insertTransaccion(MposApplication.getInstance().getContentResolver(), transacciones);
    }


    public static DatosSesion build() throws SQLException {
        if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())) {
            final BdSigmaManager bdSigmaManager = StorageUtil.crearConexionSigmaManager();
            return SessionFactory.crearSesionLocal(
                    bdSigmaManager.crearDatosConexionLocalTrx(),
                    bdSigmaManager.crearDatosConexionLocalDonwload(),
                    bdSigmaManager.crearDatosTPV(ApiData.APIDATA.getNumSerie(), "1", ApiData.APIDATA.getVersionBdApp(), new StanProviderMock(), VersionProtocolo.A01_MPOS),
                    MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvpasswordip(),
                    HexUtil.hex2byte(HexUtil.hexStringFromBytes(ApiData.APIDATA.getClaveByte()), net.fullcarga.android.api.constantes.Constantes.DEF_CHARSET),
                    ApiData.APIDATA.getNameRsa());
        } else {
            PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
            String numSerie = null;
            if (preferenceManager.isExiste(NUMERO_SERIE)) { //Obtener el número de serie de las preferencias si existe
                numSerie = preferenceManager.getValue(NUMERO_SERIE, "");
            } else {
                numSerie = ApiData.APIDATA.getNumSerie();
            }
            return SessionFactory.crearSesionLocal(
                    new DatosConexion(ApiData.APIDATA.getIpServer(), ApiData.APIDATA.getPuerto(), 5000, 60000),
                    new DatosConexion(ApiData.APIDATA.getIpServer(), ApiData.APIDATA.getPuerto(), 5000, 60000),
                    new DatosTPV(MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                            Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getNumDecimales(),
                            new StanProviderMock(), numSerie, ApiData.APIDATA.getPaisCode(), VersionProtocolo.A01_MPOS, "", "", ApiData.APIDATA.getVersionBdApp()),
                    MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvpasswordip(),
                    HexUtil.hex2byte(HexUtil.hexStringFromBytes(ApiData.APIDATA.getClaveByte()), net.fullcarga.android.api.constantes.Constantes.DEF_CHARSET),
                    ApiData.APIDATA.getNameRsa()
            );
        }
    }


    public static boolean isNumeric(char caracter) {
        try {
            Integer.parseInt(String.valueOf(caracter));
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean validateInputFields(final TextInputLayout etCampo) {
        final CharSequence textoValidar = etCampo.getEditText().getText();
        CharSequence newTextValidar = "";
        if (textoValidar.toString().startsWith(" ")) {
            newTextValidar = textoValidar.toString().trim();
        } else {
            newTextValidar = textoValidar;
        }

        final boolean isValid = !TextUtils.isEmpty(newTextValidar);
        if (!isValid) {
            etCampo.setError(etCampo.getContext().getString(R.string.campo_requerido));
            etCampo.requestFocus();
        } else {
            etCampo.setError(null);
        }
        return isValid;
    }


}