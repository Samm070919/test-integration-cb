package com.pagatodoholdings.posandroid.secciones.ticket; //NOPMD

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.DecodeData;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.DateUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FieldsTicketLayout;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.SuccessfulSaleFragmentBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;
import com.pagatodoholdings.posandroid.secciones.firma.FirmaActivity;
import com.pagatodoholdings.posandroid.secciones.formularios.FormularioFragmentFactory;
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean;
import com.pagatodoholdings.posandroid.secciones.servicios.DialogGuardarServicio;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.ManejadorFragments;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsDate;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Iconos;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.data.respuesta.Respuesta;
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno;
import net.fullcarga.android.api.formulario.Formulario;
import net.fullcarga.android.api.oper.TipoOperacion;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("PMD.GodClass")
public class EmailTicketFragment extends AbstractFragment implements DialogGuardarServicio.OnMessageToSnackBarListener {

    private static final int ACTIVITY_RESULT = 1;
    //--------Fonts-----------------------------------------------------

    //----------UI-------------------------------------------------------
    protected SuccessfulSaleFragmentBinding binding;

    //-----Inst ----------------------------------------------------------
    protected NivelMenu nivelMenu;
    protected boolean requiereFirma;
    protected byte[] imagebites;
    protected Menu menu;
    private Operaciones operacion;
    private Respuesta respuesta;
    private boolean operacionSiguiente;
    private String sBites = "";
    private DecodeData camposEMVData;
    private boolean isMailDefault = true;
    private Drawable icono;
    private AbstractActivity aActivity;
    private boolean favAsSkipped;
    private String dni;

    public static EmailTicketFragment newInstance(
            final NivelMenu nivelMenu,
            final Menu menu,
            final Operaciones operacion,
            final Respuesta respuesta,
            final Boolean requiereFirma,
            final DecodeData emvData,
            final String dni
    ) {
        final EmailTicketFragment fragment = new EmailTicketFragment();
        fragment.nivelMenu = nivelMenu;
        fragment.requiereFirma = requiereFirma;
        fragment.menu = menu;
        fragment.operacion = operacion;
        fragment.respuesta = respuesta;
        fragment.operacionSiguiente = respuesta.getOperacionSiguiente().isOperNextRequired();
        fragment.camposEMVData = emvData;
        fragment.dni = dni;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        aActivity = (AbstractActivity) getActivity();
        binding = SuccessfulSaleFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        try {
            if (requestCode == ACTIVITY_RESULT) {
                if (resultCode == Activity.RESULT_OK) {
                    imagebites = data.getByteArrayExtra("ByteImage");
                    sendMailDefault();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    activity.despliegaModal(true, false, "", getString(R.string.error_envio_firma), null);
                }
            }
        } catch (RuntimeException exe) {
            Logger.LOGGER.throwing("Firma", 1, exe, exe.getMessage());
            activity.despliegaModal(true, false, "", getString(R.string.error_envio_firma), null);
        }
    }

