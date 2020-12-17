package com.pagatodoholdings.posandroid.secciones.promociones.presenter;

import com.pagatodoholdings.posandroid.secciones.promociones.Documento;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionInterface;
import com.pagatodoholdings.posandroid.secciones.promociones.model.PromocionModel;

import java.util.ArrayList;

public class PromocionPresenter implements PromocionInterface.Presenter {

    private PromocionInterface.View view;
    private PromocionInterface.Model model;

    public PromocionPresenter(PromocionInterface.View view) {
        this.view = view;
        this.model = new PromocionModel(this);
    }

    @Override
    public void showListCouponsMessages(ArrayList<Documento> listaPromociones) {
        if(view!=null){
            view.showListCouponsMessages(listaPromociones);
        }
    }


    @Override
    public void couponsMessages(ArrayList<Documento> listaPromociones, String pathCoupon, String pathMessage) {
        if(view!=null){
            model.getListCouponsMessages(listaPromociones, pathCoupon,pathMessage);
        }
    }
}
