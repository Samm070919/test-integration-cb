package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import kotlinx.android.synthetic.main.dialog_delete_banlk_info.*
import kotlinx.android.synthetic.main.dialog_delete_banlk_info.view.*

class InfoCuentaBancariaDeleteDialogs(var title : String, var body : String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
        return activity.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_delete_banlk_info, null)

            view.tvInfoBankTitle.text = title
            view.tvInfoBankBody.text = body

            view.btn_cerrar_Bank_info.setOnClickListener {
                dialog?.dismiss()
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}