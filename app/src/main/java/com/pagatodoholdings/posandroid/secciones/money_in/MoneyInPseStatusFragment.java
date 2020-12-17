package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInPseStatusBinding;
import com.pagatodoholdings.posandroid.secciones.money_in.models.PagoPse;
import com.pagatodoholdings.posandroid.secciones.money_in.retrofit.MoneyInInteractor;
import com.pagatodoholdings.posandroid.utils.UtilsMoneyIn;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class MoneyInPseStatusFragment extends AbstractMoney {

    FragmentMoneyInPseStatusBinding binding;
    private PagoPse pagoPse;
    private int idPagoPse;
    MontoViewController mvImporte;

    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String TAG = MoneyInPseStatusFragment.class.getSimpleName();

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentMoneyInPseStatusBinding.inflate(inflater, container, false);

        initUI();
        reiniciarContador();
        return binding.getRoot();
    }

    private void initUI() {
        idPagoPse = preferenceManager.getIdPagoPse();

        String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();
        mvImporte = binding.lImporte.findViewById(R.id.mv_importe);
        mvImporte.setNewTextSizeIndividualMonto(22f,34f,22f);

        String baseUrl = UtilsMoneyIn.obtenerUrlPais(pais);
        MoneyInInteractor iteractor = new MoneyInInteractor(UtilsMoneyIn.obtenerUrlPais(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()));

        iteractor.searchPagoPse(idPagoPse, new RetrofitListener() {
            @Override
            public void onSuccess(Object result) {
                pagoPse = (PagoPse) result;
                assignPseResponseValues(pagoPse);
            }

            @Override
            public void onFailure(Throwable throwable) {
                //NOT IMPLEMENTED
            }

            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }
        });

        binding.btnTerminar.setOnClickListener(view -> loadMiCuenta(MoneyInPseStatusFragment.this));

    }

    private void assignPseResponseValues(PagoPse pagoPse) {
        try {

            String responseDate = new SimpleDateFormat(DATE_FORMAT).format(new SimpleDateFormat("yyyy-MM-dd").parse(pagoPse.getFechainsert()));
            String estadoPSE = "";
            mvImporte.setMonto(pagoPse.getImporte().toString());
            binding.tvField1.setText(String.valueOf(idPagoPse));
            binding.tvField3.setText(MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());
            binding.tvField4.setText(pagoPse.getEstadopsedesc());
            binding.tvField5.setText(responseDate);

            switch (pagoPse.getEstadopsedesc()) {
                case "Pendiente":
                    estadoPSE = pagoPse.getEstadopsedesc();
                    binding.moneyInPseImage.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_pending));
                    break;
                case "Aprobado":
                    estadoPSE = "Aprobada";
                    binding.moneyInPseImage.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_successful));
                    break;
                case "Rechazado":
                    estadoPSE = "Rechazada";
                    binding.moneyInPseImage.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_rejection));

                    break;
                default:
                    break;
            }
            binding.tvTransactionResultTitle.setText(binding.tvTransactionResultTitle.getText() + estadoPSE);
        } catch (ParseException exe) {
            LOGGER.throwing(TAG,1,exe,exe.getMessage());
        }


    }
}
