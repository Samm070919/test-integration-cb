package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.cambio_contraseña

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodo.sigmalib.util.Constantes
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment.CommonDialogFragmentCallBackWithCancel
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.DialogTurnOnBluetooth
import com.pagatodoholdings.posandroid.secciones.retrofit.ContrasenaServiceInteractor
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.capitalizeWords
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePasswordFragment : AbstractConfigMenu() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var correoElectronico: String? = null
    var messageError = "Error desconocido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_back.setOnClickListener {
            loadSettings(this)
        }

        btn_go_to_change_password.setOnClickListener {
            cambiarContrasena()
        }
    }

    private fun verifyNewPassword(newPass1 : String, newPass2 : String) : Boolean{
        if(newPass1.equals(newPass2)){
            return true
        }else{
            return false
        }
    }

    private fun passwordsNotMatch(){
        Toast.makeText(activity, R.string.contrasen_a_no_coinciden, Toast.LENGTH_SHORT).show()
    }

    private fun cambiarContrasena() {
        if (verifyNewPassword(
                        ed_new_password.texto.trim().toString(),
                        ed_confirm_new_password.texto.trim().toString()
                )) {
            if (correoElectronico == null) {
                correoElectronico = MposApplication
                        .getInstance()
                        .preferenceManager
                        .getValue(Constantes.Preferencia.EMAIL, "")
            }
            val cambiarContrasena = ContrasenaServiceInteractor()
            cambiarContrasena.cambiarService(
                    correoElectronico,
                    ed_current_password.texto.trim(),
                    ed_new_password.texto.trim(),
                    object : RetrofitListener<Any> {
                        override fun showMessage(message: String) {
                            //No implementation
                        }

                        override fun onFailure(throwable: Throwable) {
                            obtenerMensajeError(throwable.message!!)
                            showDialogContrasena(R.layout.layout_cambio_contrasena,
                                    getString(R.string.menu_ajustes_opcion_contraseña),
                                    capitalizeWords(messageError),
                                    object : CommonDialogFragmentCallBackWithCancel {
                                        override fun onCancel() {
                                            //No implementation
                                        }

                                        override fun onAccept() {
                                            //No implementation
                                        }
                                    })
                        }

                        override fun onSuccess(result: Any) {
                            /*showDialogContrasena(R.layout.layout_cambio_contrasena,
                                    getString(R.string.menu_title_ajustes_cambio_contraseña),
                                    getString(R.string.message_cambio_contrasena_success_body),
                                    object : CommonDialogFragmentCallBackWithCancel {
                                        override fun onCancel() {
                                            //No implementation
                                        }

                                        override fun onAccept() {
                                            cerrarSessionApp()
                                        }
                                    })*/

                            showDialog(
                                    R.drawable.ic_ilustracion_bluetooth,
                                    getString(R.string.contrasenia_actualizada),
                                    getString(R.string.has_actualizado_pass),
                                    getString(R.string.menu_item_cierre_sesion)
                            )

                        }
                    }
            )
        }else{
            passwordsNotMatch()
        }
    }

    private fun obtenerMensajeError(response: String) {
        try {
            val jsonObject = JSONObject(response)
            messageError = jsonObject.getString("message")
        } catch (e: JSONException) {
            Logger.LOGGER.throwing(AbstractFragment.TAG, 1, Throwable("Error"), "JSONException: " + e.message)
        }
    }

    fun showDialogContrasena(layout: Int,
                             title: String?,
                             body: String?,
                             callback: CommonDialogFragmentCallBackWithCancel
    ) { //NOSONAR
        val alert = AlertDialog.Builder(ContextThemeWrapper(getActivity(), R.style.AppTheme))
        val layoutInflater = LayoutInflater.from(getActivity())
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(layout, null)
        alert.setCancelable(false)
        alert.setView(view)
        val alertDialog = alert.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnAceptar: BotonClickUnico = view.findViewById(R.id.btnConfirmacion)
        btnAceptar.text = getString(R.string.aceptar)
        btnAceptar.textSize = 14f
        btnAceptar.setOnClickListener { view1: View? ->
            callback.onAccept()
            alertDialog.dismiss()
        }
        val textTitle = view.findViewById<TextView>(R.id.title_toolbar)
        textTitle.text = title
        val textBody = view.findViewById<TextView>(R.id.texto_body)
        textBody.text = body
        alertDialog.show()
    }

    private fun cerrarSessionApp() {
        (getActivity() as AbstractActivity?)!!.finishApp()
    }

    private fun showDialog(image : Int, title : String, body : String, buttonText : String){
        var dialog = DialogTurnOnBluetooth(image, title, body, buttonText, false)
        var fm = activity.supportFragmentManager
        dialog.show(fm, "Cerrar sesion dialog")
    }

    fun loadSettings(fragment: Fragment?) {
        getActivity()!!.supportFragmentManager.beginTransaction().remove(fragment!!).commit()
        if (parentFragment != null) {
            (parentFragment as DialogFragment?)!!.dismiss()
        } else {
            (activity as HomeActivity).cargarFragmentMiCuenta()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChangePasswordFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
