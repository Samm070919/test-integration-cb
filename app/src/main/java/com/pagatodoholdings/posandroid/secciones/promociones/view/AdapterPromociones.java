package com.pagatodoholdings.posandroid.secciones.promociones.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.promociones.Documento;
import com.pagatodoholdings.posandroid.secciones.promociones.Mensaje;
import com.pagatodoholdings.posandroid.secciones.promociones.Promocion;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPromociones extends BaseAdapter {

    private ArrayList<Documento> promociones;
    HomeActivity activity;
    private LayoutInflater layoutInflater;

    public AdapterPromociones(LayoutInflater layoutInflater, ArrayList<Documento> promociones, HomeActivity activity) {
        this.layoutInflater=layoutInflater;
        this.promociones = promociones;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return promociones.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {
        if(promociones.get(position) instanceof Promocion) {
            if (!fechaExpiro(position)) {
                cargarFragmentPromocionesDetalle(position);
            }else {
                Toast.makeText(activity.getApplicationContext(), "La promoción ha expirado", Toast.LENGTH_SHORT).show();
            }
        }else if (promociones.get(position) instanceof Mensaje) {
            cargarFragmentMensajesDetalle(position);
        }
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View inflatedView = layoutInflater.inflate(R.layout.custom_list_promociones,null);


        CardView cvPromos = inflatedView.findViewById(R.id.cv_promos);
        CardView cvMensajes = inflatedView.findViewById(R.id.cv_mensajes);
        ImageView ivPromos = inflatedView.findViewById(R.id.iv_promos);
        ImageView ivMensajes = inflatedView.findViewById(R.id.iv_mensajes);
        TextView textView = inflatedView.findViewById(R.id.tv_promocion_text);
        TextView textViewBody = inflatedView.findViewById(R.id.tv_promocion_body);

        if(!promociones.get(position).isInbox()){
            cvMensajes.setVisibility(View.GONE);
            cvPromos.setVisibility(View.VISIBLE);
            setImageView(ivPromos,position);
        }else{
            cvPromos.setVisibility(View.GONE);
            cvMensajes.setVisibility(View.VISIBLE);
            setImageView(ivMensajes, position);
        }

        if(promociones.get(position) instanceof Promocion) {
            if (fechaExpiro(position)) {
                textView.setTextColor(Color.LTGRAY);
            }
        }


        textView.setText(promociones.get(position).getTitulo());
        textViewBody.setText(promociones.get(position).getMensaje());
        return inflatedView;
    }

    private void setImageView(ImageView imageView, int position){
        if(!promociones.get(position).isInbox()){
            if(promociones.get(position).getImagen() !=null &&promociones.get(position).getImagen().length()>0) {
                Picasso.with(activity.getApplicationContext()).load(promociones.get(position).getImagen()).into(imageView);
            }
        }else{
            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(100,100);
            parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            imageView.setLayoutParams(parms);
        }
    }

    private boolean fechaExpiro(int position){
        SimpleDateFormat formatEntrada = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if(promociones.get(position) instanceof Promocion) {
                Date fechaFin = formatEntrada.parse(((Promocion)promociones.get(position)).getFechaFin());
                Date fechaSistema = new Date();

                if (fechaFin.compareTo(fechaSistema) < 0) {
                    return true;
                }
            }

        }catch (ParseException e){
            return false;
        }
        return false;
    }


    private void cargarFragmentMensajesDetalle(int position) {
        if(promociones.get(position) instanceof Mensaje){
            final MensajesDetalleFragment mensajesDetalleFragment = new MensajesDetalleFragment();
            mensajesDetalleFragment.setMensajeDetalle((Mensaje) promociones.get(position));
            activity.getSupportFragmentManager().beginTransaction().
                    replace(activity.getBinding().flMainPantallaCompleta.getId(), mensajesDetalleFragment).commit();
        }
    }

    private void cargarFragmentPromocionesDetalle(int position){
        if(promociones.get(position) instanceof Promocion) {
            final PromocionesDetalleFragment promocionesDetalleFragment = new PromocionesDetalleFragment();
            promocionesDetalleFragment.setPromocionDetalle((Promocion) promociones.get(position));
            activity.getSupportFragmentManager().beginTransaction().
                    replace(activity.getBinding().flMainPantallaCompleta.getId(), promocionesDetalleFragment).commit();
        }
    }
}
