package com.pagatodoholdings.posandroid.secciones.ticket;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.util.Base64;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.DecodeData;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.ticket.TicketGenerator;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsDate;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.data.respuesta.CamposTicket;
import net.fullcarga.android.api.data.respuesta.Respuesta;
import net.fullcarga.android.api.data.respuesta.RespuestaTrx;
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno;
import net.fullcarga.android.api.util.HexUtil;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Response;

public class ETicket {
    private static final String TAG = ETicket.class.getSimpleName();
    private static final String STRING_CURRENCY = "STRING_CURRENCY";
    private static final String STRING_IMPORT = "STRING_IMPORT";
    private static final String STRING_CENTS = "STRING_CENTS";
    private static final String STRING_DATE = "STRING_DATE";
    private static final String STRING_TICKET = "STRING_TICKET";
    private static final String STRING_SIGN_IMAGE64 = "STRING_SIGN_IMAGE64";
    private static final String STRING_CONTACT_PHONE = "STRING_CONTACT_PHONE";
    private static final String STRING_CONTACT_EMAIL = "STRING_CONTACT_EMAIL";
    private static final String STRING_VERSION_NAME = "STRING_VERSION_NAME";
    private static final String STRING_FULLCARGA_LABEL = "STRING_FULLCARGA_LABEL";
    private static final String STRING_YEAR = "STRING_YEAR";
    private static final String STRING_DESTINATARIO = "STRING_DESTINATARIO";
    private static final String STRING_REFERENCIA = "STRING_REFERENCIA";
    private static final String STRING_MONTO = "STRING_MONTO";
    private static final String STRING_LATITUD = "STRING_LATITUD";
    private static final String STRING_LONGITUD = "STRING_LONGITUD";
    private static final String STRING_API_KEY = "STRING_API_KEY";
    private static final String DATE_FORMAT = "dd/MMM/yyyy hh:mm aa";
    private static final String STRING_DNI_CODE = "STRING_DNI_CODE";
    //-----Inst ----------------------------------------------------------
    protected Boolean requiereFirma;
    protected byte[] imageBites;
    protected String imageSignString64;
    protected Menu menu;
    private Operaciones operacion;
    private Respuesta respuesta;
    protected PreferenceManager preferenceManager;
    private Resources resources;
    protected String emailEnvio;
    protected String sDestinatario;
    protected String sReferencia;
    protected String sMonto;
    protected String sDate;
    protected String contactNumber;
    protected String contactEmail;
    protected String dni;

    private enum TIPO_TICKET {
        OPERACION,
        QR
    }

    private TIPO_TICKET tipoTicket;

    private EnvioMailInterfece envioListener;

    //----- Var ----------------------------------------------------------
    private CharSequence ticket;
    private DecodeData camposEMVData;


    // PDS/TAE
    public ETicket(final Operaciones operacion, final Respuesta respuesta, final Boolean requiereFirma, final String emailEnvio, final EnvioMailInterfece envioListener) {
        this.requiereFirma = requiereFirma;
        this.operacion = operacion;
        this.respuesta = respuesta;
        this.resources = MposApplication.getInstance().getResources();
        this.envioListener = envioListener;
        this.emailEnvio = emailEnvio;
        this.tipoTicket = TIPO_TICKET.OPERACION;
    }

    // PCI/ADQ
    public ETicket(final Operaciones operacion, final Respuesta respuesta, final String imageSignString64, final DecodeData emvData, final String emailEnvio, final String dni, final EnvioMailInterfece envioListener) {
        this.operacion = operacion;
        this.respuesta = respuesta;
        this.camposEMVData = emvData;
        this.resources = MposApplication.getInstance().getResources();
        this.emailEnvio = emailEnvio;
        this.envioListener = envioListener;
        this.tipoTicket = TIPO_TICKET.OPERACION;
        this.imageSignString64 = imageSignString64;
        this.dni = dni;
    }

