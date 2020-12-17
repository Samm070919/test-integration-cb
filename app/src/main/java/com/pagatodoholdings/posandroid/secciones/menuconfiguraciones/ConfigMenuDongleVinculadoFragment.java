package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.qposlib.QPosManager;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.broadcasts.BroadcastManager;
import com.pagatodo.qposlib.dongleconnect.DongleConnect;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodo.qposlib.pos.QPOSDeviceInfo;
import com.pagatodo.qposlib.pos.dspread.DSpreadDevicePosFactory;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigLectorBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.PciLoginManager;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.utils.AppExecutors;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.bluetooth.BluetoothManagerUtil;

import java.sql.SQLException;

import static com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionSubVincular.NAME_RSA_PCI;

public class ConfigMenuDongleVinculadoFragment extends AbstractConfigMenu {

    private static final int DESCONECTADO = -1;
    private static final int CONECTADO = 0;
    private static final int CONECTANDO = 2;
    private static final int TIEMPO_MINIMO = 500;//Milisegundos

    private AbstractConfigMenu aCMenu;

    private FragmentConfigLectorBinding binding;
    private AbstractDongle mQpos;
    private BluetoothDevice device;
    private int statusConnection;
    private long currentTime;
    private QPosManager qPosManager;

    /**
     * inicializa el módulo bluetooth
     */
    private void startBluetoothManagement() {
        BluetoothManagerUtil mBluetoothAdapter = BluetoothManagerUtil.getBluetoothAdapter();
        mBluetoothAdapter.setBluetoothEnable(true);
        BluetoothAdapter bluetoothAdapter = mBluetoothAdapter.getmBluetoothAdapter();
        device = bluetoothAdapter.getRemoteDevice(preferenceManager.getValue(Constantes.Preferencia.DONGLE_MAC_ADDRESS, ""));
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        activity = (AbstractActivity) getActivity();
        FragmentManager fragmentManager = getFragmentManager();
        startBluetoothManagement();
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        binding = FragmentConfigLectorBinding.inflate(infalter, container, false);
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuDongleVinculadoFragment.this));
        binding.tvUsuarioLector.setText(datosLogin.getCliente().getNombre() + " " + datosLogin.getCliente().getApellido1());
        binding.tvSerieLector.setText("SN: " + preferenceManager.getValue(Constantes.Preferencia.DONGLE_VINCULADO, ""));
        binding.btnReintentarConexion.setOnClickListener(v -> {
            if (statusConnection != CONECTADO && statusConnection != CONECTANDO) {
                linkQpos(device);
            }
        });

        int visibility = PciLoginManager.isPciLoggedIn() ? View.GONE : View.VISIBLE;
        binding.btnReintentarConexion.setVisibility(visibility);

        if (PosInstance.getInstance().getDongle() != null) {
            vistaDongleConectado();
        } else {
            linkQpos(device);
        }
        binding.btnCambioLector.setOnClickListener(v -> {
            showUnlinkDeviceDialog();
        });
    }

    private int getBatteryIcon(String batteryLevel) {
        batteryLevel = batteryLevel.replace("%", "");
        int battery = Integer.parseInt(batteryLevel);
        int batteryLogo = R.drawable.battery_100;
        if (battery > 49 && battery <= 74) {
            batteryLogo = R.drawable.battery_75;
        } else if (battery > 24 && battery <= 49) {
            batteryLogo = R.drawable.battery_50;
        } else if (battery <= 24) {
            batteryLogo = R.drawable.battery_25;
        }
        return batteryLogo;
    }

    /**
     * Aquí se establece la conexión del Qpos después de ser pareado por bluetooth
     *
     * @param device El dispositivo bluetooth que quieres vincular
     */
    private void linkQpos(BluetoothDevice device) {
        activity.muestraProgressDialog(getResources().getString(R.string.Conectando_Dispositivo));
        currentTime = System.currentTimeMillis();
        binding.btnReintentarConexion.setEnabled(false);
        statusConnection = CONECTANDO;
        BroadcastManager broadcastManager = new BroadcastManager(getActivity(), bundle -> {
        });
        mQpos = new DSpreadDevicePosFactory().getDongleDevice(device, PosInterface.Tipodongle.DSPREAD, new DongleConnect() {
            @Override
            public void onDeviceConnected() {
                statusConnection = CONECTADO;
                binding.btnReintentarConexion.setEnabled(false);
                mQpos.getSessionKeys(NAME_RSA_PCI, PosInstance.getInstance().getAppContext());
                PosInstance.getInstance().setDongle(mQpos);
            }

            @Override
            public void ondevicedisconnected() {
                statusConnection = DESCONECTADO;
                vistaDongleDesconectado();
            }

            @Override
            public void deviceOnTransaction() {
                //NOT IMPLEMENTED
            }

            @Override
            public void onRequestNoQposDetected() {
                if (System.currentTimeMillis() > (currentTime + TIEMPO_MINIMO)) {
                    vistaDongleDesconectado();
                    activity.ocultaProgressDialog();
                    Toast.makeText(activity, getString(R.string.connection_fail_bluetooth), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSessionKeysObtenidas() {
                realizarSesionPci();
            }
        }, BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa"));

        broadcastManager.realizarConexion(mQpos);
    }

    private void realizarSesionPci() {
        try {
            PciLoginManager.realizarSessionPCI(new PciLoginManager.SessionPciListener() {
                @Override
                public void loginInProcess() {
                    //NOT IMPLEMENTED

                }

                @Override
                public void loginProcessFinished(boolean isSuccessful, Object result) {
                    if (isSuccessful) {
                        activity.ocultaProgressDialog();
                        vistaDongleConectado();
                        showTurnOnDialog(
                                R.drawable.ic_ilustracion_bienvenida,
                                getString(R.string.text_upgrade_felicidades),
                                getString(R.string.lector_tarjetas_conectado_exitosamente),
                                getString(R.string.continuar)
                        );
                        /*activity.despliegaModal(false, false, "", getActivity().getString(R.string.exito_vincular_lector_tarjeta), () -> {
                        });*/
                    } else if (!(result instanceof Throwable)) {
                        firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
                        qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
                        qPosManager.resetQPOS();
                        qPosManager.closeCommunication();
                        vistaDongleDesconectado();
                        activity.ocultaProgressDialog();
                        showHasBeenLinkedDialog();
                        /*activity.despliegaModal(true, false, getString(R.string.error), ((AbstractRespuesta) result).getMsjError(), () -> {

                        });*/
                    } else {
                        final Throwable throwable = (Throwable) result;
                        firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
                        qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
                        qPosManager.resetQPOS();
                        qPosManager.closeCommunication();
                        vistaDongleDesconectado();
                        activity.ocultaProgressDialog();
                        showHasBeenLinkedDialog();
                        /*activity.despliegaModal(true, false, getString(R.string.error), throwable.getMessage(), () -> {

                        });*/
                    }
                }
            });
        } catch (RuntimeException exc) {
            activity.ocultaProgressDialog();
            activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_intente_mas_tarde), null);
            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
        } catch (SQLException exc) {
            activity.ocultaProgressDialog();
            activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_conexion_bd), null);
            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
        }
    }

    private void vistaDongleConectado() {
        AppExecutors.getInstance().mainThread().execute(() -> {
            QPosManager dongle = (QPosManager) PosInstance.getInstance().getDongle();
            QPOSDeviceInfo qposDeviceInfo = dongle.getDevicePosInfo();
            binding.imgLector.setAlpha(1.0f);
            binding.txtBatteryDongle.setText(qposDeviceInfo.getBatteryPercentage());
            binding.imgBatteryDongle.setImageResource(getBatteryIcon(qposDeviceInfo.getBatteryPercentage()));
            binding.btnReintentarConexion.setVisibility(View.GONE);
        });
    }

    private void vistaDongleDesconectado() {
        AppExecutors.getInstance().mainThread().execute(() -> {
            statusConnection = DESCONECTADO;
            binding.imgLector.setAlpha(0.5f);
            binding.txtBatteryDongle.setText("");
            binding.imgBatteryDongle.setImageResource(0);
            binding.btnReintentarConexion.setVisibility(View.VISIBLE);
            binding.btnReintentarConexion.setEnabled(true);
        });
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    public void desvincularDongleService(HomeActivity activity) {
        final ConfigMenuConfiguracionSubVincular fragmentSubvincular = new ConfigMenuConfiguracionSubVincular();
        fragmentSubvincular.setListener(getListener());
        activity.getSupportFragmentManager().beginTransaction().replace(activity.getBinding().flMainPantallaCompleta.getId(), fragmentSubvincular).commit();
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //NOT IMPLEMENTED
    }

    private void showUnlinkDeviceDialog() {
        DialogUnlinkDevice dialog = new DialogUnlinkDevice();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        dialog.show(fm, "Unlink device dialog");
    }

    private void showTurnOnDialog(int resource, String title, String body, String buttonText) {
        DialogTurnOnBluetooth dialog =
                new DialogTurnOnBluetooth(resource, title, body, buttonText, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        dialog.show(fm, "Turn on Bluetooth dialog");
    }

    private void showHasBeenLinkedDialog() {
        DialogDeviceHasBeenLinked dialog = new DialogDeviceHasBeenLinked();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        dialog.show(fm, "Device has been linked");
    }
}
