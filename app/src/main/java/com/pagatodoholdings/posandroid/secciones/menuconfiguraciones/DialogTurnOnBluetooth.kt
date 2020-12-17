package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import kotlinx.android.synthetic.main.turn_on_bluetooth_dialog.view.*

class DialogTurnOnBluetooth(
        var image : Int,
        var title : String,
        var body : String,
        var buttonText : String,
        var flag : Boolean
) : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let{
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            var view = inflater.inflate(R.layout.turn_on_bluetooth_dialog, null)

            view.apply {
                imageViewDialog.setImageResource(image)
                textViewTitle.text = title
                textViewBody.text = body
                btn_continue_link.text = buttonText
                btn_continue_link.setOnClickListener {

                    if(buttonText.equals("Cerrar Sesión")){
                        cerrarSessionApp()
                        dialog?.cancel()
                    }else if(flag){
                        loadDeviceLinked()
                        dialog?.cancel()
                    }else{
                        dialog?.cancel()
                    }

                }
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        //return super.onCreateDialog(savedInstanceState)
    }

    private fun cerrarSessionApp() {
        (activity as AbstractActivity?)!!.finishApp()
    }

    private fun loadDeviceLinked(){
        val dongleVinculadoFragment = ConfigMenuDongleVinculadoFragment()
        dongleVinculadoFragment.listener = (activity as HomeActivity)
        (activity as HomeActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(
                        (activity as HomeActivity)
                                .binding.flMainPantallaCompleta
                                .id,
                        dongleVinculadoFragment
                ).commit()
    }
}