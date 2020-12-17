package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment
import com.pagatodoholdings.posandroid.databinding.FragmentReminderDialogBinding
import com.pagatodoholdings.posandroid.secciones.calendar.ListServicesActivity
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.SpinnerCalendarAdapter
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.Utilities
import java.util.*

class ReminderDialog : DialogFragment() {

    lateinit var binding: FragmentReminderDialogBinding
    private val locale = Locale("es", "MX")
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onMessageToSnackBarListener: OnMessageToSnackBarListener? = null
    private lateinit var spinnerAdapter: SpinnerCalendarAdapter
    private var reminder = FavoritoBean()
    private var period: Long=0
    private var dia: Int?=0
    private lateinit var type: String
    private var isSuccess: Boolean = false
    private var message: String = ""
    private var favouritesServices: FavouritesServices? =null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReminderDialogBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        cleanData()
    }

    fun initViews() {
        favouritesServices = FavouritesServices()

        binding.btnCancelDialog?.setOnClickListener {
            hideKeyBoard(binding.edtAlias)
            hideKeyBoard(binding.edtDay)
            this.dismiss()
        }

        binding.btnConfirmDialog?.setOnClickListener {
            validateInfo()
        }

        getIcon()
        val periodicity = arrayOf("Periodo de Facturación", "SEMANAL", "QUINCENAL", "MENSUAL", "BIMESTRAL")
        spinnerAdapter = context?.let { SpinnerCalendarAdapter(it, android.R.layout.simple_spinner_item, periodicity) }!!
        binding.spnPeriodicity.adapter = spinnerAdapter
        binding.spnPeriodicity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    //NO implementation
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {/*No implementation*/}
        }

        if (type.equals("Edit")) {
            binding.btnConfirmDialog.text = "Guardar"
            binding.edtDay.setText(reminder.diaInicio.toString().trim())
            binding.edtAlias.setText(reminder.alias)
            binding.spnPeriodicity.setSelection(0)

            //Obtener Imagen del Producto del favorito (POR DEFINIR)

        } else if (type.equals(resources.getString(R.string.añadir_title))) {
            binding.btnConfirmDialog.text = resources.getString(R.string.añadir_title)
            //Obtener Imagen del Producto del favorito (POR DEFINIR)
        }

        binding.edtAlias.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyBoard(binding.edtAlias)
    //            binding.linDataPicker.performClick()
                true
            } else
                false
        }

        binding.edtAlias.requestFocus()
        binding.edtAlias.setFocusableInTouchMode(true)
        showKeyboard()

        binding.edtDay.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyBoard(binding.edtDay)
                true
            } else
                false
        }

    }

    private fun getIcon(){
        if(reminder.procod!=null){
            var producto = SigmaBdManager.getProducto(reminder.procod, OnFailureListener.BasicOnFailureListener(
                    "Error PRODUCTO",
                    "Error al Obtener Producto"
            ))

            if(producto!=null && producto.icono!=null){
             var icono = SigmaBdManager.getIcono(producto.icono, OnFailureListener.BasicOnFailureListener(
                     "Error ICONO",
                     "Error al Obtener Icono"
             ))

                if(icono!=null) {
                    binding.imgIcon.setImageDrawable(Utilities.getIcono(icono.ruta))
                }
            }
        }
    }

    fun setInfoToReminder() {
        reminder.alias = binding.edtAlias.text.toString()
        reminder.diaInicio = binding.edtDay.text.toString().trim().toInt()

        if(binding.spnPeriodicity.selectedItem.toString().equals("BIMESTRAL")){
            reminder.periodo= "BIMENSUAL"
        }else{
            reminder.periodo= binding.spnPeriodicity.selectedItem.toString()
        }
    }

    fun validateInfo() {
        if (binding.edtAlias.text.isEmpty()) {
            Toast.makeText(context, "Es necesario agregar un Alias",Toast.LENGTH_SHORT).show()
            return
        } else if (binding.edtDay.text.isEmpty()) {
            Toast.makeText(context, "Es necesario asignar un Día",Toast.LENGTH_SHORT).show()
            return
        } else if (binding.spnPeriodicity.selectedItemPosition == 0) {
            Toast.makeText(context, "Es necesario asignar la Periodicidad",Toast.LENGTH_SHORT).show()
            return
        } else {
            setInfoToReminder()
            validateDayWithPeriodicity()
        }
    }

    fun goToUpdateReminder() {
        //No Implementation
    }

    private fun consumeService(){
        (activity as ListServicesActivity).muestraProgressDialog(resources.getString(R.string.cargando))
        if (type.equals("Edit")) {
            hideKeyBoard(binding.edtAlias)
            hideKeyBoard(binding.edtDay)
            goToUpdateReminder()
        } else if (type.equals("Añadir")) {
            hideKeyBoard(binding.edtAlias)
            hideKeyBoard(binding.edtDay)
            goToSaveReminder()
        }
    }

    private fun validateDayWithPeriodicity(){

        val day = binding.edtDay.text.toString().trim().toInt()
        val periodicity = binding.spnPeriodicity.selectedItem.toString()

        when (periodicity) {
            "SEMANAL" -> {
                if(day>=8){
                    hideKeyBoard(binding.edtAlias)
                    hideKeyBoard(binding.edtDay)
                    Toast.makeText(context, "El Rango es de: 1 a 7 días",Toast.LENGTH_SHORT).show()
                    return
                }else{
                    consumeService()
                }
            }
            "QUINCENAL" -> {
                if(day>=16){
                    hideKeyBoard(binding.edtAlias)
                    hideKeyBoard(binding.edtDay)
                    Toast.makeText(context, "El Rango es de: 1 a 15 días",Toast.LENGTH_SHORT).show()
                    return
                }else{
                    consumeService()
                }
            }
            "MENSUAL" ->{
                if(day>=32){
                    hideKeyBoard(binding.edtAlias)
                    hideKeyBoard(binding.edtDay)
                    Toast.makeText(context, "El Rango es de: 1 a 31 días",Toast.LENGTH_SHORT).show()
                    return
                }else{
                    consumeService()
                }
            }
            "BIMESTRAL" ->{
                if(day>=32){
                    hideKeyBoard(binding.edtAlias)
                    hideKeyBoard(binding.edtDay)
                    Toast.makeText(context, "El Rango es de: 1 a 31 días",Toast.LENGTH_SHORT).show()
                    return
                }else{
                    consumeService()
                }
            }
            else -> {
                consumeService()
            }
        }//end When
    }

    fun goToSaveReminder(){
        favouritesServices?.altaFavorito(object : RetrofitListener<Boolean> {
            override fun showMessage(s: String) {
                //No implementation
            }
            override fun onFailure(throwable: Throwable) {
                (activity as ListServicesActivity).ocultaProgressDialog()
                Logger.LOGGER.throwing("", 1, throwable, throwable.message)
                isSuccess = false
                cleanData()
                message = getString(R.string.dialog_save_reminder_failed)
                closeDialog()
            }
            override fun onSuccess(altaExitosa: Boolean) {
                (activity as ListServicesActivity).ocultaProgressDialog()
                if(altaExitosa){
                    cleanData()
                    isSuccess = true
                    message = getString(R.string.dialog_save_reminder_success)
                    closeDialog()
                    (activity as ListServicesActivity).despliegaModal(false,false,"¡Alta Exitosa!",getString(R.string.dialog_save_reminder_success),
                            ModalFragment.CommonDialogFragmentCallBack {
                                //(activity as ListServicesActivity).restaurarHome()
                            })
                }else{
                    cleanData()
                    isSuccess = false
                    message = getString(R.string.dialog_save_reminder_failed)
                    closeDialog()
                }
            }

        },reminder,
                ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod())
    }

    fun closeDialog() {
        if (this != null && activity != null) {
            onMessageToSnackBarListener!!.onMessageToSnackBarListener(isSuccess, message)
            this.dismiss()
        }
    }

    fun cleanData(){
        binding.edtDay.setText("")
        binding.edtAlias.setText("")
        binding.spnPeriodicity.setSelection(0)
    }

    protected fun hideKeyBoard(view: View) {
        val imm: InputMethodManager = getContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showKeyboard() {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT)
        imm.showSoftInput(activity!!.findViewById(R.id.content), InputMethodManager.SHOW_IMPLICIT)
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    fun setMessageListener(onMessageToSnackBarListener: OnMessageToSnackBarListener) {
        this.onMessageToSnackBarListener = onMessageToSnackBarListener
    }

    fun setReminder(reminder: FavoritoBean) {
        this.reminder = reminder
    }

    fun setType(type: String) {
        this.type = type
    }

    interface OnMessageToSnackBarListener {
        fun onMessageToSnackBarListener(boolean: Boolean, message: String)
    }

}
