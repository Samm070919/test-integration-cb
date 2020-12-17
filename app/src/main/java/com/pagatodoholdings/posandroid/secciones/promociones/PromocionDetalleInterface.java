package com.pagatodoholdings.posandroid.secciones.promociones;

import android.graphics.Bitmap;

public interface PromocionDetalleInterface {

    interface View{
        void showCode(Bitmap bitmap);
    }

    interface Presenter{
        void showCode(Bitmap bitmap);
        void getCode(Promocion promocion);
    }

    interface Model{
        void showCode(Promocion promocion);
    }

}
