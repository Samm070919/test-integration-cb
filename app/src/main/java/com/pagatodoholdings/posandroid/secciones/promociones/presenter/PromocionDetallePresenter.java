package com.pagatodoholdings.posandroid.secciones.promociones.presenter;

import android.graphics.Bitmap;

import com.pagatodoholdings.posandroid.secciones.promociones.Promocion;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionDetalleInterface;
import com.pagatodoholdings.posandroid.secciones.promociones.model.PromocionDetalleModel;

public class PromocionDetallePresenter implements PromocionDetalleInterface.Presenter {

    private PromocionDetalleInterface.View view;
    private PromocionDetalleInterface.Model model;

    public PromocionDetallePresenter(PromocionDetalleInterface.View view) {
        this.view = view;
        this.model = new PromocionDetalleModel(this);
    }


    @Override
    public void showCode(Bitmap code) {
        if(view!=null){
            view.showCode(code);
        }
    }


    @Override
    public void getCode(Promocion promocion) {
        if(view!=null)
        {
            model.showCode(promocion);
        }
    }

}
