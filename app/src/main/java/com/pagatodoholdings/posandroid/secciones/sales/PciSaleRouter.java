package com.pagatodoholdings.posandroid.secciones.sales;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.acquiring.views.Direction;
import com.pagatodoholdings.posandroid.secciones.firma.FirmaActivity;
import com.pagatodoholdings.posandroid.secciones.sales.breakdown.BreakdownFragment;
import com.pagatodoholdings.posandroid.secciones.sales.cardreader.CardReaderFragment;
import com.pagatodoholdings.posandroid.secciones.sales.dues.DuesFragment;
import com.pagatodoholdings.posandroid.secciones.sales.insertcard.InsertCardFragment;
import com.pagatodoholdings.posandroid.secciones.sales.processing.ProcessingFragment;
import com.pagatodoholdings.posandroid.secciones.sales.returnmoney.DevolucionFragment;
import com.pagatodoholdings.posandroid.secciones.sales.successfully.SuccessfulSaleFragment;

public class PciSaleRouter implements PciSalesContracts.Router{

    private PciSalesActivity activity;
    private int idContanier;

    PciSaleRouter(PciSalesActivity activity) {
        this.activity = activity;
        this.idContanier = R.id.container_pci;
    }

    @Override
    public void showBreakdown() {
        activity.loadFragment(BreakdownFragment.newInstance(),idContanier, Direction.NONE,true);
    }

    @Override
    public void showCardReader() {
        loadFragment(CardReaderFragment.newInstance());
    }

    @Override
    public void showInsertCard(Fragment fragment) {
        loadFragment(new InsertCardFragment());
    }

    @Override
    public void showDues() {
        loadFragment(DuesFragment.newInstance());
    }

    @Override
    public void showProcessing() {
        loadFragment(ProcessingFragment.newInstance());
    }

    @Override
    public void showSuccessfulSale() {
        loadFragment(SuccessfulSaleFragment.newInstance());
    }

    @Override
    public void showEmailTicket(Fragment fragment){
        loadFragment(fragment);
    }

    @Override
    public void showFirm() {
        Intent intent = new Intent(activity, FirmaActivity.class);
        activity.startActivity(intent);
    }

    private void loadFragment(Fragment fragment){
        activity.loadFragment(fragment,idContanier, Direction.FORDWARD,false);
    }

    @Override
    public void showReturnMoney() {
        Fragment fragment = DevolucionFragment.newInstance();
        loadFragment(fragment);
    }

}
