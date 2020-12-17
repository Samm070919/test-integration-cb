package com.pagatodoholdings.posandroid.secciones.calendar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.calendar.models.PanelItem
import com.pagatodoholdings.posandroid.utils.Utilities
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Iconos

class FavouritesPanelAdapter(private val context: Context, listener: OnItemPanelClickListener) : RecyclerView.Adapter<FavouritesPanelAdapter.ViewHolder>() {
    private var listItemsPanel: List<PanelItem>? = null
    private val listener: OnItemPanelClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPanel = listItemsPanel!![position]
        var icono:Iconos? =null
        holder.linContainer.setOnClickListener {
            listener?.onItemPanelClickListener(itemPanel)
        }

        when (itemPanel.alias) {
            "Ver más" -> {
                holder.txvName.text = itemPanel.alias
                holder.txvName.visibility = View.VISIBLE
                holder.imgIcon.setImageResource(itemPanel.imageInt!!)
                holder.imgIcon.layoutParams.height = 142
                holder.imgIcon.layoutParams.width = 102
                holder.linContainer.setBackgroundResource(R.drawable.border_blue)
            }
            "Más" -> {
                holder.txvName.visibility = View.GONE
                val constraintSet = ConstraintSet()
                constraintSet.clone(holder.linContainer)
                constraintSet.connect(R.id.imgIcon, ConstraintSet.BOTTOM, R.id.gl5, ConstraintSet.BOTTOM, 0)
                constraintSet.applyTo(holder.linContainer)
                holder.imgIcon.setImageResource(itemPanel.imageInt!!)

                val parameter = holder.imgIcon.layoutParams as ConstraintLayout.LayoutParams
                parameter.setMargins(30, 30, 30, 30)
                holder.imgIcon.layoutParams = parameter
            }
            else -> {

                holder.txvName.text = itemPanel.alias
                holder.txvName.visibility = View.VISIBLE

                if(itemPanel.imageInt!=null) {
                    icono = SigmaBdManager.getIcono(itemPanel.imageInt!!, OnFailureListener.BasicOnFailureListener("ERROR", "Error"))
                    if(icono!=null) {
                        holder.imgIcon.setImageDrawable(Utilities.getIcono(icono.ruta))
                    }else{
                        holder.imgIcon.setImageResource(itemPanel.imageInt!!)
                    }
                }else{
                    holder.imgIcon.setImageResource(R.drawable.ya_icon_logo)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return listItemsPanel!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txvName: TextView = itemView.findViewById(R.id.txvName)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val linContainer: ConstraintLayout = itemView.findViewById(R.id.linContainer)
        val cvContainer: CardView = itemView.findViewById(R.id.cvContainer)
    }

    interface OnItemPanelClickListener {
        fun onItemPanelClickListener(panelItem: PanelItem)
    }

    fun replaceData(listItemsPanel: List<PanelItem>) {
        this.listItemsPanel = listItemsPanel
        this.notifyDataSetChanged()
    }

}