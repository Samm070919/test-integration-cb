package com.pagatodoholdings.posandroid.secciones.promociones;

import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;

public interface PromocionInterface {

    interface View{
        void showListCouponsMessages(ArrayList<Documento> listaPromociones);
    }

    interface Presenter{
        void showListCouponsMessages(ArrayList<Documento> listaPromociones);
        void couponsMessages(ArrayList<Documento> listaPromociones, String pathCoupon, String pathMessage);
    }

    interface Model{
        void getListCoupons(ArrayList<Documento> listaPromociones, String path);
        void getListMessages(ArrayList<Documento> listaMensajes, String path);
        void getListCouponsMessages(ArrayList<Documento> listaPromociones, String pathCoupon, String pathMessage);
        Promocion parseCoupon(DocumentChange documentChange);
        Mensaje parseMessage(DocumentChange documentChange);
    }

}
