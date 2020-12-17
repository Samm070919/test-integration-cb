package com.pagatodoholdings.posandroid.secciones.kit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentKitDatosEnvioEmptyBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.NivelBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNivel1;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNivel2;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;

import java.util.ArrayList;
import java.util.List;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class KitDatosEnvioEmptyFragment extends AbstractStepFragment {

    final String TAG = this.getClass().getSimpleName();
    FragmentKitDatosEnvioEmptyBinding binding;
    private AbstractActivity homeActivity;
    private List<String> nivelesList;
    private RegistroInteractor registroInteractor;
    private DatosNegocio datosNegocio;
    private DatosCompraKit datosCompraKit;
    private boolean isRegisterDirection = true;

    private boolean isProgressDialogShowing;
    private byte dialogRequestCount;
    private CapturedDatosEnvio capturedDatosEnvioListener;


    public KitDatosEnvioEmptyFragment(CapturedDatosEnvio capturedDatosEnvioListener) {
        this.capturedDatosEnvioListener = capturedDatosEnvioListener;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        datosCompraKit = new DatosCompraKit();
        homeActivity = (AbstractActivity) getActivity();
        registroInteractor = new RegistroInteractor();
        datosNegocio = MposApplication.getInstance().getDatosNegocio();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kit_datos_envio_empty, container, false);

        cargarNiveles();

        binding.ivClose.setOnClickListener(v -> closeFragment(KitDatosEnvioEmptyFragment.this));

        binding.spinnerNivel0.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long rowId) {
                if (nivelesList.size() > 2) {
                    cargarNivel1(((GenericAdaptadorSpinner<NivelBean>) adapterView.getAdapter()).getItem(index).getNivelcod());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                /* No usada actualmente*/
            }
        });

        binding.spinnerNivel1.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long rowId) {
                if (nivelesList.size() > 2) {
                    cargarNivel2(((GenericAdaptadorSpinner<NivelBean>) adapterView.getAdapter()).getItem(index).getNivelcod());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                /* No usada actualmente*/
            }
        });

        binding.spinnerNivel2.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long rowId) {
                hideProgressDialog(); // Needs this extra call to ensure the dialogRequestCount is set to zero
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                /* No usada actualmente*/
            }
        });

        binding.btnConfirmar.setOnClickListener(v -> {
            if (obtenerDatosEnvio()) {
                cargarSolicitarKitFragment();
            }
        });
    }

    private void loadAddress() {
        /*
        *   [
            "Region",           Nivel0
            "Departamento",     Nivel1
            "Ciudad"            Nivel2
            ]
        * */

        if (datosNegocio != null) {
            cargarNivel0();
            binding.spinnerNivel0.obtenTvTitulo().setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreyLigth));
            binding.spinnerNivel1.obtenTvTitulo().setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreyLigth));
            binding.spinnerNivel2.obtenTvTitulo().setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreyLigth));
            binding.tilDireccion.getEditText().setText(datosNegocio.getDireccion());

        }
    }


    void cargarNiveles() {
        homeActivity.muestraProgressDialog(getString(R.string.cargando));
        dialogRequestCount = 1;
        showProgressDialog();

        registroInteractor.getNiveles(new RetrofitListener<List<String>>() {
            @Override
            public void onSuccess(final List<String> tList) {
                nivelesList = tList;
                if (tList != null && !tList.isEmpty()) {
                    //Obtenemos la lista de títulos Nivel1 y Nivel2
                    hideProgressDialog();
                    cargarNivel0();
                    loadAddress();
                }
            }

            @Override
            public void onFailure(final Throwable thr) {
                LOGGER.throwing(TAG, 1, thr, "Error al cargar combo niveles");
                dialogRequestCount = 0;
                hideProgressDialog();
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.error_conexion), () -> closeFragment(KitDatosEnvioEmptyFragment.this));
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    void cargarNivel0() {
        showProgressDialog();

        binding.spinnerNivel0.obtenTvTitulo().setText(nivelesList.get(0));

        registroInteractor.cargarNivel0(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                NivelBean nivelBean = new NivelBean(0, "");
                tList.add(0,nivelBean);
                binding.spinnerNivel0.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(homeActivity.getApplicationContext(), tList));
                binding.spinnerNivel0.obtenSpinnerContenido().setSelection(0);

                hideProgressDialog();
            }

            @Override
            public void onFailure(final Throwable thr) {
                LOGGER.throwing(TAG, 4, thr, "Error al cargar combo nivel 2");
                dialogRequestCount = 0;
                hideProgressDialog();
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.error_conexion), () -> closeFragment(KitDatosEnvioEmptyFragment.this));
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    void cargarNivel1(final int idNivel0) {
        showProgressDialog();

        binding.spinnerNivel1.obtenTvTitulo().setText(nivelesList.get(1));
        registroInteractor.getNivel1(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                NivelBean nivelBean = new NivelBean(0, "");
                tList.add(0,nivelBean);
                binding.spinnerNivel1.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(homeActivity.getApplicationContext(), tList));
                binding.spinnerNivel1.obtenSpinnerContenido().setSelection(0);

                hideProgressDialog();
            }

            @Override
            public void onFailure(final Throwable thr) {
                LOGGER.throwing(TAG, 3, thr, "Error al cargar combo nivel 1");
                dialogRequestCount = 0;
                hideProgressDialog();
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.error_conexion), () -> closeFragment(KitDatosEnvioEmptyFragment.this));
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel0);
    }

    void cargarNivel2(final int idNivel1) {
        showProgressDialog();

        binding.spinnerNivel2.obtenTvTitulo().setText(nivelesList.get(2));
        homeActivity.muestraProgressDialog(getString(R.string.cargando));
        registroInteractor.getNivel2(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                NivelBean nivelBean = new NivelBean(0, "");
                tList.add(0,nivelBean);
                binding.spinnerNivel2.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(homeActivity.getApplicationContext(), tList));

                int spinnerIndex = 0;
                binding.spinnerNivel2.obtenSpinnerContenido().setSelection(spinnerIndex);

                hideProgressDialog();
            }

            @Override
            public void onFailure(final Throwable thr) {
                LOGGER.throwing(TAG, 4, thr, "Error al cargar combo nivel 2");
                dialogRequestCount = 0;
                hideProgressDialog();

                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.error_conexion), () -> closeFragment(KitDatosEnvioEmptyFragment.this));
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel1);
    }

    private boolean obtenerDatosEnvio() {
        if (nivelesList.size() > 0) {
            final NivelBean nivel1Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel0.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel0.obtenSpinnerContenido().getSelectedItemPosition());

            if(nivel1Bean.getDescripcion().isEmpty()){
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty,nivelesList.get(0)), () -> {
                });
                return false;
            }
            datosCompraKit.addNivel0ToDireccion(nivel1Bean.getDescripcion());
        }

        if (nivelesList.size() > 1) {
            final NivelBean nivel1Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel1.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel1.obtenSpinnerContenido().getSelectedItemPosition());

            DatosNivel1 nivel1 = new DatosNivel1();
            nivel1.setNivelcod(nivel1Bean.getNivelcod());
            nivel1.setDescripcion(nivel1Bean.getDescripcion());
            datosCompraKit.setNivel1(nivel1.getNivelcod());
            if(nivel1.getDescripcion().isEmpty()){
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty,nivelesList.get(1)), () -> {
                });
                return false;
            }
            datosCompraKit.addNivel1ToDireccion(nivel1Bean.getDescripcion());
        }

        if (nivelesList.size() > 2) {
            final NivelBean nivel2Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel2.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel2.obtenSpinnerContenido().getSelectedItemPosition());

            DatosNivel2 nivel2 = new DatosNivel2();
            nivel2.setNivelcod(nivel2Bean.getNivelcod());
            nivel2.setDescripcion(nivel2Bean.getDescripcion());
            datosCompraKit.setNivel2(nivel2.getNivelcod());
            if(nivel2.getDescripcion().isEmpty()) {
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty, nivelesList.get(2)), () -> {
                });
                return false;
            }
            datosCompraKit.addNivel2ToDireccion(nivel2Bean.getDescripcion());
        }

        //Obtener Dirección
        if (binding.tilDireccion.getEditText().getText().toString() == null ||
                binding.tilDireccion.getEditText().getText().toString().length() < 1) {
            homeActivity.despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.error_direccion), () -> {
            });
            return false;
            //Obtener Código Postal
        } else {
            datosCompraKit.setDireccion(binding.tilDireccion.getEditText().getText().toString());
            datosCompraKit.setCodpostal("000000");
            datosCompraKit.addNivel3ToDireccion(datosCompraKit.getDireccion());
        }
        return true;
    }

    private void cargarSolicitarKitFragment() {
        capturedDatosEnvioListener.updatedData(datosCompraKit);
    }


    protected void closeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else if (homeActivity instanceof HomeActivity) {
            ((HomeActivity) homeActivity).regresaMenu();
        } else {
            homeActivity.onBackPressed();
        }
    }

    protected void showProgressDialog() {
        if (!isProgressDialogShowing) {
            homeActivity.muestraProgressDialog("Cargando");
            isProgressDialogShowing = true;
        } else if (dialogRequestCount < 0) {
            dialogRequestCount = 0;
        }

        dialogRequestCount++;
    }

    protected void hideProgressDialog() {
        dialogRequestCount--;

        if (dialogRequestCount <= 0) {
            if (isProgressDialogShowing) {
                homeActivity.ocultaProgressDialog();
                isProgressDialogShowing = false;
            } else if (dialogRequestCount < 0) {
                dialogRequestCount = 0;
            }
        }
    }

}