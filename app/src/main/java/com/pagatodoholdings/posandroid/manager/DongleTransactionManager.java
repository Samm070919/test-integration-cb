package com.pagatodoholdings.posandroid.manager;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.google.gson.Gson;
import com.pagatodo.qposlib.QPosManager;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.dongleconnect.AplicacionEmv;
import com.pagatodo.qposlib.dongleconnect.DongleConnect;
import com.pagatodo.qposlib.dongleconnect.DongleListener;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodo.qposlib.dongleconnect.TransactionAmountData;
import com.pagatodo.qposlib.pos.PosResult;
import com.pagatodo.qposlib.pos.QposParameters;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.EmvManager;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.DecodeData;
import com.pagatodo.sigmalib.emv.PerfilEmvApp;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.reportetrx.TransaccionesBD;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.SigmaUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.Capabilities;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.PciLoginManager;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.ValidatePerfilEMV;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.Validationexe;
import com.pagatodoholdings.posandroid.utils.Logger;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.PerfilesEmv;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.data.DataOpTarjeta;
import net.fullcarga.android.api.data.respuesta.AbstractRespuesta;
import net.fullcarga.android.api.data.respuesta.Respuesta;
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno;
import net.fullcarga.android.api.oper.TipoOperacion;
import net.fullcarga.android.api.util.HexUtil;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.CANCELADO;
import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.ERROR_DISPOSITIVO;
import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.NO_CHIP;
import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.NO_CHIP_FALLBACK;
import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.TERMINADO;
import static com.pagatodo.qposlib.pos.PosResult.PosTransactionResult.TIMEOUT;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.getTipoOperacion;
import static net.fullcarga.android.api.constantes.Constantes.DEF_CHARSET;
import static net.fullcarga.android.api.oper.TipoOperacion.SOFTWARE_UPDATE;

public class DongleTransactionManager implements DongleListener, DongleConnect {

    private static final String TAG = DongleTransactionManager.class.getSimpleName();

    private static final String GOODS = "GOODS";
    private static final String INQUIRY = "INQUIRY";
    private static final String CASHBACK = "CASHBACK";

    private static final String ERROR_AL_CONSULTAR_EL_PERFIL = "Error al consultar el perfil";
    private List<String> fields = new ArrayList<>();
    private String intImporteBitmap = "";
    private String codPostal = null;

    private AbstractActivity activity;
    private AbstractDongle qposDongle;
    private Productos producto;
    protected Operaciones operacion;
    private TransactionAmountData mTransactionAmountData;
    private TransaccionesBD datosOperacion;

    private boolean fallbackActivado = false;
    private DecodeData decodeDataTarjeta;
    private Respuesta respuestaOperacion;
    private AdqTransactionCallback transactionCallback;
    private DataOpTarjeta dataOpTarjeta;
    private DoTradeResult entryMode;
    private QposParameters qposParameters;

    private DongleTransactionManager() {
        qposParameters = new QposParameters();
        qposParameters.setCtlsTransactionFloorLimitValue("00000000");
        qposParameters.setCtlsTransactionCvmLimitValue("000000080000");
        qposParameters.setCtlsTransactionLimitValue("000010000000");
    }

    public static DongleTransactionManager getInstance() {
        return new DongleTransactionManager();
    }

    public void setActivity(final AbstractActivity activity) {
        this.activity = activity;
    }

    public String setProducto(final Productos producto) {
        this.producto = producto;
        int tradeMode = 0;

        if (producto.getTarjetaEmvcl() == 1) {
            tradeMode |= QposParameters.MODE_NFC;
        }

        if (producto.getTarjetaEmv() == 1) {
            tradeMode |= QposParameters.MODE_ICC;
        }

        if (producto.getTarjetaBanda() == 1) {
            tradeMode |= QposParameters.MODE_MS;
        }

        qposParameters.setTradeMode(tradeMode);
        return getAppTradeMessage(tradeMode);
    }

    private String getAppTradeMessage(final int tradeMode) {
        if (tradeMode == (QposParameters.MODE_ICC)) {
            return "Inserta la Tarjeta";
        } else if (tradeMode == (QposParameters.MODE_MS)) {
            return "Desliza la Tarjeta";
        } else if (tradeMode == (QposParameters.MODE_NFC)) {
            return "Acerca la Tarjeta al Lector";
        } else {  // (MODE_NFC | MODE_MS) is not supported
            return "Desliza o Inserta la Tarjeta";
        }
    }

    public void setListener(final AdqTransactionCallback listener) {
        this.transactionCallback = listener;
    }

    public DecodeData getDecodeDataTarjeta() {
        return decodeDataTarjeta;
    }

