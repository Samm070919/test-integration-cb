package com.pagatodoholdings.posandroid.secciones.sales.returnmoney;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentDevolucionBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.BtnCheckHolder;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeDataSingleton;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

import java.util.ArrayList;
import java.util.List;

import static com.pagatodo.sigmalib.NivelMenu.SEGUNDO_NIVEL;
import static com.pagatodo.sigmalib.NivelMenu.TERCER_NIVEL;

public class DevolucionFragment extends PciSalesFragmentSupport<FragmentDevolucionBinding> {
    private String TAG = DevolucionFragment.class.getSimpleName();

    public static DevolucionFragment newInstance() {
        return new DevolucionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_devolucion, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.setTitle(resources.getString(R.string.devolución));
        activity.resetQposManager();
        setUpOptions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                activity.finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpOptions() {
        final int minMenuCount = 3;
        List<Menu> categorias = SigmaBdManager.getCategorias(throwable -> Log.e(TAG, throwable.getMessage()));
        List<Menu> items = SigmaBdManager.getItemsPorNivel(SEGUNDO_NIVEL, categorias.get(2), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener menú segundo nivel"));
        items = SigmaBdManager.getItemsPorNivel(TERCER_NIVEL, items.get(0), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener menú tercer nivel"));

        if (items.size() > (minMenuCount + 1)) {
            List<BtnCheckHolder.ItemProdData> listItems = getlistMenuItems(items, minMenuCount);
            SimpleDevolucionAdapter adapter = new SimpleDevolucionAdapter(listItems, menu -> {
                ChargeDataSingleton.getInstance().setMenu(menu);
                onContinue();
            });
            binding.rvListOpciones.setAdapter(adapter);
            binding.rvListOpciones.setHasFixedSize(true);
        } else {
            final Menu devMenu = items.get(minMenuCount);
            binding.btnConfirmar.setOnClickListener(v -> {
                ChargeDataSingleton.getInstance().setMenu(devMenu);
                onContinue();
            });
            binding.btnConfirmar.setVisibility(View.VISIBLE);
            binding.rvListOpciones.setVisibility(View.GONE);
        }
    }

    private List<BtnCheckHolder.ItemProdData> getlistMenuItems(List<Menu> items, final int minMenuCount) {
        List<BtnCheckHolder.ItemProdData> listItems = new ArrayList<>();

        for (int i = minMenuCount; i < items.size(); i++) {
            listItems.add(new BtnCheckHolder.ItemProdData(items.get(i), SigmaBdManager.obtenIcono(items.get(i), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener el icono del producto"))));
        }

        return listItems;
    }

    private void onContinue() {
        String referencia = binding.etReferencia.getText().toString().trim();

        if (referencia.length() > 0) {
            ChargeDataSingleton.getInstance().setReferenciaDevolucion(referencia);
            activity.getRouter().showCardReader();
        } else {
            Toast.makeText(getContext(), "La Referencia no puede venir vacía", Toast.LENGTH_SHORT).show();
        }
    }
}
