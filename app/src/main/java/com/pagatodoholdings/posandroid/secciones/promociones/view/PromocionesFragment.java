package com.pagatodoholdings.posandroid.secciones.promociones.view;


import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentPromocionesBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.promociones.Documento;
import com.pagatodoholdings.posandroid.secciones.promociones.Mensaje;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionInterface;
import com.pagatodoholdings.posandroid.secciones.promociones.presenter.PromocionPresenter;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class PromocionesFragment extends AbstractFragment implements PromocionInterface.View {

    private FragmentPromocionesBinding binding;
    private HomeActivity homeActivity;
    private LayoutInflater layoutInflater;
    private PromocionInterface.Presenter presenter;
    ArrayList<Documento> listaPromociones = new ArrayList<>();
    AdapterPromociones customAdapterPromo;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        homeActivity = (HomeActivity) getActivity();
        layoutInflater=inflater;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_promociones, container, false);
        presenter = new PromocionPresenter(this);
        loadCoupons();
        binding.ivClose.setOnClickListener(v -> cerrar());
    }

    private void loadCoupons(){

        presenter.couponsMessages(
                listaPromociones,
                getString(
                        R.string.firestore_cupones,
                        BuildConfig.APPLICATION_ID.substring(
                                0,
                                BuildConfig.APPLICATION_ID.length() - 3
                        ),
                        MposApplication.getInstance().getDatosLogin().getCliente().getPais(),
                        MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod()),
                getString(
                        R.string.firestore_inbox,
                        BuildConfig.APPLICATION_ID.substring(
                                0,
                                BuildConfig.APPLICATION_ID.length() - 3
                        ),
                        MposApplication.getInstance().getDatosLogin().getCliente().getPais(),
                        MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod()
                )
        );
    }

    @Override
    public void showListCouponsMessages(ArrayList<Documento> listaPromociones) {
        customAdapterPromo = new AdapterPromociones(layoutInflater, listaPromociones, homeActivity);
        binding.lvListaPromociones.setAdapter(customAdapterPromo);
    }

    private void cerrar() {
        binding.fragmentPromociones.setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().remove(PromocionesFragment.this).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
           homeActivity.regresaMenu();
        }
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implements
    }
}
