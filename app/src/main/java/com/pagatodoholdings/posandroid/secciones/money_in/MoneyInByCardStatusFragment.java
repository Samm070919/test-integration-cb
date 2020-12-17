package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInCardStatusBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.money_in.models.PagoPse;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class MoneyInByCardStatusFragment extends AbstractMoney {

    FragmentMoneyInCardStatusBinding binding;
    private PagoPse pagoPse;
    private int idPagoPse;
    MontoViewController mvImporte;
    private InfoCompraKitCoF info;

    private static final String TAG = MoneyInByCardStatusFragment.class.getSimpleName();

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        info= (InfoCompraKitCoF) bundle.getSerializable(MoneyInByCardFragment.OBJECT_NAME);
        binding = FragmentMoneyInCardStatusBinding.inflate(inflater, container, false);

        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        idPagoPse = preferenceManager.getIdPagoPse();

        mvImporte = binding.lImporte.findViewById(R.id.mv_importe);
        mvImporte.setNewTextSizeIndividualMonto(22f, 34f, 22f);
        TextView tvFooterMonto = binding.lImporte.findViewById(R.id.tv_subtitle);
        tvFooterMonto.setText(getResources().getString(R.string.monto_rellenado_tarjeta));

        mvImporte.setMonto(Utilities.getFormatedImportObject(BigDecimal.valueOf(info.getImportesaldo())));
        binding.tvMontoCobrado.setText(Utilities.getFormatedImport(BigDecimal.valueOf(info.getAmount())));
        binding.tvMontoComision.setText(Utilities.getFormatedImport(BigDecimal.valueOf(info.getComision())));
        binding.tvAutorization.setText(info.getAuthorizationcode());
        binding.tvFolio.setText(info.getTransactionid());
        binding.tvReferencia.setText("Depósito a Cuenta");
        binding.tvLastcardNumbers.setText("**** " + info.getNumero4ultimos());

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha = inputFormat.parse(info.getCreated());
            binding.tvFecha.setText(UtilsDate.getDateNewFormat(fecha));
        } catch (ParseException e) {
            LOGGER.throwing(TAG,1, e, e.getMessage());
        }

        binding.btnTerminar.setOnClickListener(view -> ((HomeActivity)getActivity()).restauraHome());
        binding.ivCompartirRellenaSaldoTarjeta.setOnClickListener(view ->{
            takeScreenshot();
        });
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    private void takeScreenshot() {
        if (Utilities.takeScreenshot(getActivity().getWindow().getDecorView().getRootView())) {
            showDialog(R.layout.layout_money_in_screenshot, new ModalFragment.CommonDialogFragmentCallBack() {
                @Override
                public void onAccept() {
                    //none
                }
            }, getString(R.string.txt_close));
        }
    }
}
