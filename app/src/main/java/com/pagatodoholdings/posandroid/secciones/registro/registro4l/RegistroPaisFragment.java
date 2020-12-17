package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.RecyclerPaisAdapter;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroPaisBinding;
import com.pagatodoholdings.posandroid.secciones.registro.ConfigPaisListener;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroPaisesInteractor;
import com.pagatodoholdings.posandroid.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistroPaisFragment extends AbstractStepFragment {

    private static final String TAG = RegistroPaisFragment.class.getSimpleName();
    private FragmentRegistroPaisBinding binding;
    private RecyclerPaisAdapter adapter;
    private ArrayList<RegistroPaises> paises;
    private Map<Country, Boolean> paisesGenerales = new EnumMap<>(Country.class);
    private ConfigPaisListener configPaisListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ConfigPaisListener) {
            configPaisListener = (ConfigPaisListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_pais, container, false);
        paisesGenerales.clear();
        binding.paisRecylcer.setHasFixedSize(true);
        binding.paisRecylcer.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registroPaises();
    }

    private void initAdapter(final Map<Country, Boolean> filtroPaises) {
        if (adapter == null) {
            adapter = new RecyclerPaisAdapter(filtroPaises);
            binding.paisRecylcer.setAdapter(adapter);
            adapter.setOnItemClickListener(country -> {
                if (configPaisListener != null) {
                    configPaisListener.onFetchUrlForCountry(country);
                }
            });
        }


    }

    public static androidx.fragment.app.Fragment newInstance() {
        final RegistroPaisFragment fragment = new RegistroPaisFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private  void  registroPaises (){
       final RegistroPaisesInteractor paisesInteractor =  new RegistroPaisesInteractor();
       paisesInteractor.registroPaises(new RetrofitListener() {

           @Override
           public void onSuccess(Object responce) {
               paises = (ArrayList<RegistroPaises>) responce;
              Logger.LOGGER.fine( TAG, Arrays.toString(paises.toArray()) );
               initAdapterFilter(paises);

           }

           @Override
           public void showMessage(String message) {
               //NOT IMPLEMENTED
           }

           @Override
           public void onFailure(Throwable throwableError) {
               Logger.LOGGER.throwing(TAG,1,throwableError,throwableError.getMessage());
           }
       });
     }

    void   initAdapterFilter( final ArrayList<RegistroPaises> paises ) {

        for (RegistroPaises pais : paises) {
            switch (pais.getCodigo()) {

                case "076": //Brazil
                    paisesGenerales.put(Country.BRASIL, pais.getActivo());
                    break;
                case "032":
                    paisesGenerales.put(Country.ARGENTINA, pais.getActivo());
                    break;
                case "604":
                    paisesGenerales.put(Country.PERU, pais.getActivo());
                    break;
                case "152":
                    paisesGenerales.put(Country.CHILE, pais.getActivo());
                    break;
                case "170":
                    paisesGenerales.put(Country.COLOMBIA, pais.getActivo());
                    break;
                case "218":
                    paisesGenerales.put(Country.ECUADOR, pais.getActivo());
                    break;
                case "724":
                    paisesGenerales.put(Country.ESPANA, pais.getActivo());
                    break;
                case "484":
                    paisesGenerales.put(Country.MEXICO, pais.getActivo());
                    break;
                default:
                    paisesGenerales.put(Country.COLOMBIA, true);
            }
        }

        initAdapter(paisesGenerales);

    }
}