    public void setCuotas(final int cuotas) {
        final DataOpTarjeta datosTarjeta = new DataOpTarjeta(
                dataOpTarjeta.getCryptPan(),
                dataOpTarjeta.getCryptTrack1(),
                dataOpTarjeta.getCryptTrack2(),
                dataOpTarjeta.getCryptTrack3(),
                dataOpTarjeta.getCryptTagsEmv(),
                dataOpTarjeta.getPinBlock(),
                dataOpTarjeta.getPosEntryMode(),
                dataOpTarjeta.getImporte(),
                dataOpTarjeta.getOtroImporte(),
                dataOpTarjeta.getCurrencyCode(),
                dataOpTarjeta.getDecimales(),
                dataOpTarjeta.getImporteOperacion(),
                dataOpTarjeta.getImporteCashback(),
                dataOpTarjeta.getImportePropina(),
                dataOpTarjeta.getImporteImpuestos(),
                dataOpTarjeta.getComision(),
                cuotas == 0 ? null : cuotas,
                null,
                null,
                null);
        doOperation(datosTarjeta);
    }

    public void initTransaction(final BigDecimal importe, final BigDecimal propina, final BigDecimal impuesto, final String referencia) {
        List<Operaciones> operaciones = SigmaBdManager.getOperacionesVisiblesPorProducto(producto.getCodigo(), new OnFailureListener.BasicOnFailureListener(TAG, "Error al consultar operaciones"));
        this.operacion = operaciones.get(0);
        if (PciLoginManager.isPciLoggedIn()) {
            qposDongle = MposApplication.getInstance().getPreferedDongle().getQpos(PosInterface.Tipodongle.DSPREAD);
            realizarTransaccion(importe, propina, impuesto, referencia);
        } else {
            transactionCallback.qposNoConectado();
        }
    }

