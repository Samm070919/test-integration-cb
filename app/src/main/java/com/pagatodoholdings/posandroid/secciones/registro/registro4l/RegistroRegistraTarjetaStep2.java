package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroRegistraTarjetaStep2Binding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCoF;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CCNameFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CCNumberFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CCSecureCodeFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CCValidityFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CardBackFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CardFrontFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.CoFResultFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.AltaTarjetaCoFBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCoFInteractor;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardUtils;
import com.pagatodoholdings.posandroid.utils.cardutils.ViewPagerAdapter;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.sql.SQLException;


import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;

public class RegistroRegistraTarjetaStep2 extends AbstractStepFragment implements FragmentManager.OnBackStackChangedListener {

    public CardFrontFragment cardFrontFragment;
    public CardBackFragment cardBackFragment;

    RegistroCoF activity;
    CCNumberFragment numberFragment;
    CCNameFragment nameFragment;
    CCValidityFragment validityFragment;
    CoFResultFragment CofResult;
    public CCSecureCodeFragment secureCodeFragment;
    FragmentRegistroRegistraTarjetaStep2Binding mBinding;
    private boolean allowBack = true;
    DialogFragment progressFragment;
    private int idActivityForResult;


    int total_item;
    boolean backTrack = false;

    private boolean mShowingBack = false;

    String cardNumber, cardCVV, cardValidity, cardName;

    public RegistroRegistraTarjetaStep2() {
        // Required empty public constructor
    }

    private static final String TAG = RegistroRegistraTarjetaStep2.class.getSimpleName();

    public RegistroRegistraTarjetaStep2(RegistroCoF activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    public RegistroRegistraTarjetaStep2(RegistroCoF activity, int isForResult) {
        // Required empty public constructor
        this.activity = activity;
        this.idActivityForResult = isForResult;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_registra_tarjeta_step2, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardFrontFragment = new CardFrontFragment();
        cardBackFragment = new CardBackFragment(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        if (savedInstanceState == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cardFrontFragment).commit();

        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getChildFragmentManager().addOnBackStackChangedListener(this);

        mBinding.viewpager.setOffscreenPageLimit(5);
        setupViewPager(mBinding.viewpager);


        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == total_item)
                    mBinding.btnNext.setText("Finalizar");
                else {
                    mBinding.btnNext.setText("Continuar");
                }
                Log.d("track", "onPageSelected: " + position);

                if (position == total_item) {
                    flipCard();
                    backTrack = true;
                } else if (position == total_item - 1 && backTrack) {
                    flipCard();
                    backTrack = false;
                }

                if(position== 0){
                    mBinding.btnBack.setVisibility(View.GONE);
                }else{
                    mBinding.btnBack.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mBinding.viewpager.getCurrentItem();
                if (pos < total_item) {
                    mBinding.viewpager.setCurrentItem(pos + 1);
                } else {
                    checkEntries();
                }

            }
        });
        mBinding.btnBack.setVisibility(View.GONE);
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mBinding.viewpager.getCurrentItem();
                if (pos <= total_item) {
                    mBinding.viewpager.setCurrentItem(pos - 1);
                }
            }
        });
    }

    private void cerrar() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        }

    }

    private void reiniciarContador() {

        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }


    public void checkEntries() {
        cardName = nameFragment.getName();
        cardNumber = numberFragment.getCardNumber();
        cardValidity = validityFragment.getValidity();
        cardCVV = secureCodeFragment.getValue();

        if (TextUtils.isEmpty(cardNumber) || !CreditCardUtils.isValid(cardNumber.replace(" ", ""))) {
            Toast.makeText(getActivity(), "Ingresa un Número de Tarjeta Valido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardName)) {
            Toast.makeText(getActivity(), "Ingresa un Nombre Valido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardValidity) || !CreditCardUtils.isValidDate(cardValidity)) {
            Toast.makeText(getActivity(), "Ingresa una Vigencia Valida", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardCVV) || cardCVV.length() < 3) {
            Toast.makeText(getActivity(), "Ingresa un Número de Segruridad Valido", Toast.LENGTH_SHORT).show();
        } else {

            iniciaSesionSigma();

        }

    }

    void registraTarjeta() {
        mBinding.bottomToolBar.setVisibility(View.GONE);
        DatosSesion datosSesion = ApiData.APIDATA.getDatosSesion();
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();
        String usuario = MposApplication.getInstance().getDatosLogin().getCliente().getEmail();
        final String tpvcod = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        final String token = MposApplication.getInstance().getDatosLogin().getToken();
        cardValidity = cardValidity.replace("/", "");
        cardNumber = cardNumber.replace(" ", "");
        final AltaTarjetaCoFBean bean = new AltaTarjetaCoFBean(
                cardCVV,
                cardValidity,
                cardName.toUpperCase(),
                cardNumber.trim()
        );
        RegistroCoFInteractor registroRetrofit = new RegistroCoFInteractor(url);
        registroRetrofit.registroTarjetaCoF(bean, token, tpvcod, pais, usuario, new RetrofitListener<DatosTarjetaCoFBean>() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
                progressFragment.dismiss();
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                progressFragment.dismiss();
                showNext(false, null);
            }

            @Override
            public void onSuccess(DatosTarjetaCoFBean result) {
                progressFragment.dismiss();
                hideProgressDialog();
                showNext(true, result );
            }
        });
    }

    void showNext(boolean success, DatosTarjetaCoFBean datosTarjetaCoFBean) {
        CofResult = new CoFResultFragment(success, idActivityForResult, datosTarjetaCoFBean);
        mBinding.viewpager.setVisibility(View.GONE);
        mBinding.containerCof.setVisibility(View.VISIBLE);
        mBinding.bottomToolBar.setVisibility(View.GONE);
        activity.hiddeToolBars();
        setAllowBack(false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_cof, CofResult);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        numberFragment = new CCNumberFragment(this);
        nameFragment = new CCNameFragment(this);
        validityFragment = new CCValidityFragment(this);
        secureCodeFragment = new CCSecureCodeFragment(this);
        RegistroRecibePagosTarjetaFragment recibeTarjeta = new RegistroRecibePagosTarjetaFragment();
        adapter.addFragment(numberFragment);
        adapter.addFragment(nameFragment);
        adapter.addFragment(validityFragment);
        adapter.addFragment(secureCodeFragment);

        total_item = adapter.getCount() - 1;
        viewPager.setAdapter(adapter);

    }

    private void flipCard() {
        if (mShowingBack) {
            getChildFragmentManager().popBackStack();
            return;
        }
        mShowingBack = true;

        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardBackFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onBackPressed() {
        int pos = mBinding.viewpager.getCurrentItem();
        if (pos > 0) {
            mBinding.viewpager.setCurrentItem(pos - 1);
        }
    }

    public void nextClick() {
        mBinding.btnNext.performClick();
    }

    public boolean isAllowBack() {
        return allowBack;
    }

    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    private void iniciaSesionSigma() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        progressFragment = RegistroRegistraTarjetaProcessing.newInstance();
        progressFragment.setCancelable(false);
        progressFragment.show(ft, "dialog");
        DatosSesion datosSesion = null;
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            return;
        }

        try {
            datosSesion = build();
        } catch (SQLException exe) {
            LOGGER.throwing(TAG, 1, exe, exe.getMessage());
        }
        registraTarjeta();
    }


    @Override
    public void onBackStackChanged() {
        mShowingBack = (getChildFragmentManager().getBackStackEntryCount() > 0);
    }
}
