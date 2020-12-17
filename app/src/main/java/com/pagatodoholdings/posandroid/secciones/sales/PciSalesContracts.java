package com.pagatodoholdings.posandroid.secciones.sales;

import androidx.fragment.app.Fragment;

public class PciSalesContracts {

    interface Router {

        void showBreakdown();
        void showCardReader();
        void showInsertCard(Fragment fragment);
        void showEmailTicket(Fragment fragment);
        void showDues();
        void showProcessing();
        void showSuccessfulSale();
        void showFirm();
        void showReturnMoney();
    }
}
