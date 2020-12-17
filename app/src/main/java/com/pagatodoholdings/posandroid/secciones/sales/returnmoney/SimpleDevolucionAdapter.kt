package com.pagatodoholdings.posandroid.secciones.sales.returnmoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.BtnCheckHolder
import com.pagatodoholdings.posandroid.utils.Utilities
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu

class SimpleDevolucionAdapter(private val listItems: MutableList<BtnCheckHolder.ItemProdData>,
                              private val onItemChosen: Consumer<Menu>?)
    : RecyclerView.Adapter<SimpleDevolucionAdapter.SubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_submenu, parent, false) as View
        return SubViewHolder(view)
    }

    override fun getItemCount() = listItems.size

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
        val itemProdData = listItems[position]
        val drawable = Utilities.getIcono(itemProdData.icon)

        holder.icon.setImageDrawable(drawable)
        holder.label.text = itemProdData.menu.texto
        holder.itemView.setOnClickListener {
            onItemChosen?.accept(itemProdData.menu)
        }
    }

    class SubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icono_producto)
        val label: TextView = itemView.findViewById(R.id.submenu)
    }
}