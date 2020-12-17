package com.pagatodoholdings.posandroid.secciones.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import kotlinx.android.synthetic.main.dialog_insufficient_balance.*
import kotlinx.android.synthetic.main.dialog_insufficient_balance.view.*

class PDSErrorDialog(var flag : String, var homeActivity: HomeActivity?) : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
        return activity.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_insufficient_balance, null)

            if(flag == "from_pagos_servicios"){
                view.dialog_body_txt.text = getText(R.string.saldo_insuficiente_txt)
                view.btn_validar.text = getText(R.string.rellena_saldo)
                view.btn_validar.setOnClickListener {
                    goToFillBalance()
                }
            }else if(flag == "from_dialog_autoriza_pds"){
                dialog_body_txt.text = getText(R.string.problema_comunicacion_servicio)
                btn_validar.text = getText(R.string.intenta_de_nuevo)
            } else {
                view.dialog_body_txt.text = flag
                view.salir_txt.visibility = View.GONE
                view.btn_validar.setText(R.string.aceptar)
                view.btn_validar.setOnClickListener {
                    hiddeDialog()
                }
            }

            view.salir_txt.setOnClickListener {
                hiddeDialog()
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun goToFillBalance(){
        homeActivity!!.cargarFragmentMoneyIn()
        hiddeDialog()
    }

    private fun hiddeDialog(){
        dialog!!.cancel()
    }
}