package com.pagatodoholdings.posandroid.secciones.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import kotlinx.android.synthetic.main.fragment_dni.view.*

class DniDialog(private val dniInteractionListener: DniInteractionListener?) : DialogFragment() {
    companion object {
        val TAG = DniDialog::class.java.simpleName
    }

    private lateinit var inflatedView: View
    private lateinit var prefix: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let { fragmentActivity ->
            setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
            val inflater = fragmentActivity!!.layoutInflater

            inflatedView = inflater.inflate(R.layout.fragment_dni, null)
            inflatedView.okButton.setOnClickListener(onActionPerformed)

            when (MposApplication.getInstance().datosLogin.pais.codigo) {
                "604" -> { // PERU
                    inflatedView.textView.text = "Ingresa el DNI del Cliente"
                    inflatedView.editText.hint = "DNI"
                    prefix = "DNI"
                }
                "170" -> { // Colombia
                    inflatedView.textView.text = "Ingresa el CC del Cliente"
                    inflatedView.editText.hint = "CC"
                    prefix = "CC"
                }
                else -> {
                    inflatedView.textView.text = "Ingresa el DNI del Cliente"
                    inflatedView.editText.hint = "DNI"
                    prefix = "DNI"
                }
            }

            AlertDialog.Builder(fragmentActivity)
                    .setView(inflatedView)
                    .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(false)
    }

    private val onActionPerformed = View.OnClickListener {
        val dni = inflatedView.editText.text.trim().toString()
        if (dni.isEmpty()) {
            inflatedView.editText.error = "Este Dato es Requerido"
        } else {
            dniInteractionListener?.onDniRetrieved("$prefix: $dni")
            dismiss()
        }
    }

    interface DniInteractionListener {
        fun onDniRetrieved(dni: String)
    }
}