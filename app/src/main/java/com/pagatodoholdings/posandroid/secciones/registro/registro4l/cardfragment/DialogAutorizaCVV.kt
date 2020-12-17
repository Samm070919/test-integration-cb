package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.utils.ManejadorFragments
import com.pagatodoholdings.posandroid.utils.Utilities
import kotlinx.android.synthetic.main.dialog_cvv_compra.*


class DialogAutorizaCVV(val listener: OnCVVGiven, val texto: String) : DialogFragment() {

    interface OnCVVGiven {
        fun cvvGiven(cvv: String)
    }

    private lateinit var manejadorFragments: ManejadorFragments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
        manejadorFragments = ManejadorFragments(activity!!.supportFragmentManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_cvv_compra, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_pass_autorizacion.maximoCaracteres = 4
        et_pass_autorizacion.estableceAccionIme { textView, imeAction, keyEvent ->
            if (imeAction === EditorInfo.IME_ACTION_DONE) {
                Utilities.hideSoftKeyboard(activity)
                btn_continuar_autorizacion.callOnClick()
                return@estableceAccionIme true
            }
            false
        }
        if(!texto.isEmpty())
            tvTextoCvv.text = texto
        btn_continuar_autorizacion.setOnClickListener {
            val cvv:String  = et_pass_autorizacion.text
            when {

                cvv.trim().isEmpty() -> {
                    Toast.makeText(activity, "Ingresa un CVV", Toast.LENGTH_SHORT).show()
                }
                cvv.trim().length < 3 -> {
                    Toast.makeText(activity, "Completa el CVV", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    dismiss()
                    listener.cvvGiven(et_pass_autorizacion.onlySubSecuentTextCVV!!.trim())
                }
            }

        }
    }

    private fun continueFlow(){

    }
    protected fun cargarConfirmacion() {

    }

}