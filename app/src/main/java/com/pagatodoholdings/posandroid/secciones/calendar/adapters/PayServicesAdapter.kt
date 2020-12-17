package com.pagatodoholdings.posandroid.secciones.calendar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.entities.icono
import com.pagatodoholdings.posandroid.utils.Utilities
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Iconos
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos

class PayServicesAdapter(private val context: Context, listener: OnComercioClickListener) : RecyclerView.Adapter<PayServicesAdapter.ViewHolder>() {

    private var listProducts: List<Productos> = ArrayList()
    private val listener: OnComercioClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayServicesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var producto: Productos = listProducts.get(position)

        if(producto.icono!=null){
            var icono = SigmaBdManager.getIcono(producto.icono, BasicOnFailureListener("ERROR", "Error"))
            holder.imgIcon.setImageDrawable(Utilities.getIcono(icono.ruta))
        }else{
            holder.imgIcon.setImageResource(R.drawable.ya_icon_logo)
        }

        holder.consContainer.setOnClickListener {
            listener?.onComercioClickListener(producto)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val consContainer: ConstraintLayout = itemView.findViewById(R.id.consContainer)
    }

    override fun getItemCount(): Int {
        return listProducts.size
    }

    fun replaceData(listProductos: List<Productos>) {
        this.listProducts = listProductos
        this.notifyDataSetChanged()
    }

    interface OnComercioClickListener {
        fun onComercioClickListener(reminder: Productos)
    }

}