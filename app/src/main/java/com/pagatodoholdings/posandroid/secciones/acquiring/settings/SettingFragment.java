package com.pagatodoholdings.posandroid.secciones.acquiring.settings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.SettingsFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref;
import com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.ArrowSettingsBtn;
import com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.IvaSwitchSettings;
import com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.PropinaSwitchSettings;
import com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.SettingsBtn;
import com.pagatodoholdings.posandroid.secciones.acquiring.support.TemplateFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ES_IMPUESTO;
import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ES_PROPINA;
import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.IMPUESTOS_SELECTED;
import static com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.PROPINA_SELECTED;
import static com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.SwitchSettingsBtn.collapse;
import static com.pagatodoholdings.posandroid.secciones.acquiring.binding.settings.SwitchSettingsBtn.expand;


public class SettingFragment extends TemplateFragment<SettingsFragmentBinding> {

    @SuppressLint("StaticFieldLeak")
    private  View perIva;

    @SuppressLint("StaticFieldLeak")
    private  View perPro;

    private SettingsPref pref;

    public static SettingFragment newInstance(){
        return new SettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = SettingsPref.getInstance();
        homeActivity.setBotonBackDeshabilitado(false);
    }

    @Override
    public void setListener(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        homeActivity.setBotonBackDeshabilitado(true);
        binding.toolbar.setNavigationIcon(R.drawable.ic_icono_back);
        homeActivity.setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(homeActivity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

        perIva = binding.include.includePer.constRoot;
        perPro = binding.include2.includePer.constRoot;
        binding.include.textPer.setText(pref.getData(IMPUESTOS_SELECTED));
        binding.include2.textPer.setText(pref.getData(PROPINA_SELECTED));

        if (!pref.getDataBoolean(ES_IMPUESTO,false)){
            collapseImpuestos();
        } else {
            expanseImuestos();
        }

        if (!pref.getDataBoolean(ES_PROPINA,false)){
            collapsePropina();
        } else {
            expansePropina();
        }

        binding.setSettingsBtn(setSwitch());
        binding.include8.setSettingsBtn(new ArrowSettingsBtn(new SettingsBtn.ArrowData(getResources().getString(R.string.devolución))));
        binding.include8.containerOption.setOnClickListener(v -> homeActivity.getRouter().showReturnMoney());
        binding.toolbar.setNavigationOnClickListener(v -> closeFragment(this));
        binding.include.incLabels.setVisibility(pref.getDataBoolean(ES_IMPUESTO, false) ? View.VISIBLE : View.INVISIBLE);
        binding.include2.incLabels.setVisibility(pref.getDataBoolean(ES_PROPINA, false) ? View.VISIBLE : View.INVISIBLE);
        binding.include.switchSet.setChecked(pref.getDataBoolean(ES_IMPUESTO, false));
        binding.include2.switchSet.setChecked(pref.getDataBoolean(ES_PROPINA, false));

        initPaintImpuesto(pref.getDataPer(IMPUESTOS_SELECTED));
        initPaintPropina(pref.getDataPer(PROPINA_SELECTED));
    }

    private void collapseImpuestos(){
        collapse(perIva);
        binding.include.incLabels.setVisibility(View.GONE);

    }

    private void collapsePropina(){
        collapse(perPro);
        binding.include2.incLabels.setVisibility(View.GONE);

    }

    private void expanseImuestos(){
        expand(perIva);
        binding.include.incLabels.setVisibility(View.VISIBLE);

    }

    private void expansePropina(){
        expand(perPro);
        binding.include2.incLabels.setVisibility(View.VISIBLE);
    }

    public List<SettingsBtn> setSwitch() {
        ArrayList<SettingsBtn> list = new ArrayList<>();
        list.add(IvaSwitchSettings.getInstance(Objects.requireNonNull(getContext()), v -> {
            switch (v.getId()) {
                case R.id.card_h:
                    binding.include.textPer.setText(binding.include.includePer.textH.getText());
                    pref.saveData(IMPUESTOS_SELECTED, "0%");

                    setSelected(true, binding.include.includePer.cardH, binding.include.includePer.textH);
                    setSelected(false, binding.include.includePer.cardM, binding.include.includePer.textM);
                    setSelected(false, binding.include.includePer.cardL, binding.include.includePer.textL);
                    break;
                case R.id.card_m:
                    binding.include.textPer.setText(binding.include.includePer.textM.getText());
                    pref.saveData(IMPUESTOS_SELECTED, "5%");

                    setSelected(false, binding.include.includePer.cardH, binding.include.includePer.textH);
                    setSelected(true, binding.include.includePer.cardM, binding.include.includePer.textM);
                    setSelected(false, binding.include.includePer.cardL, binding.include.includePer.textL);
                    break;
                case R.id.card_l:
                    binding.include.textPer.setText(binding.include.includePer.textL.getText());
                    pref.saveData(IMPUESTOS_SELECTED, "19%");

                    setSelected(false, binding.include.includePer.cardH, binding.include.includePer.textH);
                    setSelected(false, binding.include.includePer.cardM, binding.include.includePer.textM);
                    setSelected(true, binding.include.includePer.cardL, binding.include.includePer.textL);
                    break;

                default:
                    break;
            }
        }, (buttonView, isChecked) -> expanseOrCollapseImpuestos(isChecked)));

        list.add(PropinaSwitchSettings.getInstance(getContext(), v -> {
                switch (v.getId()) {
                    case R.id.card_h:
                        binding.include2.textPer.setText(binding.include2.includePer.textH.getText());
                        pref.saveData(PROPINA_SELECTED, "8%");

                        setSelected(true, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                        setSelected(false, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                        setSelected(false, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                        break;
                    case R.id.card_m:
                        binding.include2.textPer.setText(binding.include2.includePer.textM.getText());
                        pref.saveData(PROPINA_SELECTED, "10%");

                        setSelected(false, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                        setSelected(true, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                        setSelected(false, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                        break;
                    case R.id.card_l:
                        binding.include2.textPer.setText(binding.include2.includePer.textL.getText());
                        pref.saveData(PROPINA_SELECTED, "15%");

                        setSelected(false, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                        setSelected(false, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                        setSelected(true, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                        break;

                    default:
                        break;
                }
        }, (buttonView, isChecked) -> expanseOrCollapsePropina(isChecked)));
        return list;
    }

    private void setSelected(boolean isSelected, CardView cardView, TextView textView) {
        if (isSelected) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            textView.setTextColor(Color.WHITE);
        } else {
            cardView.setCardBackgroundColor(Color.WHITE);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void expanseOrCollapsePropina(boolean isChecked) {
        if (isChecked) {
            if (perPro != null) expansePropina();
            binding.include2.textPer.setText("8%");
            pref.saveData(PROPINA_SELECTED, "8%");
        } else {
            if (perPro != null) collapsePropina();
            pref.saveData(PROPINA_SELECTED, "0%");
        }
        pref.saveData(ES_PROPINA, isChecked);
        initPaintPropina(pref.getDataPer(PROPINA_SELECTED));
    }

    private void initPaintPropina(String s) {
        switch (s) {
            case "8%":
                setSelected(true, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                setSelected(false, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                setSelected(false, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                break;
            case "10%":
                setSelected(false, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                setSelected(true, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                setSelected(false, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                break;
            case "15%":
                setSelected(false, binding.include2.includePer.cardH, binding.include2.includePer.textH);
                setSelected(false, binding.include2.includePer.cardM, binding.include2.includePer.textM);
                setSelected(true, binding.include2.includePer.cardL, binding.include2.includePer.textL);
                break;
            default:
                break;
        }
    }

    private void expanseOrCollapseImpuestos(boolean isChecked) {
        if (isChecked) {
            if (perIva != null) expanseImuestos();
            binding.include.textPer.setText("0%");
            pref.saveData(IMPUESTOS_SELECTED, "0%");
        } else {
            if (perIva != null) collapseImpuestos();
            pref.saveData(IMPUESTOS_SELECTED, "0%");
        }
        pref.saveData(ES_IMPUESTO, isChecked);
        initPaintImpuesto(pref.getDataPer(IMPUESTOS_SELECTED));
    }

    private void initPaintImpuesto(String s) {
        switch (s) {
            case "0%":
                setSelected(true, binding.include.includePer.cardH, binding.include.includePer.textH);
                setSelected(false, binding.include.includePer.cardM, binding.include.includePer.textM);
                setSelected(false, binding.include.includePer.cardL, binding.include.includePer.textL);
                break;
            case "5%":
                setSelected(false, binding.include.includePer.cardH, binding.include.includePer.textH);
                setSelected(true, binding.include.includePer.cardM, binding.include.includePer.textM);
                setSelected(false, binding.include.includePer.cardL, binding.include.includePer.textL);
                break;
            case "19%":
                setSelected(false, binding.include.includePer.cardH, binding.include.includePer.textH);
                setSelected(false, binding.include.includePer.cardM, binding.include.includePer.textM);
                setSelected(true, binding.include.includePer.cardL, binding.include.includePer.textL);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }


    protected void closeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            homeActivity.regresaMenu();
        }
    }
}
