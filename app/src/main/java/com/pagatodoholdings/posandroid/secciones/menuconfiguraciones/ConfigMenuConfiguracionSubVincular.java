package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.qposlib.QPosManager;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.broadcasts.BroadcastManager;
import com.pagatodo.qposlib.dongleconnect.DongleConnect;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodo.qposlib.pos.dspread.DSpreadDevicePosFactory;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuConfiguracionSubVincularBinding;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.PciLoginManager;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.DongleRegistro;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroDongleInteractor;
import com.pagatodoholdings.posandroid.utils.AppExecutors;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.bluetooth.BluetoothManagerUtil;

import java.sql.SQLException;
import java.util.HashMap;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;

public class ConfigMenuConfiguracionSubVincular extends AbstractConfigMenu {

    public static final String NAME_RSA_PCI = BuildConfig.NAME_RSA_PCI;
    private FragmentConfigMenuConfiguracionSubVincularBinding mBinding;
    private ConfigMenuBluetoothAdapter mRvAdapter;
    private BluetoothManagerUtil mBluetoothAdapter;
    private static final String POS_ID = "posId";
    private String mQposId = "";
    private AbstractDongle mQpos;
    private QPosManager qPosManager;
    private BluetoothDevice mDevice;
    private int mTimeOutCounter;
    private BroadcastManager broadcastManager;
    private String nombreTransaccion = "";

    public ConfigMenuConfiguracionSubVincular() {
        // Required empty public constructor
    }

    public static ConfigMenuConfiguracionSubVincular newInstance() {
        return new ConfigMenuConfiguracionSubVincular();
    }

    public void setNombreTransaccion(String nombreTransaccion) {
        this.nombreTransaccion = nombreTransaccion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showTurnOnDialog(
                R.drawable.ic_ilustracion_bluetooth,
                getString(R.string.config_submenu_enciende_bluetooth),
                getString(R.string.turn_on_bluetooth_dialog_body_text),
                getString(R.string.continuar),
                false
        );
        initUi(inflater, container);
        return mBinding.getRoot();
    }

