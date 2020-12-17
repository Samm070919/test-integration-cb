package com.pagatodoholdings.posandroid.secciones.sales.cardreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.qposlib.QPosManager;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.broadcasts.BroadcastManager;
import com.pagatodo.qposlib.dongleconnect.DongleConnect;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodo.qposlib.pos.dspread.DSpreadDevicePosFactory;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.databinding.CardReaderFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.PciLoginManager;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.bluetooth.BluetoothManagerUtil;

import java.sql.SQLException;

import static com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionSubVincular.NAME_RSA_PCI;

public class CardReaderFragment extends PciSalesFragmentSupport<CardReaderFragmentBinding> {

    private static final int TIEMPO_MINIMO = 500;//Milisegundos
    private BluetoothDevice device;
    private long currentTime;
    private AbstractDongle mQpos;
    private QPosManager qPosManager;
    protected FirebaseAnalytics firebaseAnalytics;

    public static CardReaderFragment newInstance() {
        return new CardReaderFragment();
    }


    /**
     * inicializa el módulo bluetooth
     */
    private void startBluetoothManagement() {
        BluetoothManagerUtil mBluetoothAdapter = BluetoothManagerUtil.getBluetoothAdapter();
        mBluetoothAdapter.setBluetoothEnable(true);
        BluetoothAdapter bluetoothAdapter = mBluetoothAdapter.getmBluetoothAdapter();
        device = bluetoothAdapter.getRemoteDevice(MposApplication.getInstance().getPreferenceManager().getValue(Constantes.Preferencia.DONGLE_MAC_ADDRESS, ""));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.card_reader_fragment, container, false);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.hideAllToolbar();
        startBluetoothManagement();
        linkQpos(device);

        binding.btnReintentarConexion.setOnClickListener(v -> {
            binding.btnReintentarConexion.setVisibility(View.GONE);
            activity.resetQposManager();
            startBluetoothManagement();
            linkQpos(device);
        });

    }


    /**
     * Aquí se establece la conexión del Qpos después de ser pareado por bluetooth
     *
     * @param device El dispositivo bluetooth que quieres vincular
     */

    private void linkQpos(BluetoothDevice device) {
        binding.progressCircularBar.setVisibility(View.VISIBLE);
        currentTime = System.currentTimeMillis();

        //No implementation
        BroadcastManager broadcastManager = new BroadcastManager(getActivity(), bundle -> {
        });
        mQpos = new DSpreadDevicePosFactory().getDongleDevice(device, PosInterface.Tipodongle.DSPREAD, new DongleConnect() {
            @Override
            public void onDeviceConnected() {
                mQpos.getSessionKeys(NAME_RSA_PCI, PosInstance.getInstance().getAppContext());
                PosInstance.getInstance().setDongle(mQpos);
            }

            @Override
            public void ondevicedisconnected() {
                PosInstance.getInstance().setDongle(null);
                binding.btnReintentarConexion.setVisibility(View.VISIBLE);
                binding.progressCircularBar.setVisibility(View.GONE);
            }

            @Override
            public void deviceOnTransaction() {
                //No implementation
            }

            @Override
            public void onRequestNoQposDetected() {
                if (System.currentTimeMillis() > (currentTime + TIEMPO_MINIMO)) {
                    PosInstance.getInstance().setDongle(null);
                    binding.btnReintentarConexion.setVisibility(View.VISIBLE);
                    binding.progressCircularBar.setVisibility(View.GONE);

                    if (activity != null)
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
                    //No implementation
                }

                @Override
                public void loginProcessFinished(boolean isSuccessful, Object result) {
                    if (isSuccessful) {
                        activity.getRouter().showInsertCard(getParentFragment());
                    } else {
                        firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
                        qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
                        qPosManager.resetQPOS();
                        qPosManager.closeCommunication();
                    }
                }
            });
        } catch (RuntimeException exc) {
            activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_intente_mas_tarde), null);
            Logger.LOGGER.throwing("Adquirencia", 1, exc, exc.getMessage());
        } catch (SQLException exc) {
            activity.despliegaModal(true, false, getString(R.string.error), getString(R.string.error_conexion_bd), null);
            Logger.LOGGER.throwing("Adquirencia", 1, exc, exc.getMessage());
        }
    }


}