    private void initUI() { //NOSONAR
        activity = (AbstractActivity) getActivity();

        binding.tvFechaPago.setText(UtilsDate.getDateNewFormat(new Date()));

        BigDecimal bigDecimal = ((RespuestaTrxCierreTurno) respuesta).getCamposCierreTurno().getImporte();
        CurrencyImport currencyImport = Utilities.getFormatedImportObject(bigDecimal);

        binding.mvImporte.setMonto(currencyImport);
        binding.mvImporte.setNewTextSizeIndividualMonto(15, 30, 15);

        final Productos productos = SigmaBdManager.getProducto(operacion, new OnFailureListener.BasicOnFailureListener("EmailTicketFragment", ""));
        if (productos != null && productos.getPci() != 1) {
            binding.textView8.setText("Recarga exitosa");
            Map<String, String> campos = new LinkedHashMap<>();
            campos.put("Compañía", productos.getDescripcion());
            campos.put("Teléfono", ((RespuestaTrxCierreTurno) respuesta).getCamposCierreTurno().getRefCliente());
            campos.put("Autorización", ((RespuestaTrxCierreTurno) respuesta).getCamposCierreTurno().getRefLocal());
            campos.put("Fecha", DateUtil.getDateNow());

            for (Map.Entry<String, String> entry : campos.entrySet()) {
                binding.contenedorCampos.addView(
                        new FieldsTicketLayout(getContext(), null, entry.getKey(), entry.getValue()));
            }
        }

        if (productos.getIcono() == null) {
            binding.icono.setImageResource(R.drawable.ic_tarjeta_blue);
        } else {
            Iconos intIcono = SigmaBdManager.getIcono(productos.getIcono(), this);
            icono = Utilities.getIcono(intIcono.getRuta());
            binding.icono.setImageDrawable(icono);
        }

        binding.labelConcept.setVisibility(View.VISIBLE);
        binding.labelTarjeta.setVisibility(View.VISIBLE);

        final RespuestaTrxCierreTurno restCierreTurno = (RespuestaTrxCierreTurno) respuesta;

        if (camposEMVData != null && camposEMVData.getMaskedPAN().length() > 4) {
            String last4 = camposEMVData.getMaskedPAN().substring(camposEMVData.getMaskedPAN().length() - 4);
            binding.textTarjeta.setText("**" + last4);
        } else {
            binding.textTarjeta.setText(restCierreTurno.getCamposCierreTurno().getRefCliente());
        }

        binding.textAuthEmisor.setText(restCierreTurno.getCamposCierreTurno().getRefRemota());
        binding.textFolio.setText(restCierreTurno.getCamposCierreTurno().getRefLocal());
        binding.textTarjeta.setVisibility(View.VISIBLE);
        binding.textConcept.setVisibility(View.VISIBLE);
        binding.textConcept.setSelected(true);

        if (operacion.getOperacion().equalsIgnoreCase(Constantes.DEVOLUCION)) {
            binding.textConcept.setText("Cancelación con Tarjeta");
            binding.cardCancelado.setVisibility(View.VISIBLE);
            binding.tvMontoLabel.setText("Monto Cancelado");
        } else {
            binding.textConcept.setText("Venta con Tarjeta");
            binding.imageView12.setVisibility(View.VISIBLE);
            binding.textView8.setVisibility(View.VISIBLE);
            binding.tvMontoLabel.setText("Monto Cobrado");
        }

        //Envío default
        if (requiereFirma && imagebites == null) {
            final Intent signatureIntent = new Intent(getActivity(), FirmaActivity.class);
            if (camposEMVData != null) {
                signatureIntent.putExtra(FirmaActivity.CARD_HOLDER_NAME, camposEMVData.getCardholderName());
                signatureIntent.putExtra(FirmaActivity.CLIENT_DNI_CODE, dni);
            }
            startActivityForResult(signatureIntent, ACTIVITY_RESULT);
        } else {
            sendMailDefault();
        }

        //TODO: Quizá aqui se le asigne el listener al boton de aceptar/continuar/finalizar
        binding.btnAceptar.setOnClickListener(view -> {
            isMailDefault = false;
            if (operacionSiguiente) {
                final TipoOperacion tipoOperacionNext = respuesta.getOperacionSiguiente().getTipoOperacionNext();
                if (tipoOperacionNext == TipoOperacion.CONSULTA_Z || tipoOperacionNext == TipoOperacion.CONSULTA_X || tipoOperacionNext == TipoOperacion.VENTA || tipoOperacionNext == TipoOperacion.DEVOLUCION) {
                    cargarFragmentOperacionSiguiente(nivelMenu,
                            menu,
                            SigmaBdManager.getOperacionPorProducto(
                                    menu.getProducto(),
                                    respuesta.getOperacionSiguiente().getTipoOperacionNext().getTipo(),
                                    new BasicOnFailureListener(TAG, getString(R.string.error_operacionproducto_ticket))
                            ),
                            respuesta.getOperacionSiguiente().getFormularioNext());
                } else if (tipoOperacionNext == TipoOperacion.ICONOS_LIST || tipoOperacionNext == TipoOperacion.LOGOS_LIST || tipoOperacionNext == TipoOperacion.INIT_LIST) {
                    activity.finish();
                } else {
                    ApiData.APIDATA.setDatosOperacionSiguiente(null);
                    activity.finish();
                }
            } else {
                //Seccion Para enviar ticket
                if (!binding.textoEnviarMail.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(binding.textoEnviarMail.getText()).find()) {
                    activity.muestraProgressDialog("Enviando");
                    fastSend(imagebites);
                    ApiData.APIDATA.setDatosOperacionSiguiente(null);
                } else {
                    showInfoAlert("Correo no válido");
                }
            }
        });
    }

