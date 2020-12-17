package com.pagatodoholdings.posandroid.secciones.acquiring.charge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.ChargeFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.support.FragmentKeyBoard;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pagatodo.sigmalib.NivelMenu.SEGUNDO_NIVEL;
import static com.pagatodo.sigmalib.NivelMenu.TERCER_NIVEL;

public class ChargeFragment extends FragmentKeyBoard<ChargeFragmentBinding> implements AdqProdListener, OnFailureListener, SumListener {

    private final String TAG = ChargeFragment.class.getSimpleName();

    public static ChargeFragment newInstance() {
        return new ChargeFragment();
    }

    private AdqProdAdapter adapter;
    private BtnCheckHolder.ItemProdData prod;
    private Menu currentProducto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AdqProdAdapter(this);
        currentProducto = null;
    }

    @Override
    public void setListener(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.charge_fragment, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        Keyboard keyboard = new Keyboard(getActivity(), R.xml.number_pad);
        binding.keyboardView.setKeyboard(keyboard);
        binding.keyboardView.setPreviewEnabled(false);
        binding.keyboardView.setOnKeyboardActionListener(this);
        binding.moneyInputSimple.setEdittextEditable(false);
        binding.moneyInputSimple.getEditText().setText("0");

        List<Menu> categorias = SigmaBdManager.getCategorias(throwable -> Log.e(TAG, throwable.getMessage()));
        List<Menu> items = SigmaBdManager.getItemsPorNivel(SEGUNDO_NIVEL, categorias.get(2), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener menú segundo nivel"));
        items = SigmaBdManager.getItemsPorNivel(TERCER_NIVEL, items.get(0), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener menú tercer nivel"));
        List<BtnCheckHolder.ItemProdData> listItems = getlistMenuItems(items);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.recMenuProd.setLayoutManager(mGridLayoutManager);
        binding.recMenuProd.setHasFixedSize(false);
        binding.recMenuProd.setAdapter(adapter);
        adapter.setListProd(listItems);
        adapter.notifyDataSetChanged();
        binding.imageButtonSetting.setOnClickListener(v -> {
                    homeActivity.cargarFragmentSettings();
//                    Productos producto = SigmaBdManager.getProducto(listItems.get(2).getMenu().getProducto(), new BasicOnFailureListener("hg", ""));
//                    DeferredPaymentPicker deferredPaymentPicker = DeferredPaymentPicker.Companion.newInstance(producto.getPerfilEmv());
//                    deferredPaymentPicker.setCancelable(false);
//                    deferredPaymentPicker.setDeferredInteractionListener(fees -> {});
//                    deferredPaymentPicker.show(getActivity().getSupportFragmentManager(), "DeferredPaymentPicker");
                }
        );
        setEditText(this);
        binding.button.setOnClickListener(v ->
                configureContinueButton());
        if (MposApplication.getInstance().getDatosNegocio() != null) {
            binding.businessName.setText(MposApplication.getInstance().getDatosNegocio().getRs());
            binding.businessName.setVisibility(View.VISIBLE);
        }
    }

    private void configureContinueButton() {
        String money = binding.moneyInputSimple.getEditText().getText().toString().trim();
        BigDecimal bigDecimal = new BigDecimal(money, MathContext.DECIMAL32);

        if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
            showToast("Debes ingresar un monto");
        } else if (currentProducto == null) {
            showToast("Debes seleccionar un tipo de cuenta");
        } else {
            ChargeDataSingleton.getInstance().setMoney(Objects.requireNonNull(money));
            homeActivity.getRouter().showBreakdow();
        }
    }

    private List<BtnCheckHolder.ItemProdData> getlistMenuItems(List<Menu> items) {
        List<BtnCheckHolder.ItemProdData> listItems = new ArrayList<>();
        if (items.size() <= 3) {
            for (Menu m : items) {
                listItems.add(new BtnCheckHolder.ItemProdData(m, SigmaBdManager.obtenIcono(m, this)));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                listItems.add(new BtnCheckHolder.ItemProdData(items.get(i), SigmaBdManager.obtenIcono(items.get(i), this)));
            }
        }
        return listItems;
    }

    public void showDialogBotonAceptar(int layout, String buttonTextVincular, String buttonTextComprar,
                                       ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(homeActivity, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(homeActivity);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final ImageView ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(view1 -> alertDialog.dismiss());
        final BotonClickUnico btnVincular = view.findViewById(R.id.btnVincular);
        btnVincular.setText(buttonTextVincular);
        btnVincular.setTextSize(14);
        btnVincular.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });

        final BotonClickUnico btnComprar = view.findViewById(R.id.btnComprar);
        btnComprar.setText(buttonTextComprar);
        btnComprar.setTextSize(14);
        btnComprar.setOnClickListener(view1 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @Override
    public void onClickItem(BtnCheckHolder.ItemProdData data) {

        if (prod != null) adapter.getHolder(prod).deselectItem();
        adapter.getHolder(data).selectItem();
        prod = data;
        ChargeDataSingleton.getInstance().setMenu(data.getMenu());
        currentProducto = data.getMenu();
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }

    @Override
    public void onSum(String sum) {
        binding.moneyInputSimple.getEditText().setText(sum);
    }

    @Override
    public void onTextSum(String suma) {
        binding.sum.setText(suma);
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    private void showToast(String msj) {
        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
    }
}
