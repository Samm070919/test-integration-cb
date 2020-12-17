package com.pagatodoholdings.posandroid.secciones.sales;

import android.content.Intent;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodo.qposlib.QPosManager;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.PciSaleActivityBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.manager.DongleTransactionManager;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.sales.binding.TitleFrag;
import com.pagatodoholdings.posandroid.secciones.sales.insertcard.InsertCardFragment;
import com.pagatodoholdings.posandroid.secciones.ticket.EmailTicketFragment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pagatodoholdings.posandroid.utils.Constantes.RESULT_CODE_ADQ;

public class PciSalesActivity extends AbstractActivity implements PciSalesListener {//NOSONAR

    public static final String DEVOLUCION = "DEVOLUCION";
    public static final String COBRO = "COBRO";
    private static final String TYPE_ID = "TYPE_ID";
    private PciSaleActivityBinding binding;
    private PciSaleRouter router;
    private int contenedor;
    private DongleTransactionManager manager;
    private boolean isBackEnabled = false;

    public static Intent createIntent(HomeActivity activity, String typeOperation) {
        Intent intent = new Intent(activity, PciSalesActivity.class);
        intent.putExtra(TYPE_ID, typeOperation);
        return intent;
    }

    @Override
    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.pci_sale_activity);
        contenedor = binding.containerPci.getId();
        router = new PciSaleRouter(this);
        botonBackDeshabilitado = false;
        setSupportActionBar(this.binding.includeToolbar.toolbarHead);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getExtras() != null) {
            String s = Objects.requireNonNull(getIntent().getExtras().getString(TYPE_ID));
            if (DEVOLUCION.equals(s)) {
                router.showReturnMoney();
            } else if (COBRO.equals(s)) {
                router.showBreakdown();
            }

        }
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        final Fragment actualFragment = getSupportFragmentManager().findFragmentById(contenedor);
        if (actualFragment instanceof InsertCardFragment) {
            if (isBackEnabled()) {
                finish();
            }
        } else if (actualFragment instanceof EmailTicketFragment) {
            setResult(RESULT_CODE_ADQ);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void setTitleFrag(TitleFrag titleFrag) {
        this.binding.setTitleFrags(titleFrag);
        if (titleFrag.getTitleFrag().isEmpty()) {
            this.binding.includeToolbar.titleFrag.setVisibility(View.GONE);
        } else {
            this.binding.includeToolbar.titleFrag.setVisibility(View.VISIBLE);
        }
        if (titleFrag.getSubtitle().isEmpty()) {
            this.binding.includeToolbar.subtitleFrag.setVisibility(View.GONE);
        } else {
            this.binding.includeToolbar.subtitleFrag.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void hideBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void hideToolbar() {
        this.binding.includeToolbar.toolbarHead.setVisibility(View.GONE);
    }

    @Override
    public void showToolbar() {
        this.binding.includeToolbar.toolbarHead.setVisibility(View.VISIBLE);
    }

    public void resetQposManager() {

        QPosManager qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
        if (qPosManager != null) {
            qPosManager.resetQPOS();
            qPosManager.closeCommunication();
        }
    }

    public PciSaleRouter getRouter() {
        return router;
    }

    public void hideAllToolbar() {
        setTitleFrag(TitleFrag.getInstance("", ""));
        hideToolbar();
        hideBackButton();
    }

    public boolean isBackEnabled() {
        return isBackEnabled;
    }

    public void setBackEnabled(boolean backEnabled) {
        isBackEnabled = backEnabled;
    }

    public DongleTransactionManager getManager() {
        return manager;
    }

    public void setManager(DongleTransactionManager manager) {
        this.manager = manager;
    }
}
