package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.CustomProgressLoader;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuInfoBancariaAndCardsBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.money_in.MoneyInCashBankDetailFragment;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.CuentaBancariaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosCuentaBancaria;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosService;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCoFInteractor;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.CustomDialog;
import com.pagatodoholdings.posandroid.utils.SwipeController;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class ConfigMenuInfoBancariaAndCardsFragment extends AbstractConfigMenu {

    private FragmentConfigMenuInfoBancariaAndCardsBinding binding;
    private MisDatosService misDatosService;

    private List<DatosTarjetaCoFBean> cardList;
    public static CardsAdapter adapter;
    private DatosCuentaBancaria datosCuenta;
    private int montoVerificarCuenta = 0;

    private List<DatosCuentaBancaria> accountList;
    private AccountAdapter adapterAccounts;

    private CustomProgressLoader loader;
    private List<CuentaBancariaBean> mCuentaBancariaBeans;

    private static final String paramCountry = "param-country";
    private static RecyclerView rvCards;
    private static LinearLayout llAddCard;

    public static ConfigMenuInfoBancariaAndCardsFragment newInstance(int country) {
        Bundle args = new Bundle();
        args.putInt(paramCountry, country);
        ConfigMenuInfoBancariaAndCardsFragment fragment = new ConfigMenuInfoBancariaAndCardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ConfigMenuInfoBancariaAndCardsFragment() {
        // Required empty public constructor
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        misDatosService = new MisDatosService();
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_config_menu_info_bancaria_and_cards, container, false);
        cargarDatosCuenta();

        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuInfoBancariaAndCardsFragment.this));
        rvCards = binding.rvTarjetas;
        llAddCard = binding.llAddCard;
        loader = new CustomProgressLoader(requireActivity());

        binding.cbRellenaTarjeta.setChecked(false);
        binding.cbRellenaTarjeta.setEnabled(false);
        binding.cbRellenaCuenta.setChecked(false);
        binding.cbRellenaCuenta.setEnabled(false);

        //bindListeners();
        getCards();
    }

    private void getCards() {
        getListener().muestraProgressDialog(getResources().getString(R.string.cta_bca_load));
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();
        String usuario = MposApplication.getInstance().getDatosLogin().getCliente().getEmail();
        final String tpvcod = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        final String token = MposApplication.getInstance().getDatosLogin().getToken();
        RegistroCoFInteractor registroRetrofit = new RegistroCoFInteractor(url);
        registroRetrofit.getTarjetas(token, tpvcod, pais, usuario, new RetrofitListener<List<DatosTarjetaCoFBean>>() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                getListener().ocultaProgressDialog();
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
                cargarDatosCuenta();
            }

            @Override
            public void onSuccess(List<DatosTarjetaCoFBean> result)
            {
                if(result.size() > 0) {
                    binding.tvAgregarTarjeta.setVisibility(View.INVISIBLE);
                    binding.llAddCard.setVisibility(View.GONE);
                    binding.tvAgregarTarjeta.setEnabled(false);
                    cardList = new ArrayList<>();
                    cardList.add(result.get(0));// = result;
                    adapter = new CardsAdapter(cardList, (vista, position) -> loadFragmentEditCard(cardList.get(position)), getContext());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                    binding.rvTarjetas.setHasFixedSize(true);
                    binding.rvTarjetas.setLayoutManager(layoutManager);
                    binding.rvTarjetas.setAdapter(adapter);


                    new ItemTouchHelper(itemTouchHelperCards).attachToRecyclerView(binding.rvTarjetas);

                }else{
                    binding.llAddCard.setVisibility(View.VISIBLE);
                    binding.tvAgregarTarjeta.setVisibility(View.VISIBLE);
                    binding.tvAgregarTarjeta.setEnabled(true);
                }
                getListener().ocultaProgressDialog();
                binding.tvAgregarTarjeta.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(),RegistroCoF.class);
                    intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_ADD_CARD_BY_MENU);
                    startActivityForResult(intent, 2);// Activity is started with requestCode 2
                });

                binding.llAddCard.setOnClickListener(v -> {
                    Intent intent=new Intent(getActivity(),RegistroCoF.class);
                    intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_ADD_CARD_BY_MENU);
                    startActivityForResult(intent, 2);// Activity is started with requestCode 2
                });

                cargarDatosCuenta();
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCards = new SwipeController(0, ItemTouchHelper.LEFT, (viewHolder, direction, position) -> {
        CustomDialog.showDialog(getContext(), R.drawable.ic_ilustracion_desvincular_lector, "SI", "NO",
                getResources().getString(R.string.menu_config_elimina_cuenta_title),
                getResources().getString(R.string.menu_config_elimina_tarjeta_content),
                new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                    @Override
                    public void onCancel() {
                        //loadMiCuenta(ConfigMenuInfoBancariaAndCardsFragment.this);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAccept() {
                        deleteCard(cardList.get(position).getIdtarjeta(), position);
                        showDeleteDialog(
                                getString(R.string.delete_card),
                                getString(R.string.we_have_deleted_your_card)
                        );
                    }
                });

    });

    private void deleteCard(int idtarjeta, int position) {
        getListener().muestraProgressDialog(getResources().getString(R.string.cta_bca_load));
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();
        String usuario = MposApplication.getInstance().getDatosLogin().getCliente().getEmail();
        final String tpvcod = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        final String token = MposApplication.getInstance().getDatosLogin().getToken();
        RegistroCoFInteractor registroRetrofit = new RegistroCoFInteractor(url);
        registroRetrofit.deleteTarjetas(idtarjeta, token, tpvcod, pais, usuario, new RetrofitListener<ResponseBody>() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                getListener().ocultaProgressDialog();
                getListener().ocultaProgressDialog();
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ResponseBody result)
            {
                adapter.deleteItem(position);
                binding.rvTarjetas.setVisibility(View.GONE);
                binding.llAddCard.setVisibility(View.VISIBLE);
                binding.tvAgregarTarjeta.setVisibility(View.VISIBLE);
                getListener().ocultaProgressDialog();
            }
        });
    }


    private void loadFragmentEditCard(DatosTarjetaCoFBean datosTarjetaCoFBean) {

        final MoneyInCashBankDetailFragment fragment;

    }

    private void loadFragmentEditAccount(DatosCuentaBancaria datosCuentaBancaria) {
        final ConfigMenuInfoBancariaFragment fragmentMicuenta = new ConfigMenuInfoBancariaFragment(datosCuentaBancaria, new AccountUpdate() {
            @Override
            public void accountUpdated() {
                cargarDatosCuenta();
            }
        });

        fragmentMicuenta.setListener((HomeActivity)getActivity());
        ((HomeActivity)getActivity()).cargarFragmentCuentaBancaria(fragmentMicuenta);

    }


     void cargarDatosCuenta() {
        getListener().muestraProgressDialog(getResources().getString(R.string.cta_bca_load));
        misDatosService.getDatosCuentaBancaria(
                new RetrofitListener<DatosCuentaBancaria>() {
                    @Override
                    public void showMessage(String message) {
                        //No implementation
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.throwing(TAG, 1, throwable, getResources().getString(R.string.cta_bca_title_error_consult_data));
                        getListener().ocultaProgressDialog();
                        getListener().despliegaModal(true, false, getResources().getString(R.string.generic_error),
                                "No se pudieron cargar los Datos Bancarios", () ->
                                        loadMiCuenta(ConfigMenuInfoBancariaAndCardsFragment.this));
                    }

                    @Override
                    public void onSuccess(DatosCuentaBancaria datosCuentaBancaria) {
                        if (datosCuentaBancaria != null && datosCuentaBancaria.getCtabancaria() != null) {
                            binding.llAddAccount.setVisibility(View.GONE);
                            binding.rvCuentas.setVisibility(View.VISIBLE);
                            binding.tvAgregarCuenta.setVisibility(View.INVISIBLE);
                            binding.tvAgregarCuenta.setEnabled(true);
                            accountList = new ArrayList<>();
                            accountList.add(datosCuentaBancaria);
                            binding.rvCuentas.setAdapter(adapterAccounts);
                            adapterAccounts = new AccountAdapter(accountList, (vista, position) -> loadFragmentEditAccount(accountList.get(position)));
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                            binding.rvCuentas.setHasFixedSize(true);
                            binding.rvCuentas.setLayoutManager(layoutManager);
                            binding.rvCuentas.setAdapter(adapterAccounts);

                            ItemTouchHelper.SimpleCallback itemTouchHelper = new SwipeController(0, ItemTouchHelper.LEFT, (viewHolder, direction, position) -> {
                                DatosCuentaBancaria deleteItem = adapterAccounts.getAccountList().get(position);
                                CustomDialog.showDialog(getContext(), R.drawable.ic_ilustracion_desvincular_lector, "SI", "NO",
                                        getResources().getString(R.string.menu_config_elimina_cuenta_title),
                                        getResources().getString(R.string.menu_config_elimina_cuenta_content),
                                        new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                            @Override
                                            public void onCancel() {
                                                //loadMiCuenta(ConfigMenuInfoBancariaAndCardsFragment.this);
                                                adapterAccounts.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onAccept() {
                                                adapterAccounts.deleteItem(position);
                                                binding.rvCuentas.setVisibility(View.GONE);
                                                binding.llAddAccount.setVisibility(View.VISIBLE);
                                                binding.tvAgregarCuenta.setVisibility(View.VISIBLE);
                                                showDeleteDialog(
                                                        getString(
                                                                R.string.bank_account_deleted
                                                        ),
                                                        getString(R.string.we_have_deleted_your_bank_account)
                                                );
                                            }
                                        });

                            });
                            new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvCuentas);

                        } else {
                           binding.tvAgregarCuenta.setEnabled(true);
                           binding.llAddAccount.setVisibility(View.VISIBLE);
                           binding.tvAgregarCuenta.setVisibility(View.VISIBLE);
                        }
                        binding.tvAgregarCuenta.setOnClickListener(v -> {
                            final ConfigMenuInfoBancariaFragment fragmentMicuenta = new ConfigMenuInfoBancariaFragment(datosCuentaBancaria, new AccountUpdate() {
                                @Override
                                public void accountUpdated() {
                                    cargarDatosCuenta();
                                }
                            });

                            fragmentMicuenta.setListener((HomeActivity)getActivity());
                            ((HomeActivity)getActivity()).cargarFragmentCuentaBancaria(fragmentMicuenta);
                        });
                        binding.llAddAccount.setOnClickListener(v -> {
                            final ConfigMenuInfoBancariaFragment fragmentMicuenta = new ConfigMenuInfoBancariaFragment(datosCuentaBancaria, new AccountUpdate() {
                                @Override
                                public void accountUpdated() {
                                    cargarDatosCuenta();
                                }
                            });

                            fragmentMicuenta.setListener((HomeActivity)getActivity());
                            ((HomeActivity)getActivity()).cargarFragmentCuentaBancaria(fragmentMicuenta);
                        });
                        getListener().ocultaProgressDialog();
                    }
                }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());
    }



    public void muestraProgressDialog(final String mensaje) {
        if (loader != null) {
            loader.setMessage(mensaje);
            loader.show();
        }
    }

    public void ocultaProgressDialog() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    public static void updateAdapter(DatosTarjetaCoFBean datosTarjetaCoFBean){
        adapter.getCardList().add(datosTarjetaCoFBean);
        llAddCard.setVisibility(View.GONE);
        rvCards.setVisibility(View.VISIBLE);
        rvCards.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constantes.REQUEST_ADD_CARD_BY_MENU) {
            if (data != null && data.hasExtra("DatosTarjeta")) {
                Bundle bundle = data.getBundleExtra("DatosTarjeta");
                DatosTarjetaCoFBean datosTarjetaCoFBean = (DatosTarjetaCoFBean) bundle.get("DatosTarjeta");
                binding.rvTarjetas.setVisibility(View.VISIBLE);
                binding.llAddCard.setVisibility(View.GONE);
                binding.tvAgregarTarjeta.setVisibility(View.INVISIBLE);
                if(adapter == null) {
                    cardList = new ArrayList<>();
                    cardList.add(datosTarjetaCoFBean);// = result;
                    adapter = new CardsAdapter(cardList, (vista, position) -> loadFragmentEditCard(cardList.get(position)), getContext());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                    binding.rvTarjetas.setHasFixedSize(true);
                    binding.rvTarjetas.setLayoutManager(layoutManager);
                    binding.rvTarjetas.setAdapter(adapter);
                    new ItemTouchHelper(itemTouchHelperCards).attachToRecyclerView(binding.rvTarjetas);
                }else {
                    adapter.getCardList().add(datosTarjetaCoFBean);
                    adapter.notifyDataSetChanged();
                }
            }
        }else if (resultCode == Constantes.REQUEST_ADD_ACCOUNT_BY_MENU) {
            if (data != null && data.hasExtra("DatosCuenta")) {
                Bundle bundle = data.getBundleExtra("DatosCuenta");
                DatosCuentaBancaria datosTarjetaCoFBean = (DatosCuentaBancaria) bundle.get("DatosCuenta");
                accountList.add(0, datosTarjetaCoFBean);
                adapterAccounts.notifyDataSetChanged();
                binding.rvCuentas.setVisibility(View.VISIBLE);
                binding.llAddAccount.setVisibility(View.GONE);
                binding.tvAgregarCuenta.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected boolean isTomandoBack() {
        loadMiCuenta(ConfigMenuInfoBancariaAndCardsFragment.this);
        return true;
    }

    private void showDeleteDialog(String title, String body){
        InfoCuentaBancariaDeleteDialogs dialog = new InfoCuentaBancariaDeleteDialogs(title, body);
        FragmentManager  fm = getActivity().getSupportFragmentManager();
        dialog.show(fm, "Delete dialog");
    }
}