    private void realizarTransaccion(final BigDecimal importe, final BigDecimal propina, final BigDecimal impuesto, final String referencia) {
        limpiandoVariables();
        if (MposApplication.getInstance().getPreferedDongle() == null) {
            transactionCallback.qposNoConectado();
            return;
        }
        qposDongle.setDongleListener(this);
        qposDongle.setDongleConnect(this);

        if (producto.getPerfilEmv() == null) {
            fields.add(referencia);
            if (producto.getTarjetaEmv() == 1 || producto.getTarjetaBanda() == 1) {
                operacionEMVTarjeta(0);
            } else {
                operacionEMVSinTarjeta(0);
            }
        } else {
            final NumberFormat numberFormat = SigmaBdManager.getNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, activity.getString(R.string.error_formato_monto)));
            fields.add(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(SigmaUtil.cleanImporteInput(String.valueOf(importe), numberFormat)));
//            fields.add(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(SigmaUtil.cleanImporteInput(String.valueOf(propina), numberFormat)));
//            fields.add(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(SigmaUtil.cleanImporteInput(String.valueOf(impuesto), numberFormat)));
            enviarImportes(producto.getPerfilEmv(), importe, BigDecimal.ZERO, propina, impuesto);
        }
    }

    private void enviarImportes(final int idPerfil, final BigDecimal inImporte, final BigDecimal inRetiroEfectivo, final BigDecimal inPropina, final BigDecimal inImpuesto) {//NOSONAR
        transactionCallback.procesandoTransaccion();
        final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(idPerfil, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));

        //Validacion de Impuesto pointY Costo
        BigDecimal inCosto = fullPerfil.getEmvCashBackMonedas() != null
                ? getCostoOperacion(idPerfil).add(BigDecimal.ZERO)
                : BigDecimal.ZERO;

        final BigDecimal monto1 = checkimportebitmap(idPerfil, inImporte, inRetiroEfectivo, inPropina, inImpuesto, inCosto);
        final BigDecimal monto2 = checkimportebitmap2(idPerfil, inImporte, inRetiroEfectivo, inPropina, inImpuesto, inCosto);

        mTransactionAmountData = new TransactionAmountData();
        mTransactionAmountData.setTransactionType(GOODS);
        mTransactionAmountData.setAmount(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(monto1.toPlainString())); // Todo: Throws a ArithmeticException at DatosTPV.java line 111
        mTransactionAmountData.setCashbackAmount(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(monto2.toPlainString()));
        mTransactionAmountData.setCurrencyCode(String.format(Locale.getDefault(), "%04d", Integer.parseInt(fullPerfil.getPerfilesEmv() != null ? String.valueOf(fullPerfil.getEmvMonedas().getCodigoMoneda()) : "0")));
        mTransactionAmountData.setDecimales(fullPerfil.getEmvMonedas().getDecimales());
        mTransactionAmountData.setImporteOperacion(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(inImporte.toPlainString()));
        mTransactionAmountData.setImporteCashback(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(inRetiroEfectivo.toPlainString()));
        mTransactionAmountData.setImportePropina(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(inPropina.toPlainString()));
        mTransactionAmountData.setImporteImpuestos(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(inImpuesto.toPlainString()));
        mTransactionAmountData.setComision(inCosto.compareTo(BigDecimal.ZERO) == 0 ? ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(inCosto.toPlainString()) : null);
        if (fullPerfil.getPerfilesEmv() == null) {
            mTransactionAmountData.setTransactionType(INQUIRY);
            mTransactionAmountData.setAmount("");
            mTransactionAmountData.setCashbackAmount("");
        } else if (fullPerfil.getPerfilesEmv().getInRetiroEfectivo() == 1 && monto2.compareTo(BigDecimal.ZERO) == 0.0) {
            fields.add(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(String.valueOf(inCosto)));
            mTransactionAmountData.setTransactionType(GOODS);
        } else {
            if (fullPerfil.getPerfilesEmv().getInRetiroEfectivo() == 1) {
                fields.add(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(String.valueOf(inCosto)));
                mTransactionAmountData.setTransactionType(CASHBACK);
            } else {
                mTransactionAmountData.setTransactionType(GOODS);
            }
        }

        mTransactionAmountData.setCodigoPostal(codPostal);
        mTransactionAmountData.setTipoOperacion(getTipoOperacion(operacion).getTipo());
        mTransactionAmountData.setAmountIcon(fullPerfil.getEmvMonedas() != null ? fullPerfil.getEmvMonedas().getNombre() : "");
        mTransactionAmountData.setCapacidades(listCaps(fullPerfil));
        qposDongle.doTransaccion(mTransactionAmountData, qposParameters);
    }

    private BigDecimal getCostoOperacion(final int perfil) {
        if (perfil != 0) {
            final PerfilEmvApp perfilEmv = EmvManager.getFullPerfil(perfil, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));
            return perfilEmv.getPerfilesEmv().getCosto() > 0.0 ? BigDecimal.valueOf(perfilEmv.getPerfilesEmv().getCosto()) : BigDecimal.ZERO;
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal checkimportebitmap(final int perfil, final BigDecimal in_Importe, final BigDecimal in_RetiroEfectivo, final BigDecimal in_Propina, final BigDecimal in_Impuesto, final BigDecimal in_Costo) {//NOSONAR

        final PerfilEmvApp perfilEmv = EmvManager.getFullPerfil(perfil, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));
        intImporteBitmap = printBinaryform(perfilEmv.getPerfilesEmv().getImporteBitmap());
        intImporteBitmap = String.format(Locale.getDefault(), "%05d", Integer.parseInt(intImporteBitmap.trim()));

        BigDecimal bufferDuble = BigDecimal.ZERO;
        for (int lugarBitmap = intImporteBitmap.length(); lugarBitmap > 0; lugarBitmap--) {
            switch (lugarBitmap) {
                case 5:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Importe);
                    }
                    break;
                case 4:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_RetiroEfectivo);
                    }
                    break;
                case 3:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Propina);
                    }
                    break;
                case 2:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Impuesto);
                    }
                    break;
                case 1:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Costo);
                    }
                    break;
                default:
                    bufferDuble = bufferDuble.add(BigDecimal.ZERO);
                    break;
            }
        }
        return bufferDuble;
    }

    private BigDecimal checkimportebitmap2(final int perfil, final BigDecimal in_Importe, final BigDecimal in_RetiroEfectivo, final BigDecimal in_Propina, final BigDecimal in_Impuesto, final BigDecimal in_Costo) {//NOSONAR

        final PerfilEmvApp perfilEmv = EmvManager.getFullPerfil(perfil, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));
        intImporteBitmap = printBinaryform(perfilEmv.getPerfilesEmv().getImporte2Bitmap());
        intImporteBitmap = String.format(Locale.getDefault(), "%05d", Integer.parseInt(intImporteBitmap.trim()));

        BigDecimal bufferDuble = BigDecimal.ZERO;
        for (int lugarBitmap = intImporteBitmap.length(); lugarBitmap > 0; lugarBitmap--) {
            switch (lugarBitmap) {
                case 5:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Importe);
                    }
                    break;
                case 4:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_RetiroEfectivo);
                    }
                    break;
                case 3:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Propina);
                    }
                    break;
                case 2:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Impuesto);
                    }
                    break;
                case 1:
                    if (intImporteBitmap.charAt(lugarBitmap - 1) == '1') {
                        bufferDuble = bufferDuble.add(in_Costo);
                    }
                    break;
                default:
                    bufferDuble = bufferDuble.add(BigDecimal.ZERO);
                    break;
            }
        }
        return bufferDuble;
    }

    private String printBinaryform(final int number) {
        return Integer.toBinaryString(number);
    }

    private void operacionEMVTarjeta(final int indicePerfilEmv) {
        final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(indicePerfilEmv, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));

        mTransactionAmountData = new TransactionAmountData();
        mTransactionAmountData.setCurrencyCode("0000");
        mTransactionAmountData.setTransactionType(INQUIRY);
        mTransactionAmountData.setAmount("");
        mTransactionAmountData.setCashbackAmount("");
        mTransactionAmountData.setDecimales(2);
        mTransactionAmountData.setTipoOperacion(getTipoOperacion(operacion).getTipo());
        mTransactionAmountData.setAmountIcon(fullPerfil.getEmvMonedas() != null ? fullPerfil.getEmvMonedas().getNombre() : "");
        qposDongle.doTransaccion(mTransactionAmountData, qposParameters);
    }

    private void operacionEMVSinTarjeta(final int indicePerfilEmv) {
        final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(indicePerfilEmv, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));

        mTransactionAmountData = new TransactionAmountData();
        mTransactionAmountData.setCurrencyCode("0000");
        mTransactionAmountData.setTransactionType(INQUIRY);
        mTransactionAmountData.setAmount("");
        mTransactionAmountData.setCashbackAmount("");
        mTransactionAmountData.setTipoOperacion(getTipoOperacion(operacion).getTipo());
        mTransactionAmountData.setAmountIcon(fullPerfil.getEmvMonedas() != null ? fullPerfil.getEmvMonedas().getNombre() : "");
        transactionCallback.procesandoTransaccion();

        datosOperacion = new TransaccionesBD();
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(getTipoOperacion(operacion), abstractRespuesta -> {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                operSiguiente();
            } else {
                if (abstractRespuesta.isCorrecta()) {
                    final RespuestaTrxCierreTurno restCierreTurno = (RespuestaTrxCierreTurno) abstractRespuesta;
                    datosOperacion.setReflocal(restCierreTurno.getCamposCierreTurno().getRefLocal());
                    datosOperacion.setImporte(restCierreTurno.getCamposCierreTurno().getImporte().toString());
                    datosOperacion.setRefcliente(restCierreTurno.getCamposCierreTurno().getRefCliente());

                    operationKindDifferentToD(abstractRespuesta);

                } else {
                    transactionCallback.errorDeOperacion(abstractRespuesta.getMsjError());
                    LOGGER.throwing(TAG, 1, new Throwable(), abstractRespuesta.getMsjError());
                }

            }
        }, throwable -> transactionCallback.errorDeOperacion(throwable.getMessage()));
        transaccion.withFields(fields);
        transaccion.withProcod(operacion.getProducto());
        transaccion.realizarOperacion();
        LOGGER.info(TAG, activity.getString(R.string.operacion_venta));
    }

    private void operationKindDifferentToD(AbstractRespuesta abstractRespuesta) {
        if (!mTransactionAmountData.getTipoOperacion().equals("D") && producto.getPerfilEmv() != null) {
            try {
                respuestaOperacion = abstractRespuesta;
                //new String(event.getResponseTrx().getCampoTagsEmv(), DEF_ENCODING).trim()
                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.APROBADO, activity.getString(R.string.operacion_finalizada), true));
            } catch (RuntimeException exe) {
                LOGGER.throwing(TAG, 1, exe, activity.getString(R.string.error_finaliza_operacion));
                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.UNKNOWN, activity.getString(R.string.error_finaliza_operacion), false));
            }
        } else {
            onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.APROBADO, activity.getString(R.string.operacion_finalizada), true));
        }
    }

    private DataOpTarjeta validateProfileNFC(DecodeData decodeData, PerfilEmvApp perfilesEmv) {
        Logger.LOGGER.info(TAG, " Operación por Comunicación de Campo Cercano");

        final DataOpTarjeta.PosEntryMode entryMode = DataOpTarjeta.PosEntryMode.CONTACLESS;

        if (perfilesEmv.getPerfilesEmv() != null) {
            try {
                ValidatePerfilEMV.validatefallback(perfilesEmv.getPerfilesEmv(), decodeData.getServiceCode(), fallbackActivado);
                ValidatePerfilEMV.validateDateOfExpiry(perfilesEmv.getPerfilesEmv(), decodeData.getExpiryDate());
//                ValidatePerfilEMV.validateBinCuotas(perfilesEmv.getPerfilesEmv(), decodeData.getMaskedPAN());
            } catch (final Validationexe exception) {
                Logger.LOGGER.throwing(TAG, 1, exception, "Error al Validar Tarjeta por NFC ");
                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.DECLINADO, exception.getMessage(), false));
                return null;
            }
        }

        final DataOpTarjeta dataOpTarjeta = getDataOpTarjeta(mTransactionAmountData, decodeData, entryMode, perfilesEmv);

        /*logger de campos enviados a API*/
        Logger.LOGGER.info(TAG, new Gson().toJson(dataOpTarjeta));

        fallbackActivado = false;

