package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodo.sigmalib.reportetrx.Model;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuReporteVentasItemBinding;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.UtilsKt;
import com.pagatodoholdings.posandroid.utils.enums.ProductCategory;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReporteVentasAdapter extends RecyclerView.Adapter<ReporteVentasAdapter.Holder> {

    private final SaleItemFormatHelper formatHelper = new SaleItemFormatHelper();

    //----- Inst ----------------------------------------------------------
    private final List<Model> dataset;
    private final List<Model> filterDataset;
    private final Consumer<Model> consumer;

    public ReporteVentasAdapter(final List<Model> dataset, Consumer<Model> consumer) {
        this.filterDataset = new ArrayList<>(dataset);
        this.consumer = consumer;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final FragmentConfigMenuReporteVentasItemBinding binding = FragmentConfigMenuReporteVentasItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.tvSaldoMovimientos.setNewTextSizeIndividualMonto(12f, 22f, 12f);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final Model model = filterDataset.get(position);

        BigDecimal bigDecimal = model.getImporte() != null
                ? new BigDecimal(model.getImporte())
                : BigDecimal.ZERO;
        CurrencyImport currencyImport = formatHelper.getCurrencyFormatObject(bigDecimal);
        holder.bindig.tvSaldoMovimientos.setMonto(currencyImport);
        holder.bindig.txtDia.setText(formatHelper.getDayNumber(model.getFecha()));

        if (model.getProCod().equalsIgnoreCase("SalidaDinero")) {
            holder.bindig.txtDescProducto.setText("Salida de Dinero");
            holder.bindig.txtReferencia.setText("Cuenta: " + model.getRefCliente());
            holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_perdida);
        } else if (model.getProCod().equalsIgnoreCase("RecargaTarjeta")) {
            holder.bindig.txtDescProducto.setText("Relleno de Saldo");
            holder.bindig.txtReferencia.setText(
                    UtilsKt.capitalizeWords(
                            model.getDescProducto()
                    ) + ": " + model.getRefCliente());
            holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_ganancia);
        } else if (model.getProCod().equalsIgnoreCase("PAGO_QR")) {
            holder.bindig.txtDescProducto.setText(model.getDescProducto());
            holder.bindig.txtReferencia.setText("Pago a: " + model.getRefCliente());
            holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_perdida);
        } else if (model.getProCod().equalsIgnoreCase("CompraKit")) {
            holder.bindig.txtDescProducto.setText("Compra Lector MPos");
            holder.bindig.txtReferencia.setText(UtilsKt.capitalizeWords(
                    model.getDescProducto()
            ) + ": " + model.getRefCliente());
            holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_perdida);
        } else if (model.getCodigoOper().equalsIgnoreCase(Constantes.DEVOLUCION)) {
            holder.bindig.txtDescProducto.setText("Venta con Tarjeta");
            holder.bindig.txtReferencia.setText(
                    UtilsKt.capitalizeWords(
                            model.getDescProducto()
                    ) + ": " + model.getRefCliente());
            holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_devuelta);
        } else {
            Productos producto = formatHelper.getProducto(model.getProCod());

            switch (producto.getCategoria()) {
                case ProductCategory.MDP_REGULADOS:
                    holder.bindig.txtDescProducto.setText("Venta con Tarjeta");
                    holder.bindig.txtReferencia.setText(
                            UtilsKt.capitalizeWords(
                                    model.getDescProducto()
                            ) + ": " + model.getRefCliente());
                    holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_ganancia);
                    break;
                case ProductCategory.PDS:
                case ProductCategory.PDS_EXTRA:
                    holder.bindig.txtDescProducto.setText("Pago de Servicio");
                    holder.bindig.txtReferencia.setText(
                            UtilsKt.capitalizeWords(
                                    model.getDescProducto()
                            ) + ": " + model.getRefCliente());
                    holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_perdida);
                    break;
                case ProductCategory.NA:
                case ProductCategory.TAE:
                case ProductCategory.OTRAS_RECARGAS: // Pines
                    holder.bindig.txtDescProducto.setText("Recarga Electrónica");
                    holder.bindig.txtReferencia.setText(
                            UtilsKt.capitalizeWords(
                                    model.getDescProducto()
                            ) + ": " + model.getRefCliente());
                    holder.bindig.viewColor.setImageResource(R.drawable.ic_mov_perdida);
                    break;
                default:
                    holder.bindig.txtDescProducto.setText(model.getDescProducto());
                    holder.bindig.txtReferencia.setText(MposApplication.getInstance().getString(R.string.transaction_adapter_reference_local, model.getRefCliente()));
                    holder.bindig.viewColor.setImageResource(R.color.colorWhite);
            }
        }

        holder.bindig.getRoot().setOnClickListener(view -> consumer.accept(model));
    }

    @Override
    public int getItemCount() {
        return filterDataset.size();
    }

    public boolean filterByDate(final Calendar calFilter) {
        final Calendar cal = Calendar.getInstance();
        filterDataset.clear();
        for (final Model model : dataset) {
            cal.setTimeInMillis(model.getFecha());
            final String DATE_FORMAT = "dd/MM/yyyy";
            if (DateFormat.format(DATE_FORMAT, cal.getTime()).equals(DateFormat.format(DATE_FORMAT, calFilter.getTime()))) {
                filterDataset.add(model);
            }
        }
        notifyDataSetChanged();
        return filterDataset.isEmpty();
    }

    public boolean filterByDateRange(final Calendar calFilterIni, final Calendar calFilterFin) {
        final Calendar cal = Calendar.getInstance();
        filterDataset.clear();

        for (final Model model : dataset) {
            cal.setTimeInMillis(model.getFecha());
            if (!(cal.before(calFilterIni) || cal.after(calFilterFin))) {
                filterDataset.add(model);
            }
        }

        notifyDataSetChanged();
        return filterDataset.isEmpty();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final FragmentConfigMenuReporteVentasItemBinding bindig;

        Holder(final FragmentConfigMenuReporteVentasItemBinding bindig) {
            super(bindig.getRoot());
            this.bindig = bindig;
        }
    }
}
