package com.pagatodoholdings.posandroid.secciones.promociones.view;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentPromocionesDetalleBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.promociones.Promocion;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionDetalleInterface;
import com.pagatodoholdings.posandroid.secciones.promociones.presenter.PromocionDetallePresenter;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromocionesDetalleFragment extends AbstractFragment implements PromocionDetalleInterface.View {


    private FragmentPromocionesDetalleBinding binding;
    private HomeActivity homeActivity;
    private Promocion promocionDetalle;

    public void setPromocionDetalle(Promocion promocionDetalle) {
        this.promocionDetalle = promocionDetalle;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }



    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        homeActivity = (HomeActivity) getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_promociones_detalle, container, false);

        PromocionDetalleInterface.Presenter presenter;
        presenter = new PromocionDetallePresenter(this);
        presenter.getCode(promocionDetalle);

        if(promocionDetalle.getImagen()!=null && !promocionDetalle.getImagen().isEmpty()) {
            Picasso.with(homeActivity.getApplicationContext()).load(promocionDetalle.getImagen()).into(binding.ivDetalle);
        }
        binding.tvTitlePromocion.setText(promocionDetalle.getMensaje() + "\n" + getString(R.string.validez_cupones) + " " + promocionDetalle.getFechaFin());

        binding.ivDetalle.setOnClickListener(v -> {
            if (promocionDetalle.getTipoCodigo().equals("QR")) {
                binding.ivDetalle.animate().alpha(0f).setDuration(1000);
                binding.ivDetalle.setClickable(false);
                binding.llContainerCode.animate().alpha(1f).setDuration(1000);

                binding.llContainerCode.setVisibility(View.VISIBLE);
                binding.ivDetalle.setVisibility(View.GONE);
            }
        });

        binding.codeContainer.setOnClickListener(v -> {
                binding.llContainerCode.animate().alpha(0f).setDuration(1000);
                binding.ivDetalle.animate().alpha(1f).setDuration(1000);
                binding.ivDetalle.setClickable(true);
                binding.ivDetalle.setVisibility(View.VISIBLE);
                binding.llContainerCode.setVisibility(View.GONE);
        });

        binding.ivClose.setOnClickListener(v ->
                cerrar(PromocionesDetalleFragment.this));
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }

    private void cerrar(Fragment fragment) {
            homeActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            if (getParentFragment() != null) {
                ((DialogFragment) getParentFragment()).dismiss();
            } else {
                homeActivity.cargarFragmentPromociones();
            }
    }

    @Override
    public void showCode(Bitmap bitmap) {

        switch (promocionDetalle.getTipoCodigo()) {
            case "QR":
                binding.codeContainer.setImageBitmap(bitmap);
                break;
            case "CB":
                binding.ivCodebar.setImageBitmap(bitmap);
                binding.ivCodebar.setVisibility(View.VISIBLE);
                binding.tvBodyCode.setText(promocionDetalle.getCodigo());
                binding.tvBodyCode.setVisibility(View.VISIBLE);
                break;
            case "NS":
                binding.tvBodyCode.setText(getString(R.string.serie_title_cupones) + " " + promocionDetalle.getCodigo());
                binding.tvBodyCode.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
