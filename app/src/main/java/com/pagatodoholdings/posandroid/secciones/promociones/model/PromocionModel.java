package com.pagatodoholdings.posandroid.secciones.promociones.model;


import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pagatodoholdings.posandroid.secciones.promociones.Documento;
import com.pagatodoholdings.posandroid.secciones.promociones.Mensaje;
import com.pagatodoholdings.posandroid.secciones.promociones.Promocion;
import com.pagatodoholdings.posandroid.secciones.promociones.PromocionInterface;

import java.util.ArrayList;
import java.util.Iterator;

public class PromocionModel implements PromocionInterface.Model {

    private PromocionInterface.Presenter presenter;

    public PromocionModel(PromocionInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getListCoupons(final ArrayList<Documento> listaPromociones, String path) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection(path);
        query.addSnapshotListener((snapshots, exc) -> {
            if (exc == null && snapshots != null) {
                Iterator var4 = snapshots.getDocumentChanges().iterator();
                while(var4.hasNext()) {
                    DocumentChange documentChange = (DocumentChange)var4.next();
                    Promocion promocion = parseCoupon(documentChange);

                    if(listaPromociones.contains(promocion)){
                        listaPromociones.remove(promocion);
                    }

                    listaPromociones.add(promocion);
                    DocumentChange.Type type = documentChange.getType();
                    if (type == DocumentChange.Type.REMOVED) {
                        listaPromociones.remove(promocion);
                    }
                }
                presenter.showListCouponsMessages(listaPromociones);
            }
        });
    }

    @Override
    public void getListMessages(ArrayList<Documento> listaMensajes, String path) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection(path);
        query.addSnapshotListener((snapshots, exc) -> {
            if (exc == null && snapshots != null) {
                Iterator var4 = snapshots.getDocumentChanges().iterator();
                while(var4.hasNext()) {
                    DocumentChange documentChange = (DocumentChange)var4.next();
                    Mensaje mensaje = parseMessage(documentChange);

                    if(listaMensajes.contains(mensaje)){
                        listaMensajes.remove(mensaje);
                    }
                    mensaje.setInbox(true);
                    listaMensajes.add(mensaje);
                    DocumentChange.Type type = documentChange.getType();
                    if (type == DocumentChange.Type.REMOVED) {
                        listaMensajes.remove(mensaje);
                    }
                }
                presenter.showListCouponsMessages(listaMensajes);
            }
        });
    }

    @Override
    public void getListCouponsMessages(ArrayList<Documento> listaPromociones, String pathCoupon, String pathMessage) {

            if(pathCoupon != null && !pathCoupon.isEmpty()){
               getListCoupons(listaPromociones,pathCoupon);
            }

            if(pathMessage !=null && !pathMessage.isEmpty()){
                getListMessages(listaPromociones,pathMessage);
            }

            presenter.showListCouponsMessages(listaPromociones);
    }

    @Override
    public Mensaje parseMessage(DocumentChange documentChange) {
        Mensaje mensaje = documentChange.getDocument().toObject(Mensaje.class);
        mensaje.setId(documentChange.getDocument().getId());
        return mensaje;
    }

    @Override
    public Promocion parseCoupon(DocumentChange documentChange) {
        Promocion promocion = documentChange.getDocument().toObject(Promocion.class);
        promocion.setId(documentChange.getDocument().getId());
        return promocion;
    }
}
