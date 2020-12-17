package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import kotlinx.android.synthetic.main.unlink_device_dialog.view.*

class DialogUnlinkDevice : DialogFragment() {

    lateinit var fragment : ConfigMenuDongleVinculadoFragment

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        fragment = ConfigMenuDongleVinculadoFragment()

        return activity.let{
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            var view = inflater.inflate(R.layout.unlink_device_dialog, null)

            view.apply {
                btn_cancelar_unlink_device.setOnClickListener {
                    dialog?.cancel()
                }

                btnConfirmacion.setOnClickListener {
                    fragment.desvincularDongleService(activity as HomeActivity)
                    dialog?.cancel()
                }
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        //return super.onCreateDialog(savedInstanceState)
    }

}