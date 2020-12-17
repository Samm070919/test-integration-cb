package com.pagatodoholdings.posandroid.secciones.calendar

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.CustomProgressLoader
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios
import com.pagatodoholdings.posandroid.databinding.ActivityListServicesBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity
import com.pagatodoholdings.posandroid.secciones.calendar.fragments.PayServicesFragment
import com.pagatodoholdings.posandroid.secciones.calendar.fragments.ReminderDialog
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean

class ListServicesActivity : AbstractActivity(), ReminderDialog.OnMessageToSnackBarListener {

    lateinit var binding: ActivityListServicesBinding
    var existFavorite = false;

    //NOSONAR
    var loaderAct: CustomProgressLoader? = null

    override fun initUi() {
        loaderAct = CustomProgressLoader(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_services)
        var parametros :Bundle = this.intent.extras!!
        if(parametros!=null) {
            existFavorite = parametros.getBoolean("existFavorite")
        }

        binding.ivBack.setOnClickListener({
            finish()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("isListServicesActiviyRoot",true);
            startActivity(intent)
        })

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, PayServicesFragment())
        transaction.commit()
    }

    override fun muestraProgressDialog(mensaje: String?) {
        runOnUiThread {
            if (loaderAct != null) {
                loaderAct!!.setMessage(mensaje)
                loaderAct!!.show()
            }
        }
    }

    fun showAddFavouriteDialog(reminderDialogFragment: ReminderDialog, favourite : FavoritoBean){
        val fM = this!!.supportFragmentManager
        reminderDialogFragment!!.setReminder(favourite)
        reminderDialogFragment!!.setType("Añadir")
        reminderDialogFragment!!.setMessageListener(this)
        reminderDialogFragment!!.setOnDismissListener(DialogInterface.OnDismissListener {
        })
        reminderDialogFragment?.show(fM, "reminder_dialog")
    }

    override fun ocultaProgressDialog() {
        if (loaderAct != null && loaderAct!!.isShowing) {
            loaderAct!!.dismiss()
        }
        if (isModalFragmentShowing()) {
            dismissConfirmation()
        }
    }

    override fun validaCampos(): Boolean {
        /*No implementation*/
        return true
    }

    override fun registraCamposValidar(): MutableList<EditTextDatosUsuarios>? {
        /*No implementation*/
        return null
    }

    override fun realizaAlPresionarBack() {
        /*No implementation*/
    }

    fun loadHome(){
        finish()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("isListServicesActiviyRoot",true);
        startActivity(intent)
    }

    override fun onMessageToSnackBarListener(isSuccess: Boolean, message: String) {
        if (isSuccess) {
            existFavorite = true
            showSuccessSnackBar(binding.root, message)
        } else {
            showErrorSnackBar(binding.root, message)
        }
    }
}
