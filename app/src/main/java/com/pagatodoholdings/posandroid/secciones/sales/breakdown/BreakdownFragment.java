package com.pagatodoholdings.posandroid.secciones.sales.breakdown;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.EmvManager;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.PerfilEmvApp;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.SigmaUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.BreakdownFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref;
import com.pagatodoholdings.posandroid.secciones.acquiring.binding.TotalRow;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeDataSingleton;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;
import com.pagatodoholdings.posandroid.secciones.sales.binding.BreakdownData;
import com.pagatodoholdings.posandroid.secciones.sales.binding.TitleFrag;
import com.pagatodoholdings.posandroid.secciones.sales.calculate.BreakdownAdditons;
import com.pagatodoholdings.posandroid.utils.Logger;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.IMPUESTOS_SELECTED;
import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.PROPINA_SELECTED;

public class BreakdownFragment extends PciSalesFragmentSupport<BreakdownFragmentBinding> {

    private SettingsPref pref;
    private String[] itemsImpuestos;
    private String[] itemsPropinas;
    private BreakdownAdditons breakdownAdditons;
    private Productos productos;
    private NumberFormat numberFormat = SigmaBdManager.getNumberFormat(new OnFailureListener.BasicOnFailureListener(getClass().getSimpleName(), "Error al formatear saldo"));


    public static BreakdownFragment newInstance() {
        return new BreakdownFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = SettingsPref.getInstance();
        breakdownAdditons = BreakdownAdditons.getInstance(clearBigInput(ChargeDataSingleton.getInstance().getMoney()));
        itemsImpuestos = new String[]{"0%", "5%", "19%"};
        itemsPropinas = new String[]{"0%", "8%", "10%", "15%"};
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.breakdown_fragment, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.resetQposManager();
        String nameRazonSocial = "";
        if (MposApplication.getInstance().getDatosNegocio() != null) {
            nameRazonSocial = MposApplication.getInstance().getDatosNegocio().getRs();
        }
        String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(getClass().getSimpleName(), "Error al obtener parametro fijo"));

        binding.moneyText.setSymbol(parametroMoneda);
        binding.moneyText.setNumberFormat(numberFormat);

        binding.include3.moneyInputSimple3.setSymbol(parametroMoneda);
        binding.include3.moneyInputSimple3.setNumberFormat(numberFormat);

        binding.include4.moneyInputSimple3.setSymbol(parametroMoneda);
        binding.include4.moneyInputSimple3.setNumberFormat(numberFormat);

        binding.include6.moneyInputSimple3.setSymbol(parametroMoneda);
        binding.include6.moneyInputSimple3.setNumberFormat(numberFormat);

        binding.setTotalRowList(getTotalist());
        activity.showToolbar();
        activity.showBackButton();
        binding.titleRs.setText(nameRazonSocial);
        activity.setTitleFrag(TitleFrag.getInstance(resources.getString(R.string.title_frag_cobro_tarjeta), ""));