    /**
     * inicializa el módulo bluetooth
     */
    private void startBluetoothManagement() {
        mBluetoothAdapter = BluetoothManagerUtil.getBluetoothAdapter();
        mBluetoothAdapter.startBluetoothManagement(getActivity(), new BluetoothManagerUtil.BluetoothDevicesListListener() {
            @Override
            public void deviceFound(BluetoothDevice device) {
                String name = device.getName();
                if (name == null) {
                    return;
                }
                if (name.contains("MPOS") || name.contains("POS")) {
                    mRvAdapter.insertItem(device);
                    if (mBinding.cvDispositivos.getVisibility() != View.VISIBLE) {
                        mBinding.cvDispositivos.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void clearList() {
                mRvAdapter.clearList();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        broadcastManager = new BroadcastManager(getActivity(), bundle -> {
        });
        startBluetoothManagement();
    }

    @Override
    public void onStop() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopBluetoothManagement(getActivity());
            showProgressBar();
        }
        super.onStop();
    }

    private void initUi(final LayoutInflater inflater, final ViewGroup container) {
        mBinding = FragmentConfigMenuConfiguracionSubVincularBinding.inflate(inflater, container, false);

        mBinding.cvDispositivos.setVisibility(View.INVISIBLE);

        //listeners
        //mBinding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuConfiguracionSubVincular.this));
        mBinding.ivClose.setOnClickListener(v ->
                loadSettings(this));

        //mBinding.tvBuscarDispositivos.setOnClickListener(v -> buscarDispositivos());

        //inicializar recycler view
        mBinding.rvDispositivosEncontrados.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });

        mRvAdapter = new ConfigMenuBluetoothAdapter(this::pairBluetoothDevice);

        mBinding.rvDispositivosEncontrados.setAdapter(mRvAdapter);
        /*mBinding.rvDispositivosEncontrados.setAdapter(new ConfigMenuBluetoothAdapter(new ConfigMenuBluetoothAdapter.BtnClickListener() {
            @Override
            public void onBtnClick(BluetoothDevice device) {
                pairBluetoothDevice(device);
            }
        }));*/
    }

    private void buscarDispositivos() {
        if (mBluetoothAdapter != null) {
            showProgressBar();
            mBluetoothAdapter.setBluetoothDiscoveryFinishedListener(this::hideProgressBar);
            mBluetoothAdapter.startSearchBluetoothDevices();
        }
    }

    private void showProgressBar() {
        mBinding.progressCircularBar.setVisibility(View.VISIBLE);
        //mBinding.tvBuscarDispositivos.setClickable(false);
    }

    private void hideProgressBar() {
        mBinding.progressCircularBar.setVisibility(View.GONE);
        //mBinding.tvBuscarDispositivos.setClickable(true);
    }

    private void pairBluetoothDevice(final BluetoothDevice device) {

        if (mBluetoothAdapter == null) {
            return;
        }

        hideProgressBar();
        muestraProgressDialog();
        linkQpos(device);
    }

    private void validateTransaction() {
        if ("ADQUIRENCIA".equals(nombreTransaccion)) {
            ((HomeActivity) activity).cargarFragmentCobrar();
        } else {
            ((HomeActivity) activity).regresaMenu();
        }
    }

    /**
     * Usa este método para mostrar el mensaje de vinculando, pero automáticamente maneja el time out
     */
    private void muestraProgressDialog() {
        activity.muestraProgressDialogConTimeOut("Vinculando", 30000, () -> {
            mRvAdapter.deviceHasStopedBonding();
            activity.despliegaModal(true, false, getString(R.string.error),
                    getString(R.string.dongle_timeout), () -> AppExecutors.getInstance().mainThread().execute(() -> {
                        desconectarDongle();
                        activity.ocultaProgressDialogTimeOut();
                    }));
        });
    }

    /**
     * Aquí se establece la conexión del Qpos después de ser pareado por bluetooth
     *
     * @param device El dispositivo bluetooth que quieres vincular
     */
    private void linkQpos(BluetoothDevice device) {
        this.mDevice = device;
        mQpos = new DSpreadDevicePosFactory().getDongleDevice(device, PosInterface.Tipodongle.DSPREAD, new DongleConnect() {

                    @Override
                    public void onDeviceConnected() {
                        PosInstance.getInstance().setDongle(mQpos);
                        mTimeOutCounter = 0;
                        setQposId();
                        vincularDongleService();
                    }

                    @Override
                    public void ondevicedisconnected() {
                        //NOT IMPLEMENTED
                    }

                    @Override
                    public void deviceOnTransaction() {
                        //NOT IMPLEMENTED
                    }

                    @Override
                    public void onRequestNoQposDetected() {
                        dispositivoDesconectadoHandler();
                    }

                    @Override
                    public void onSessionKeysObtenidas() {
                        try {
                            PciLoginManager.realizarSessionPCI(new PciLoginManager.SessionPciListener() {
                                @Override
                                public void loginInProcess() {
                                    //NOT IMPLEMENTED
                                }

                                @Override
                                public void loginProcessFinished(boolean isSuccessful, Object result) {
                                    ocultarDialogoYDesbloquearDispositivos();
                                    if (isSuccessful) {
                                        //activity.despliegaModal(false, false, "", getActivity().getString(R.string.exito_vincular_lector_tarjeta), ConfigMenuConfiguracionSubVincular.this::validateTransaction);
                                        showTurnOnDialog(
                                                R.drawable.ic_ilustracion_bienvenida,
                                                getString(R.string.text_upgrade_felicidades),
                                                getString(R.string.lector_tarjetas_conectado_exitosamente),
                                                getString(R.string.continuar),
                                                true
                                        );
                                    } else if (!(result instanceof Throwable)) {
                                        desconectarDongle();
                                        firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
                                        //activity.despliegaModal(true, false, getString(R.string.error), ((AbstractRespuesta) result).getMsjError(), () -> {
                                        //});
                                        showHasBeenLinkedDialog();
                                    } else {
                                        desconectarDongle();
                                        final Throwable throwable = (Throwable) result;
                                        firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
                                /*activity.despliegaModal(true, false, getString(R.string.error), throwable.getMessage(), () -> {

                                });*/
                                        showHasBeenLinkedDialog();
                                    }
                                }
                            });
                        } catch (RuntimeException exc) {
                            desconectarDongle();
                            showHasBeenLinkedDialog();
                            ocultarDialogoYDesbloquearDispositivos();
                            //activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_intente_mas_tarde), null);
                            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                        } catch (SQLException exc) {
                            desconectarDongle();
                            showHasBeenLinkedDialog();
                            ocultarDialogoYDesbloquearDispositivos();
                            //activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_conexion_bd), null);
                            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                        }
                    }
                },
                BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa"));
        PosInstance.getInstance().setDongle(mQpos);
        broadcastManager.realizarConexion(mQpos);
    }

    /**
     * Desconecta el dongle si está conectado
     */
    private void desconectarDongle() {
        AppExecutors.getInstance().mainThread().execute(() -> {
            buscarDispositivos();
            qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
            if (qPosManager != null) {
                qPosManager.resetQPOS();
                qPosManager.closeCommunication();
            }
        });
    }

    /**
     * Cuando selecciones un item de la lista, automaticamente se bloquearán los demás dispositivos.
     * Llama este método para ocultar el diálogo de vinculando y desbloquear los items
     */
    private void ocultarDialogoYDesbloquearDispositivos() {
        activity.ocultaProgressDialogTimeOut();
        mRvAdapter.deviceHasStopedBonding();
    }

    /**
     * Cuando se intenta vincular un dongle y se apaga en el proceso, el callback de
     * OnRequestNoQposDetected se llama 3 veces, así que se tuvo que implementar un número de intentos.
     * Además, el callback también se llamda dos veces aún cuando la conexión con el Qpos será
     * exitosa
     */
    private void dispositivoDesconectadoHandler() {
        if (mTimeOutCounter == 2) {
            ocultarDialogoYDesbloquearDispositivos();
            mTimeOutCounter = 0;
            activity.despliegaModal(true, false, getString(R.string.Dispositivo_NoEncontrado), "", null);
            buscarDispositivos();
        } else {
            mTimeOutCounter++;
        }
    }

    private void setQposId() {
        HashMap<String, String> qposIdHash = new HashMap<>();
        qposIdHash.putAll(mQpos.getQposIdHash());
        mQposId = qposIdHash.containsKey(POS_ID) ? qposIdHash.get(POS_ID) : "";
    }

    private void vincularDongleService() {
        qPosManager = (QPosManager) PosInstance.getInstance().getDongle();
        final RegistroDongleInteractor registroDongleInteractor = new RegistroDongleInteractor();
        registroDongleInteractor.registroDongle(new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                desconectarDongle();
                ocultarDialogoYDesbloquearDispositivos();
                showHasBeenLinkedDialog();
                //activity.despliegaModal(true, false, getString(R.string.error), Utilities.obtenerMensajeError(throwable.getMessage()), () -> mBluetoothAdapter.disconnectDevice());
            }

            @Override
            public void onSuccess(Object result) {
                preferenceManager.setValue(DONGLE_VINCULADO, mQposId);
                preferenceManager.setValue(Constantes.Preferencia.DONGLE_MAC_ADDRESS, mDevice.getAddress());
                //Todo: Adjust Event
//                AdjustEvent adjustEvent = new AdjustEvent("hdhjfo");
//                Adjust.trackEvent(adjustEvent);
                qPosManager.getSessionKeys(NAME_RSA_PCI, PosInstance.getInstance().getAppContext());
            }
        }, new DongleRegistro(mQposId, preferenceManager.getValue(Constantes.Preferencia.EMAIL, ""),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod()));
    }

    private void showTurnOnDialog(
            int resource,
            String title,
            String body,
            String buttonText,
            Boolean flag
    ) {
        DialogTurnOnBluetooth dialog = new DialogTurnOnBluetooth(resource, title, body, buttonText, flag);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        dialog.show(fm, "Turn on Bluetooth dialog");
    }

    private void showHasBeenLinkedDialog() {
        DialogDeviceHasBeenLinked dialog = new DialogDeviceHasBeenLinked();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        dialog.show(fm, "Device has been linked");
    }

    private void loadSettings(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            ((HomeActivity) getActivity()).cargarFragmentMiCuenta();
        }
    }
}
