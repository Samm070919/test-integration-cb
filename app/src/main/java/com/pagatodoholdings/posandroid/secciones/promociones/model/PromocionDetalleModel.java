package com.pagatodoholdings.posandroid.secciones.promociones.model;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.pagatodoholdings.posandroid.secciones.promociones.Promocion;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionDetalleInterface;
import com.pagatodoholdings.posandroid.utils.Utilities;

public class PromocionDetalleModel implements PromocionDetalleInterface.Model {

    private PromocionDetalleInterface.Presenter presenter;

    public PromocionDetalleModel(PromocionDetalleInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showCode(Promocion promocion) {
        Bitmap code;
        if (promocion.getTipoCodigo() != null) {
            switch (promocion.getTipoCodigo()) {
                case "QR":
                    code = Utilities.encodeAsBitmap(promocion.getCodigo(), BarcodeFormat.QR_CODE, 800, 800);
                    presenter.showCode(code);
                    break;
                case "CB":
                    code = Utilities.encodeAsBitmap(promocion.getCodigo(), BarcodeFormat.CODE_128, 250, 100);
                    presenter.showCode(code);
                    break;
                case "NS":
                    code = null;
                    presenter.showCode(code);
                    break;
                default:
                    break;
            }
        }
    }
}