    // Instancia para Ticket QR
    public ETicket(final String sDestinatario, final String sReferencia, final String sMonto, final String sDate, final String emailEnvio, final EnvioMailInterfece envioListener) {
        this.sDestinatario = sDestinatario;
        this.sReferencia = sReferencia;
        this.sMonto = sMonto;
        this.sDate = sDate;
        this.emailEnvio = emailEnvio;
        this.resources = MposApplication.getInstance().getResources();
        this.envioListener = envioListener;
        this.tipoTicket = TIPO_TICKET.QR;
    }

    public void init(final boolean fastSend) {
        final String header = SigmaBdManager.getHeaderTicket(operacion, new OnFailureListener.BasicOnFailureListener(TAG, resources.getString(R.string.error_encabezado_ticket)));
        final String body = new String(((RespuestaTrx) respuesta).getCamposTicket().getPlantillaCuerpo());
        final String footer = SigmaBdManager.getFooterTicket(operacion, new OnFailureListener.BasicOnFailureListener(TAG, resources.getString(R.string.error_pie_ticket)));
        final CamposTicket camposTicket = ((RespuestaTrx) respuesta).getCamposTicket();

        ticket = generateTicket(header, body, footer, camposTicket);

        if (fastSend) {
            contactNumber = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getContactPhone();
            contactEmail = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getContactEmail();
            createAndSendEicket(
                    ((RespuestaTrxCierreTurno) respuesta).getCamposCierreTurno().getImporte(),
                    contactNumber, contactEmail, emailEnvio, ((RespuestaTrxCierreTurno) respuesta).getCamposCierreTurno().getRefLocal(), this.dni);
        }
    }

    public void initQr() {
        contactNumber = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getContactPhone();
        contactEmail = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getContactEmail();
        createAndSendEicket(
                new BigDecimal(this.sMonto),
                contactNumber,
                contactEmail,
                emailEnvio,
                this.sReferencia, this.dni);

    }

    public void createAndSendEicket(final BigDecimal sImport, final String sContactPhone, final String sContactEmail, final String envioMail, final String refOperacion, String dni) {
        final CharSequence csTicket = ticket;
        String html64 = createHtml64(sImport, csTicket, sContactPhone, sContactEmail, dni);
        enviarTicket(html64, envioMail, refOperacion);
    }