//        if (isRequieredPIN(decodeData,perfilesEmv)) {
//            return null;
//        }

        return dataOpTarjeta;
    }

    private DataOpTarjeta validateProfileMCR(final DecodeData decodeData, final PerfilEmvApp perfilesEmv) {

        Logger.LOGGER.info(TAG, " Operacion por Banda Magnetica");

        final DataOpTarjeta.PosEntryMode mEntryMode;
        final Productos mProducto = SigmaBdManager.getProducto(operacion, new OnFailureListener.BasicOnFailureListener(TAG, "Error Al Consultar la BD"));
        if (ValidatePerfilEMV.isChipcard(decodeData.getServiceCode()) && mProducto.getTarjetaEmv() == 1) {
            if (fallbackActivado) {
                mEntryMode = DataOpTarjeta.PosEntryMode.FALLBACK;
            } else {
                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.ERROR_DISPOSITIVO, "Ingrese la Tarjeta por el Chip o Utilice Otra Tarjeta", false));
                return null;
            }
        } else {
            mEntryMode = DataOpTarjeta.PosEntryMode.BANDA;
        }
        if (perfilesEmv.getPerfilesEmv() != null) {
            try {
                ValidatePerfilEMV.validatefallback(perfilesEmv.getPerfilesEmv(), decodeData.getServiceCode(), fallbackActivado);
                ValidatePerfilEMV.validateDateOfExpiry(perfilesEmv.getPerfilesEmv(), decodeData.getExpiryDate());
//                ValidatePerfilEMV.validateBinCuotas(perfilesEmv.getPerfilesEmv(), decodeData.getMaskedPAN());
            } catch (final Validationexe exception) {
                Logger.LOGGER.throwing(TAG, 1, exception, "Error al Validar Tarjeta por Banda ");
                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.DECLINADO, exception.getMessage(), false));
                return null;
            }
        }

        final DataOpTarjeta mDataOpTarjeta;

        mDataOpTarjeta = getDataOpTarjeta(mTransactionAmountData, decodeData, mEntryMode, perfilesEmv);

        /*logger de campos enviados a API*/
        Logger.LOGGER.info(TAG, new Gson().toJson(mDataOpTarjeta));

        fallbackActivado = false;

        if (isRequieredPIN(decodeData, perfilesEmv) && !checkDoTrade()) {
            return null;
        }

        return mDataOpTarjeta;
    }

    private DataOpTarjeta validateProfileICC(final DecodeData decodeData, final PerfilEmvApp perfilesEmv) {
        Logger.LOGGER.info(TAG, " Obteniendo información por Chip");

//        if (perfilesEmv.getPerfilesEmv() != null) {
//            try {
//                ValidatePerfilEMV.validateDateOfExpiry(perfilesEmv.getPerfilesEmv(), decodeData.getExpiryDate());
//            } catch (final Validationexe exception) {
//                Logger.LOGGER.throwing(TAG, 1, exception, "Error al Validar los datos de la tarjeta / Fecha ");
//                onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.DECLINADO, exception.getMessage(), false));
//                return null;
//            }
//            ValidatePerfilEMV.validateBinCuotas(perfilesEmv.getPerfilesEmv(), decodeData.getMaskedPAN());
//        }

        final DataOpTarjeta dataOpTarjeta = getDataOpTarjeta(mTransactionAmountData, decodeData, DataOpTarjeta.PosEntryMode.CHIP, perfilesEmv);
        Logger.LOGGER.info(TAG, new Gson().toJson(getDataOpTarjeta(mTransactionAmountData, decodeData, DataOpTarjeta.PosEntryMode.CHIP, perfilesEmv)));

        ValidatePerfilEMV.setFailedChip(false);
        fallbackActivado = false;

        if (isRequieredPINICC(decodeData, perfilesEmv)) {
            return null;
        }

        return dataOpTarjeta;
    }

    private DataOpTarjeta getDataOpTarjeta(final TransactionAmountData transactionAmountData, final DecodeData decodeData, @NonNull final DataOpTarjeta.PosEntryMode entryMode, final PerfilEmvApp perfilesEmv) { //NOSONAR
        Logger.LOGGER.info(TAG, new Gson().toJson(decodeData));
        DataOpTarjeta opTarjeta;
        if (!transactionAmountData.getTipoOperacion().equals("D") && perfilesEmv.getPerfilesEmv() != null) {

            opTarjeta = new DataOpTarjeta(
                    decodeData.getEncPAN() != null ? HexUtil.hex2byte(decodeData.getEncPAN(), Charset.defaultCharset()) : null,
                    decodeData.getEncTrack1() != null ? HexUtil.hex2byte(decodeData.getEncTrack1(), Charset.defaultCharset()) : null,
                    decodeData.getEncTrack2() != null ? HexUtil.hex2byte(decodeData.getEncTrack2(), Charset.defaultCharset()) : null,
                    decodeData.getEncTrack3() != null ? HexUtil.hex2byte(decodeData.getEncTrack3(), Charset.defaultCharset()) : null,
                    decodeData.getIccdata() != null ? HexUtil.hex2byte(decodeData.getIccdata(), Charset.defaultCharset()) : null,
                    decodeData.getPinBlock() != null ? HexUtil.hex2byte(decodeData.getPinBlock(), Charset.defaultCharset()) : null,
                    entryMode,
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getAmount()),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getCashbackAmount()),
                    transactionAmountData.getCurrencyCode(),
                    transactionAmountData.getDecimales(),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getImporteOperacion()),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getImporteCashback()),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getImportePropina()),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getImporteImpuestos()),
                    transactionAmountData.getComision() != null ? ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(transactionAmountData.getComision()) : null,
                    null,
                    null,
                    transactionAmountData.getCodigoPostal(),
                    null);

            if (perfilesEmv.getPerfilesEmv() == null) {
                qposDongle.resetQPOS();
            }
        } else {
            opTarjeta = new DataOpTarjeta(
                    null,
                    decodeData.getEncTrack1() != null ? HexUtil.hex2byte(decodeData.getEncTrack1(), Charset.defaultCharset()) : null,
                    decodeData.getEncTrack2() != null ? HexUtil.hex2byte(decodeData.getEncTrack2(), Charset.defaultCharset()) : null,
                    null,
                    null,
                    decodeData.getPinBlock() != null ? HexUtil.hex2byte(decodeData.getPinBlock(), Charset.defaultCharset()) : null,
                    entryMode,
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte("000"),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte("000"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            qposDongle.resetQPOS(); // Implementacion para habilitar la lectura del pos
        }

        Logger.LOGGER.info(TAG, new Gson().toJson(opTarjeta));
        return opTarjeta;
    }

    private boolean isRequieredPIN(final DecodeData decodeData, final PerfilEmvApp perfilesEmv) {

        if (perfilesEmv.getPerfilesEmv() == null) {

            if (operacion.getPin() == 1) {

                qposDongle.getPin(decodeData.getMaskedPAN());
                transactionCallback.showSolicitarNip();
                return true;
            }

        } else {

            if (ValidatePerfilEMV.validateNIPifnecessary(decodeData.getServiceCode())) {
                qposDongle.getPin(decodeData.getMaskedPAN());
                transactionCallback.showSolicitarNip();
                return true;
            }
        }

        return false;
    }

    private boolean isRequieredPINICC(final DecodeData decodeData, final PerfilEmvApp perfilesEmv) {

        if (perfilesEmv != null && perfilesEmv.getPerfilesEmv() == null && operacion.getPin() == 1) {
            qposDongle.getPin(decodeData.getMaskedPAN());
            transactionCallback.showSolicitarNip();
            return true;
        }

        return false;
    }


    private boolean validateFALLBACK(final PerfilesEmv perfilesEmv) {
        Logger.LOGGER.info(TAG, new Gson().toJson(perfilesEmv));
        if (perfilesEmv != null) {
            return perfilesEmv.getChkPermiteFallback() == 1;
        } else {
            return false;
        }
    }

    private void syncOperation() {
        transactionCallback.procesandoTransaccion();
        mTransactionAmountData.setTipoOperacion(TipoOperacion.PCI_SINCRONIZACION.getTipo());
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(TipoOperacion.PCI_SINCRONIZACION, abstractRespuesta -> {
            activity.despliegaModal(
                    true,
                    false,
                    "Error",
                    "Operación cancelada."
                    ,
                    null
            );
        }, throwable -> {
            activity.despliegaModal(
                    true,
                    false,
                    "Error",
                    throwable.getMessage()
                    ,
                    null
            );
        });
        transaccion.withDatosOpTarjeta(dataOpTarjeta);
        transaccion.withProcod(operacion.getProducto());
        transaccion.withFields(fields);
        transaccion.withStan(ApiData.APIDATA.getDatosSesion().getDatosTPV().getStanProvider().getUltimo());
        transaccion.realizarOperacion();
    }

    private Map<String, String> listCaps(final PerfilEmvApp perfilesEmv) {
        final Map<String, String> caps = new HashMap<>();
        caps.put(QPosManager.TERMINAL_CAPS, Capabilities.terminalCapabilitiesCode(perfilesEmv));
        caps.put(QPosManager.ADITIONAL_CAPS, Capabilities.additionalTerminalCapabilitiesCode());
        caps.put(QPosManager.CURRENCY_CODE, String.format(Locale.getDefault(), "%04d", Integer.parseInt(perfilesEmv.getPerfilesEmv() != null ? String.valueOf(perfilesEmv.getEmvMonedas().getCodigoMoneda()) : "0")));
        caps.put(QPosManager.CVM_LIMIT, Capabilities.getTerminalExecute(perfilesEmv));
        caps.put(QPosManager.COUNTRY_CODE, (MposApplication.getInstance().getDatosLogin().getPais().getCodigo().length() > 3
                ? MposApplication.getInstance().getDatosLogin().getPais().getCodigo()
                : "0" + MposApplication.getInstance().getDatosLogin().getPais().getCodigo()));
        return caps;
    }

    private void doOperation(final DataOpTarjeta dataOpTarjeta) {
        datosOperacion = new TransaccionesBD();
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(getTipoOperacion(operacion), abstractRespuesta -> {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                operSiguiente();
            } else {
                if (abstractRespuesta.isCorrecta()) {
                    final RespuestaTrxCierreTurno restCierreTurno = (RespuestaTrxCierreTurno) abstractRespuesta;
                    datosOperacion.setReflocal(restCierreTurno.getCamposCierreTurno().getRefLocal());
                    datosOperacion.setImporte(restCierreTurno.getCamposCierreTurno().getImporte().toString());
                    datosOperacion.setRefcliente(restCierreTurno.getCamposCierreTurno().getRefCliente());
                    datosOperacion.setMedioPago(2);
                    respuestaOperacion = abstractRespuesta;

                    entryModeIsICC(restCierreTurno);
                } else {
                    transactionCallback.errorDeOperacion(abstractRespuesta.getMsjError());
                }
            }
        }, throwable -> transactionCallback.errorDeOperacion(throwable.getMessage()));
        transaccion.withDatosOpTarjeta(dataOpTarjeta);
        transaccion.withFields(fields);
        transaccion.withProcod(operacion.getProducto());
        transaccion.realizarOperacion();
    }

    private void entryModeIsICC(RespuestaTrxCierreTurno restCierreTurno) {
        if (!mTransactionAmountData.getTipoOperacion().equals("D") && entryMode.equals(DoTradeResult.ICC) && producto.getPerfilEmv() != null) {

            String tag = new String(restCierreTurno.getCampoTagsEmv(), DEF_CHARSET).trim();

            boolean tagStars = tag.startsWith("8A02");
            if (!tagStars && tag.length() > 8) {
                final String tag8A = tag.substring(tag.length() - 8);
                final String tagResto = tag.substring(0, tag.length() - 8);
                tag = tag8A + tagResto;
                LOGGER.info(TAG, tag);

            } else if (respuestaOperacion.getError() == 61) {
                transactionCallback.errorDeOperacion(respuestaOperacion.getMsjError());
            }

            qposDongle.operacionFinalizada(tag);  //new String(event.getResponseTrx().getCampoTagsEmv(), DEF_ENCODING).trim()

        } else {
            onRespuestaDongle(new PosResult(PosResult.PosTransactionResult.APROBADO, activity.getString(R.string.operacion_finalizada), true));
        }
    }

    @Override
    public void onDeviceConnected() {
        //No implementation
    }

    @Override
    public void ondevicedisconnected() {
        transactionCallback.qposDesconectado();
    }

    @Override
    public void deviceOnTransaction() {
        transactionCallback.procesandoTransaccion();
    }

    @Override
    public void onRequestNoQposDetected() {
        transactionCallback.qposNoConectado();
    }

    @Override
    public void onSessionKeysObtenidas() {
        //No implementation
    }

    @Override
    public void onResultData(Hashtable<String, String> datosTarjeta, DoTradeResult entryMode) {
        transactionCallback.procesandoTransaccion();

        this.entryMode = entryMode;

        final PerfilEmvApp perfilEmv = EmvManager.getFullPerfil(producto.getPerfilEmv() != null ? producto.getPerfilEmv() : 0, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));

        decodeDataTarjeta = new DecodeData(datosTarjeta, qposDongle.getIccTags());

        if (entryMode == DoTradeResult.NFC_ONLINE) {
            dataOpTarjeta = validateProfileNFC(decodeDataTarjeta, perfilEmv);
        } else if (entryMode == DoTradeResult.ICC) {
            dataOpTarjeta = validateProfileICC(decodeDataTarjeta, perfilEmv);
        } else if (entryMode == DoTradeResult.MCR) {
            dataOpTarjeta = validateProfileMCR(decodeDataTarjeta, perfilEmv);
        }

        if (dataOpTarjeta == null) {
            return;
        }

        if (producto == null || producto.getPerfilEmv() == null) {
            doOperation(dataOpTarjeta);
            return;
        }

        boolean cuotasRequestedByBin = perfilEmv.getPerfilesEmv() != null &&
                ValidatePerfilEMV.validateBinCuotas(perfilEmv.getPerfilesEmv(),
                        decodeDataTarjeta.getMaskedPAN());

        if (perfilEmv.getPerfilesEmv() != null &&
                (perfilEmv.getPerfilesEmv().getChkDigitosPan() == 1
                        || perfilEmv.getPerfilesEmv().getInCodigoPostal() == 1
                        || perfilEmv.getPerfilesEmv().getInCvv() == 1
                        || cuotasRequestedByBin)
                && (isFirma(perfilEmv) ||
                (datosTarjeta.containsKey("pinBlock")
                        && !datosTarjeta.get("pinBlock").isEmpty()))) {
            transactionCallback.requestCuotas();
        } else {
            doOperation(dataOpTarjeta);
        }
    }

    @Override
    public void onRespuestaDongle(PosResult result) {
        final PerfilEmvApp perfilEmv;

        if (producto.getPerfilEmv() != null) {
            perfilEmv = EmvManager.getFullPerfil(producto.getPerfilEmv(), new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));
        } else {
            perfilEmv = EmvManager.getFullPerfil(0, new OnFailureListener.BasicOnFailureListener(TAG, ERROR_AL_CONSULTAR_EL_PERFIL));
        }

        if (result.getResponce().equals(TIMEOUT)) {
            transactionCallback.qposNoResponse();
            return;
        }

        if (result.getResponce().equals(CANCELADO)) {
            transactionCallback.operacionCancelada();
            return;
        }

        if (result.getResponce().equals(NO_CHIP) && validateFALLBACK(perfilEmv.getPerfilesEmv())) {
            activity.despliegaModal(true, false, activity.getString(R.string.operacion_finalizada) + result.getResponce().name(), result.getMessage(), () -> {
                qposDongle.doTransaccion(mTransactionAmountData, qposParameters);
                transactionCallback.procesandoTransaccion();
            });
            fallbackActivado = true;
            return;
        }
        if (result.getResponce().equals(NO_CHIP_FALLBACK) || result.getResponce().equals(ERROR_DISPOSITIVO)) {
            activity.despliegaModal(true, false, activity.getString(R.string.operacion_finalizada) + result.getResponce().name(), result.getMessage(), () -> qposDongle.doTransaccion(mTransactionAmountData, qposParameters));
            return;
        }

        if (result.getResponce().equals(TERMINADO) && decodeDataTarjeta != null) {
            syncOperation();
            return;
        }

        if (result.isCorrect() && decodeDataTarjeta != null) {

            //Adjust Event
            AdjustEvent adjustEvent = new AdjustEvent("lusr63");
            Adjust.trackEvent(adjustEvent);

            boolean isfirma = isFirma(perfilEmv);

            transactionCallback.onSucces(isfirma, respuestaOperacion, decodeDataTarjeta);
        } else {
            transactionCallback.errorDeOperacion(result.getMessage());
        }
    }

    private boolean isFirma(PerfilEmvApp perfilEmv) {
        if (perfilEmv.getPerfilesEmv() != null) {
            return perfilEmv.getPerfilesEmv().getCvmFirma() == 1 && decodeDataTarjeta.getPinBlock().isEmpty();
        } else {
            return operacion.getPin() == 1 && decodeDataTarjeta.getPinBlock().isEmpty();
        }
    }

    @Override
    public void seleccionEmvApp(List<String> listEMVApps, AplicacionEmv aplicacionEmv) {
        //No implementation
    }

    @Override
    public void onSearchMifareCardResult(Hashtable<String, String> hashtable) {
        //No implementation
    }

    @Override
    public boolean checkDoTrade() {
        return operacion.getProducto().equalsIgnoreCase("PROGUBCS")
                || operacion.getProducto().equalsIgnoreCase("ADQGEN01");
    }

    @Override
    public void onBatchReadMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {
        //No implementation
    }

    @Override
    public void onBatchWriteMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {
        //No implementation
    }

    @Override
    public void onVerifyMifareCardResult(boolean isFailedChip) {
        //No implementation
    }

    @Override
    public void onOperateMifareCardResult(Hashtable<String, String> hashtable) {
        //No implementation
    }

    @Override
    public void onErrorWriteMifareCard() {
        //No implementation
    }

    @Override
    public void onFinishMifareCardResult(boolean finish) {
        //No implementation
    }

    private void operSiguiente() {
        if (!ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion().equals(SOFTWARE_UPDATE)) {
            TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                    ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                    .realizarOperacion();
        }
    }

    private void limpiandoVariables() {
        fields = new ArrayList<>();
        mTransactionAmountData = null;
        datosOperacion = null;
    }

    public void releaseTransaction() {
        if (qposDongle != null)
            qposDongle.resetQPOS();
    }
}