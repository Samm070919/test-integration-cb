package com.pagatodoholdings.posandroid.secciones.calendar.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import java.text.SimpleDateFormat
import java.util.*

class ListEventsAdapter(private val context: Context, listener: OnEventClickListener) : RecyclerView.Adapter<ListEventsAdapter.ViewHolder>() {

    private var listReminders: List<FavoritoBean> = ArrayList()
    private val listener: OnEventClickListener? = listener
    private val locale = Locale("es", "MX")
    private var currentDate: Date = GregorianCalendar(locale).time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = listReminders!![position]

        holder.txvAlias.isSelected = true
        holder.txvAlias.text = reminder.alias
        holder.txvDate.text = formatDateText(reminder.fecha)

        if(reminder.estado.equals("PAGADO")){
            holder.cvColor.setCardBackgroundColor(context.resources.getColor(R.color.green))
        }else if(reminder.estado.equals("PENDIENTE")){
            holder.cvColor.setCardBackgroundColor(context.resources.getColor(R.color.orange))
        }else{
            holder.cvColor.setCardBackgroundColor(context.resources.getColor(R.color.red))
        }

        holder.txvIdComercio.text = reminder.procod
        holder.ctlEvent.setOnClickListener {
            listener!!.OnCellClickListener(reminder)
        }

    }

    fun formatDateText(fecha:String): String{
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        var date = formatter.parse(fecha)
        val month= DateFormat.format("MMMM", date) as String
        var dateDefault=DateFormat.format("dd", date) as String + " de " +
                month.substring(0,1).toUpperCase() + month.substring(1)
        return dateDefault
    }

    override fun getItemCount(): Int {
        return listReminders!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvColor: CardView = itemView.findViewById(R.id.cvColor)
        val txvAlias: TextView = itemView.findViewById(R.id.txvAlias)
        val txvDate: TextView = itemView.findViewById(R.id.txvDate)
        val txvColor: TextView = itemView.findViewById(R.id.txvColor)
        val txvIdComercio: TextView = itemView.findViewById(R.id.txvIdComercio)
        val ctlEvent: ConstraintLayout = itemView.findViewById(R.id.ctlEvent)
    }

    fun getItem(position: Int): FavoritoBean {
        if (listReminders.size < position)
            return FavoritoBean()
        return listReminders[position]
    }

    fun replaceData(listReminders: List<FavoritoBean>) {
        this.listReminders = listReminders
        this.notifyDataSetChanged()
    }

    interface OnEventClickListener {
        fun OnCellClickListener(reminder: FavoritoBean?)
    }


}