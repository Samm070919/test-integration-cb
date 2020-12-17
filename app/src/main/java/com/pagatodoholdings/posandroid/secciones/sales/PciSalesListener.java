package com.pagatodoholdings.posandroid.secciones.sales;

import com.pagatodoholdings.posandroid.secciones.sales.binding.TitleFrag;

public interface PciSalesListener {

    void showBackButton();
    void hideBackButton();
    void hideToolbar();
    void showToolbar();
    void setTitleFrag(TitleFrag titleFrag);
}