    private void sendMailDefault() {
        int isPci = 0;
        isPci = SigmaBdManager.getProducto(operacion, new OnFailureListener.BasicOnFailureListener("", "")).getPci();

        if (isPci == 1) {
            fastSend(imagebites);
        }
    }

    protected void cargarFragmentOperacionSiguiente(final NivelMenu nivelMenu, final Menu menu, final Operaciones operacion, final Formulario formulario) {
        final AbstractFragment formularioFragment = FormularioFragmentFactory.build(nivelMenu, menu, operacion, formulario);
        formularioFragment.setRetrocesoListener(retrocesoListener);
        final ManejadorFragments manejadorFragments = new ManejadorFragments(activity.getSupportFragmentManager());
        manejadorFragments.cargarFragmentPantallaCompleta(formularioFragment, activity);
    }

    @Override
    protected boolean isTomandoBack() {
        if (!requierePCI()) {
            return operacionSiguiente;
        } else {
            return true;
        }
    }

    private boolean requierePCI() {
        if (requiereFirma) {
            activity.despliegaModal(true, false, "Firma", activity.getString(R.string.enviar_firma), () -> {
                //
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        //  none
    }

    private void enviarMailExitoso() {
        activity.showDialogButtonAcept(R.layout.dialog_correo_enviado, "Aceptar", new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
            @Override
            public void onCancel() {
                //No implementation
            }

            @Override
            public void onAccept() {
                if (favAsSkipped) {
                    activity.onBackPressed();
                } else {
                    goToSaveFavorites();
                }
            }
        });
    }

    private void showInfoAlert(final String message) {
        new androidx.appcompat.app.AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle("Email")
                .setMessage(message)
                .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                    //No implementation
                })
                .show();
    }

    private void fastSend(byte[] imagebites) {
        if (imagebites != null) {
            sBites = Base64.encodeToString(imagebites, Base64.DEFAULT);
        }

        AsyntaskSendEmail sendEmailAsyn = new AsyntaskSendEmail();
        sendEmailAsyn.execute();

        if (!isMailDefault) {
            activity.muestraProgressDialog("Procesando");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                //que hacer despues de 3 segundos

                activity.ocultaProgressDialog();
                enviarMailExitoso();
            }, 3000);
        }
    }

    @Override
    public void onMessageToSnackBarListener(boolean b, @NotNull String message) {
        if (b) {
            aActivity.showSuccessSnackBar(binding.getRoot(), message);
        } else {
            aActivity.showErrorSnackBar(binding.getRoot(), message);
        }
    }

    public void setFavAsSkipped(boolean favAsSkipped) {
        this.favAsSkipped = favAsSkipped;
    }

    private class AsyntaskSendEmail extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            String mail = "";

            if (!isMailDefault) {
                mail = binding.textoEnviarMail.getText().toString().isEmpty() ?
                        MposApplication.getInstance().getDatosLogin().getCliente().getEmail() : binding.textoEnviarMail.getText().toString();
            }

            ETicket digitalTicket = new ETicket(operacion, respuesta, sBites, camposEMVData, mail, dni,
                    new ETicket.EnvioMailInterfece() {
                        @Override
                        public void onSuccesMail() {
                            activity.ocultaProgressDialog();
                        }

                        @Override
                        public void onFailMail() {
                            activity.ocultaProgressDialog();
                        }
                    });

            digitalTicket.init(true);
            return true;
        }
    }

    private Productos getProduct(Operaciones operacion) {
        return SigmaBdManager.getProducto(
                operacion,
                new BasicOnFailureListener(
                        TAG,
                        "ErrorAlConsultar"
                )
        );
    }

    private void goToSaveFavorites() {
        FavoritoBean newReminder = new FavoritoBean();
        newReminder.setProcod(getProduct(operacion).getCodigo());

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        DialogGuardarServicio dialogGuardarServicio = new DialogGuardarServicio(icono, getProduct(operacion));
        dialogGuardarServicio.setReminder(newReminder);
        dialogGuardarServicio.setType("Añadir");
        dialogGuardarServicio.setOnDismissListener(dialogInterface -> {
        });

        dialogGuardarServicio.setMessageListener(this);
        dialogGuardarServicio.show(transaction, "FragmentTransaction");
    }
}