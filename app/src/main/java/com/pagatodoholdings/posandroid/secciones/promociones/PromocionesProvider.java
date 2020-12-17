package com.pagatodoholdings.posandroid.secciones.promociones;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pagatodo.notifications.Notificacion;

public class PromocionesProvider {

    public static PromocionesProvider promocionesProvider;
    private Context appContext;
    private int numeroPromos;
    private PromocionCallback promocionCallback;



    private PromocionesProvider(Context context,String path) {
        appContext = context;

        obtenerTotalPromocionesFirestore(path);
    }



    public static PromocionesProvider getInstance(Context context, String path){
        if (promocionesProvider==null) {
            promocionesProvider = new PromocionesProvider(context,path);
        }
        return promocionesProvider;
    }

    public PromocionCallback getPromocionCallback() {
        return promocionCallback;
    }

    public void setPromocionCallback(PromocionCallback promocionCallback) {
        this.promocionCallback = promocionCallback;
    }

    public void setNumeroPromos(int numeroNotificaciones) {
        if (promocionCallback!=null) {
            promocionCallback.onUpdate(numeroNotificaciones);
        }
        this.numeroPromos = numeroNotificaciones;
    }

    public void obtenerTotalPromocionesFirestore(final String path){
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        final Query query = database.collection(path);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final @Nullable QuerySnapshot snapshots,
                                final @Nullable FirebaseFirestoreException exc) {

                if (exc != null || snapshots==null) {
                    return;
                }
            int numberPromos=0;
                if(snapshots.getDocumentChanges()!= null && !snapshots.getDocumentChanges().isEmpty()){
                    numberPromos = snapshots.getDocumentChanges().size();
                }


                setNumeroPromos(numberPromos);
                Log.i("NUMERO PROMOS", "No.: " +numberPromos);
            }
        });
    }

    public interface PromocionCallback {
        void onUpdate(Integer notificacionesCount);
    }
}
