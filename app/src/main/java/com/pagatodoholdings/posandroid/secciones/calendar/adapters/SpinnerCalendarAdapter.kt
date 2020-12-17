package com.pagatodoholdings.posandroid.secciones.calendar.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pagatodoholdings.posandroid.R


class SpinnerCalendarAdapter(context: Context, textViewResourceId: Int, private val values: Array<String>) : ArrayAdapter<String?>(context, textViewResourceId, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(getContext())
        val dropDownView = inflater.inflate(R.layout.fragment_spinner_calendar_adapter, parent, false)
        val label = dropDownView.findViewById<TextView>(R.id.txvItemSpinner)
        if (position == 0) {
            label.setTextColor(Color.parseColor("#46606a"))
        }
        label.setPadding(0, 0, 0, 0)
        label.text = values[position]
        return dropDownView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(getContext())
        val dropDownView = inflater.inflate(R.layout.fragment_spinner_calendar_adapter, parent, false)
        val label = dropDownView.findViewById<TextView>(R.id.txvItemSpinner)
        if (position == 0) {
            label.setTextColor(Color.parseColor("#46606a"))
        } else {
            label.setTextColor(Color.BLACK)
        }
        label.text = values[position]
        return dropDownView
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): String? {
        return values[position]
    }

}