    public String createHtml64(final BigDecimal sImport, final CharSequence csTicket, final String sContactPhone, final String sContactEmail, String dni) {
        try {
            byte[] htmlBytes;
            String html = "";
            String ssDate = "";
            final NumberFormat numberFormat = SigmaBdManager.getNumberFormat(new OnFailureListener.BasicOnFailureListener("TicketGenerator", "Error al obtener number format"));

            switch (this.tipoTicket) {
                case OPERACION:
                    html = (Utilities.getStringFromRawFile(R.raw.e_ticket));
                    html = html.replace(STRING_CURRENCY, "");

                    if (sImport == null) {
                        html = html.replace(STRING_IMPORT, "");
                    } else {
                        String importeFormateado = Utilities.getFormatedImport(sImport);
                        html = html.replace(STRING_IMPORT, importeFormateado);
                    }
                    html = html.replace(STRING_CENTS, "");

                    String csT = "<pre>".concat(csTicket.toString().concat("</pre>"));

                    html = html.replace(STRING_TICKET, csT);
                    break;
                case QR:
                    html = (Utilities.getStringFromRawFile(R.raw.e_ticket_qr));
                    html = html.replace(STRING_DESTINATARIO, this.sDestinatario);
                    html = html.replace(STRING_REFERENCIA, this.sReferencia);
                    html = html.replace(STRING_MONTO, numberFormat.format(ApiData.APIDATA.getDatosSesion().getDatosTPV().convertirImporte(sImport.toString())));
                    break;
                default:
                    break;
            }

            //html = html.replace(STRING_LATITUD, "4.694630");
            //html = html.replace(STRING_LONGITUD, "-74.053954");
            //html = html.replace(STRING_API_KEY, MposApplication.getInstance().getString(R.string.google_api_key));
            html = html.replace(STRING_VERSION_NAME, BuildConfig.VERSION_NAME);

            html = html.replace(STRING_DATE, UtilsDate.getDateNewFormat(new Date()));
            //imageSignString64 se usa para almacenar el base 64 de la Firma
            if (imageSignString64 != null && !imageSignString64.equals("")) {
                imageSignString64 = imageSignString64.replaceAll("\n", "");
                html = html.replace(STRING_SIGN_IMAGE64, "<img src='data:image/png;base64," + imageSignString64 + "' alt='Firma' style='margin-top: 15px; width: 350px; height: auto;'/>");
            } else {
                html = html.replace(STRING_SIGN_IMAGE64, "");
            }
            //html = html.replace(STRING_FULLCARGA_LABEL, Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getFullcargaLabel());
            html = html.replace(STRING_CONTACT_PHONE, sContactPhone);
            html = html.replace(STRING_CONTACT_EMAIL, sContactEmail);
            String sYear = new SimpleDateFormat("yyyy", Locale.getDefault())
                    .format(new Date());
            html = html.replace(STRING_YEAR, sYear);
            html = html.replace(STRING_DNI_CODE, dni != null ? dni : "");

            //CONVERTIR HTML A BASE 64
            htmlBytes = html.getBytes(StandardCharsets.UTF_8);
            return new String(Base64.encode(htmlBytes, Base64.NO_WRAP), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Logger.LOGGER.throwing(TAG, 1, e, e.getMessage() + resources.getString(R.string.error_construccion_ticket));
            return "";
        }
    }

    public SpannableStringBuilder generateTicket(final String header, final String body, final String footer, final CamposTicket camposTicket) {
        try {
            final SpannableStringBuilder ticketStringBuilder = new SpannableStringBuilder();
            TicketGenerator ticketGenerator = new TicketGenerator();
            ticketGenerator.setDecodeData(camposEMVData);
            if (header != null && !header.isEmpty()) {
                ticketStringBuilder.append(ticketGenerator.getTicketContent(header, camposTicket));
            }
            if (body != null && !body.isEmpty()) {
                ticketStringBuilder.append(ticketGenerator.getTicketContent(HexUtil.hexStringFromBytes(body.getBytes()), camposTicket));
            }
            if (footer != null && !footer.isEmpty()) {
                ticketStringBuilder.append(ticketGenerator.getTicketContent(footer, camposTicket));
            }
            return ticketStringBuilder;
        } catch (final Exception e) {

            Logger.LOGGER.throwing(TAG, 1, e, e.getMessage() + resources.getString(R.string.error_construccion_ticket));
            return null;
        }
    }


    private void enviarTicket(final String html, final String envioMail, final String refOperacion) {

        //ENVIAR EL TICKET
        enviarEmailEticket(html, envioMail, refOperacion);
    }

    private void enviarEmailEticket(final String html, final String envioMail, final String refOperacion) {

        final TicketMailInteractor interactor = new TicketMailInteractor(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos());
        interactor.setInteractorListener(result -> {
            if (result instanceof Response && ((Response) result).code() == 200) {
                //EXITOSO
                envioListener.onSuccesMail();
            } else {
                //ERROR
                envioListener.onFailMail();
            }
        });
        Integer isPci = SigmaBdManager.getProducto(operacion, new OnFailureListener.BasicOnFailureListener("", "")).getPci();
        interactor.sendMailHtml(
                ApiData.APIDATA.getDatosSesion().getIdSesion(),
                envioMail,
                html,
                "VENTA", /*tipoOperacion.name(),*/
                isPci == 1,
                refOperacion,
                ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod()
        );
    }

    public byte[] getImageBites() {
        return imageBites;
    }

    public void setImageBites(byte[] imageBites) {
        this.imageBites = imageBites;
        this.imageSignString64 = Base64.encodeToString(imageBites, Base64.DEFAULT);
    }

    public CharSequence getTicket() {
        return ticket;
    }

    public void setTicket(CharSequence ticket) {
        this.ticket = ticket;
    }


    public interface EnvioMailInterfece {
        void onSuccesMail();

        void onFailMail();

    }
}