        if (findProfile() && productos.getPerfilEmv() != null) {
            final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(productos.getPerfilEmv(),
                    new OnFailureListener.BasicOnFailureListener("BREAKDOWNFRAGMENT", "ERROR AL CONSULTAR PERFIL"));

            if (fullPerfil != null && fullPerfil.getPerfilesEmv().getInPropina() == 0) {
                binding.include6.ctPropina.setVisibility(View.GONE);
                pref.saveData(PROPINA_SELECTED, "0%");
            } else {
                binding.include6.spinner.setAdapter(setPropAdapter());
                binding.include6.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Validar si el producto tiene propinas

                        binding.include6.setTotalRow(new TotalRow(getResources().getString(R.string.propinas) + " " + itemsPropinas[position],
                                breakdownAdditons.getPropina(itemsPropinas[position]), true));

                        BigDecimal montoTotal = breakdownAdditons.getMontotal(itemsPropinas[position]);
                        binding.moneyText.setText(numberFormat.format(montoTotal), true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //No implementation
                    }
                });
                binding.include6.linearLayout6.setOnClickListener(v -> binding.include6.spinner.performClick());
            }

            if (fullPerfil != null && fullPerfil.getPerfilesEmv().getInImpuesto() == 0) {
                binding.include4.ctPropina.setVisibility(View.GONE);
                pref.saveData(IMPUESTOS_SELECTED, "0%");
            } else {
                binding.include4.spinner.setAdapter(setImpuesAdapter());
                binding.include4.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        binding.include4.setTotalRow(new TotalRow(getResources().getString(R.string.impuestos) + " " + itemsImpuestos[position],
                                breakdownAdditons.getImpuestos(itemsImpuestos[position]), true));

                        BigDecimal subtotal = breakdownAdditons.getSubTotal(itemsImpuestos[position]);
                        binding.include3.setTotalRow(new TotalRow(getResources().getString(R.string.subtotal) + " ", numberFormat.format(subtotal), false));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //No implementation
                    }
                });
                binding.include4.linearLayout6.setOnClickListener(v -> binding.include4.spinner.performClick());
            }
        }

        binding.btnCharge.setOnClickListener(v -> {
            validateGratification();
            BreakdownData breakdownData = new BreakdownData(breakdownAdditons.getBigMontoTotal(),
                    breakdownAdditons.getBigImpuesto(),
                    breakdownAdditons.getBigPropina());
            ChargeDataSingleton.getInstance().setBreakdownData(breakdownData);

            // Todo: For debugging purposes
//            String regexString = "(?<=\\d),(?=\\d)|\\$";
//            double propina = clearInput(binding.include6.moneyInputSimple3.getText().toString().replaceAll(regexString, ""));
//            double total = clearInput(binding.moneyText.getText().toString().replaceAll(regexString, ""));
//            double impuesto = clearInput(Objects.requireNonNull(binding.include4.moneyInputSimple3.getText()).toString().replaceAll(regexString, ""));
            activity.getRouter().showCardReader();
        });

        switch (pref.getDataPer(IMPUESTOS_SELECTED)) {
            case "0%":
                binding.include4.spinner.setSelection(0);
                break;
            case "5%":
                binding.include4.spinner.setSelection(1);
                break;
            case "19%":
                binding.include4.spinner.setSelection(2);
                break;
            default:
                binding.include4.spinner.setSelection(0);
                break;
        }
        switch (pref.getDataPer(PROPINA_SELECTED)) {
            case "8%":
                binding.include6.spinner.setSelection(1);
                break;
            case "10%":
                binding.include6.spinner.setSelection(2);
                break;
            case "15%":
                binding.include6.spinner.setSelection(3);
                break;
            default:
                binding.include6.spinner.setSelection(0);
                break;
        }

        BigDecimal montoTotal = breakdownAdditons.getMontotal(pref.getDataPer(PROPINA_SELECTED));
        binding.moneyText.setText(numberFormat.format(montoTotal), true);
        validateGratification();
    }

    private Double clearInput(final String input) {
        final NumberFormat dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener("", "Error al obtener numberFormat"));
        return SigmaUtil.cleanImporteInput(input, dbnumberformat).doubleValue();
    }

    private BigDecimal clearBigInput(final String input) {
        final NumberFormat dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener("", "Error al obtener numberFormat"));
        return SigmaUtil.cleanImporteInput(input, dbnumberformat);
    }

    public List<TotalRow> getTotalist() {
        ArrayList<TotalRow> listRow = new ArrayList<>();

        TotalRow impRow = new TotalRow("IVA " + pref.getDataPer(IMPUESTOS_SELECTED),
                breakdownAdditons.getImpuestos(pref.getDataPer(IMPUESTOS_SELECTED)), true);
        TotalRow proRow = new TotalRow("Propina " + pref.getDataPer(PROPINA_SELECTED), breakdownAdditons.getPropina(pref.getDataPer(PROPINA_SELECTED)), true);
        BigDecimal bigDecimal = breakdownAdditons.getSubTotal(pref.getDataPer(IMPUESTOS_SELECTED));
        listRow.add(new TotalRow("Subtotal", numberFormat.format(bigDecimal), false));
        listRow.add(impRow);
        listRow.add(proRow);

        return listRow;
    }

    private ArrayAdapter setImpuesAdapter() {
        return new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, itemsImpuestos);
    }

    private ArrayAdapter setPropAdapter() {
        return new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, itemsPropinas);
    }

    private boolean findProfile() {
        List<Operaciones> operaciones = SigmaBdManager.getOperacionesVisiblesPorProducto(ChargeDataSingleton.getInstance().getMenu().getProducto(),
                new OnFailureListener.BasicOnFailureListener(Objects.requireNonNull(getActivity()).getClass().getSimpleName(), "Error al consultar operaciones"));
        productos = SigmaBdManager.getProducto(operaciones.get(0), throwable -> Logger.LOGGER.throwing(throwable.getMessage(), 1, throwable,
                activity.getString(R.string.error_con_bd)));

        if (operaciones != null && productos != null) {
            return true;

        }
        Toast.makeText(getContext(), "El perfil en la Base de Datos no fue encontrado", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void validateGratification() {
        if (findProfile() && productos.getPerfilEmv() != null) {
            final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(productos.getPerfilEmv(),
                    new OnFailureListener.BasicOnFailureListener("BREAKDOWNFRAGMENT", "ERROR AL CONSULTAR PERFIL"));

            if (fullPerfil != null && fullPerfil.getPerfilesEmv().getInPropina() != 0) {
                binding.include6.setTotalRow(new TotalRow("Propina " + pref.getDataPer(PROPINA_SELECTED), pref.getDataPer(PROPINA_SELECTED), true));
            } else {
                binding.include6.ctPropina.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "El producto no contiene un Perfil", Toast.LENGTH_SHORT).show();
            binding.include6.ctPropina.setVisibility(View.GONE);
        }
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
}