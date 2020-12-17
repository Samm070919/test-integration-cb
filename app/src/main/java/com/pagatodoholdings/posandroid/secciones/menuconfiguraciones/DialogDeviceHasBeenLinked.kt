package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country
import kotlinx.android.synthetic.main.device_has_been_linked.*
import kotlinx.android.synthetic.main.device_has_been_linked.view.*
import kotlinx.android.synthetic.main.device_has_been_linked.view.tv_contact_phone
import kotlinx.android.synthetic.main.turn_on_bluetooth_dialog.view.*
import java.lang.IllegalStateException

class DialogDeviceHasBeenLinked : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let{
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            var view = inflater.inflate(R.layout.device_has_been_linked, null)

            view.btn_cerrar.setOnClickListener {
                dialog?.cancel()
            }

            setContactPhone(view)

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        //return super.onCreateDialog(savedInstanceState)
    }

    private fun setContactPhone(view : View){
        if(Country.PERU.itemIsoCode == MposApplication.getInstance().datosLogin.pais.codigo){
            view.tv_contact_phone.text = Country.PERU.contactPhone
        }else if(Country.COLOMBIA.itemIsoCode ==
                MposApplication.getInstance().datosLogin.pais.codigo
        ){
            view.tv_contact_phone.text = Country.COLOMBIA.contactPhone
        }
    }
}