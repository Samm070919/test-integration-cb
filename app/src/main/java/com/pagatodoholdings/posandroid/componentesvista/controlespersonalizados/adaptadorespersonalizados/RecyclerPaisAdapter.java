package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.R;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecyclerPaisAdapter extends RecyclerView.Adapter<RecyclerPaisAdapter.CardPaisViewHolder> {
    private Map<Country, Boolean> itemsFilter;
    private List<Country> items;
    private int selectedItem = -1;
    private ItemClickListener clickListener;
    private Context context;

    public RecyclerPaisAdapter(final Map<Country, Boolean> itemAdapterBooleanMap) {
        this.items = Arrays.asList(
                Country.ARGENTINA,
                Country.BRASIL,
                Country.CHILE,
                Country.COLOMBIA,
                Country.ECUADOR,
                Country.ESPANA,
                //   ItemAdapter.MEXICO,
                Country.PERU);

        this.itemsFilter = itemAdapterBooleanMap;
    }


    @NonNull
    @Override
    public CardPaisViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        return new CardPaisViewHolder(inflater.inflate(R.layout.item_rv_pais_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardPaisViewHolder holder, int position) {
        final int pos = position;
        if (selectedItem == pos) {
            holder.layout.setBackgroundResource(R.drawable.field_bag_selected);
        } else {
            holder.layout.setBackgroundResource(R.drawable.field_bg);
        }
        holder.imageView.setImageResource(items.get(pos).getItemRes());
        holder.txtName.setTextSize(18f);
        holder.txtName.setText(items.get(pos).getItemName());

        if(!itemsFilter.get(items.get(pos)).booleanValue()){
            holder.imageView.setOnClickListener(v -> Toast.makeText(context,context.getText(R.string.registro_header_select_pais_no_disponible),Toast.LENGTH_SHORT).show());
        }else {
            holder.imageView.setOnClickListener(v -> {
                selectedItem = pos;
                clickListener.onItemSelected(items.get(pos));
                notifyDataSetChanged();
            });
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedCountry(String selectedCountryName) {
        if (clickListener != null && selectedCountryName != null) {
            int i = 0;
            for (Country item : items) {
                //Se remueven los acentos de los nombres de los paises y se convierten a mayusculas
                // para poder compararlos. Y que el nombre del país recibido por el GPS puede
                //contener o no acentos o venir en diferente formato
                String countryNameFromList = prepareText(item.getItemName());
                selectedCountryName = prepareText(selectedCountryName);
                if (selectedCountryName.equals(countryNameFromList)) {
                    selectedItem = i;
                    notifyDataSetChanged();
                    return;
                }
                i++;
            }
        }
    }

    private String prepareText(String oldText) {
        String newText = StringUtils.stripAccents(oldText);
        return newText.toUpperCase();
    }

    class CardPaisViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final ImageView imageView;
        private final LinearLayout layout;

        public CardPaisViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.cardView2);
            txtName = itemView.findViewById(R.id.nombre_producto);
            imageView = itemView.findViewById(R.id.icono_producto);
        }
    }

    public void setOnItemClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemSelected(Country country);
    }


}