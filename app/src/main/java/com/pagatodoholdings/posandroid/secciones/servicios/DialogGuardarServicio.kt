package com.pagatodoholdings.posandroid.secciones.servicios

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.SpinnerCalendarAdapter
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.Utilities
import kotlinx.android.synthetic.main.dialog_guardar_servicio.*
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import java.util.*


class DialogGuardarServicio(iconoServicio: Drawable, producto : Productos) : DialogFragment() {

    private var icono: Drawable
    private var homeActivity: HomeActivity? = null
    private var favouritesServices: FavouritesServices? =null
    private lateinit var type: String
    private var isSuccess: Boolean = false
    private var message: String = ""
    private var reminder = FavoritoBean()
    private var onMessageToSnackBarListener: OnMessageToSnackBarListener? = null
    private lateinit var spinnerAdapter: SpinnerCalendarAdapter
    private var onDismissListener: DialogInterface.OnDismissListener? = null

    init {
        icono = iconoServicio
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle);
        homeActivity = getActivity() as HomeActivity
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_guardar_servicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesServices = FavouritesServices()


        iv_icon_dialog.setImageDrawable(icono)
        btn_guardar_servicio.setOnClickListener({
            //dismiss()
            //homeActivity!!.regresaMenu()
            validateInfo()
        })

        tv_close_view.setOnClickListener({
            dismiss()
            homeActivity!!.regresaMenu()
        })

        /*iv_calendar.setOnClickListener({
            showDialogRepetir()
        })*/

        getIcon()

        val periodicity = arrayOf("", "SEMANAL", "QUINCENAL", "MENSUAL", "BIMESTRAL")
        spinnerAdapter = context?.let { SpinnerCalendarAdapter(it, android.R.layout.simple_spinner_item, periodicity) }!!

        spnPeriodicity2.adapter = spinnerAdapter
        spnPeriodicity2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    //NO implementation
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {/*No implementation*/}
        }

        /*if (type.equals("Edit")) {
            //btn_guardar_servicio.text = "Guardar"
            et_dia.setText(reminder.diaInicio.toString().trim())
            et_alias.setText(reminder.alias)
            spnPeriodicity2.setSelection(0)

            //Obtener Imagen del Producto del favorito (POR DEFINIR)

        } else if (type.equals(resources.getString(R.string.añadir_title))) {
            binding.btnConfirmDialog.text = resources.getString(R.string.añadir_title)
            //Obtener Imagen del Producto del favorito (POR DEFINIR)
        }*/

        et_alias.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyBoard(et_alias)
                //            binding.linDataPicker.performClick()
                true
            } else
                false
        }

        et_alias.requestFocus()
        et_alias.setFocusableInTouchMode(true)
        showKeyboard()

        et_dia.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyBoard(et_dia)
                true
            } else
                false
        }
    }

    /*fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            et_repetir.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year)
        }, year, month, day)

        dpd.show()
    }*/

    /*fun showDialogRepetir() {
        //Declara la lista de elementos

        val items = arrayOf("Semana", "Quincena", "Mes","Bimestre")
        //Crea el diálogo
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Elegir elemento")
        //Crea la lista de ítems en el diálogo

        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            et_repetir.setText(items[item])
        })

        //Muestra el dialogo
        builder.show()
    }*/

    fun validateInfo() {
        if (et_alias.text.isEmpty()) {
            Toast.makeText(context, "Es necesario agregar un Alias", Toast.LENGTH_SHORT).show()
            return
        } else if (et_dia.text.isEmpty()) {
            Toast.makeText(context, "Es necesario asignar un Día", Toast.LENGTH_SHORT).show()
            return
        } else if (spnPeriodicity2.selectedItemPosition == 0) {
            Toast.makeText(context, "Es necesario asignar la Periodicidad", Toast.LENGTH_SHORT).show()
            return
        } else {
            homeActivity!!.muestraProgressDialog(resources.getString(R.string.cargando))
            setInfoToReminder()
            if (type.equals("Edit")) {
                hideKeyBoard(et_alias)
                hideKeyBoard(et_dia)
                goToUpdateReminder()
            } else if (type.equals("Añadir")) {
                hideKeyBoard(et_alias)
                hideKeyBoard(et_dia)
                goToSaveReminder()
            }
        }
    }

    fun setInfoToReminder() {
        reminder.alias = et_alias.text.toString()
        reminder.diaInicio = et_dia.text.toString().trim().toInt()
        reminder.periodo= spnPeriodicity2.selectedItem.toString()
    }

    fun setType(type: String) {
        this.type = type
    }

    protected fun hideKeyBoard(view: View) {
        val imm: InputMethodManager = getContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun goToUpdateReminder() {
        //No Implementation
    }

    fun goToSaveReminder(){
        favouritesServices?.altaFavorito(object : RetrofitListener<Boolean> {
            override fun showMessage(s: String) {
                //No implementation
            }
            override fun onFailure(throwable: Throwable) {
                homeActivity!!.ocultaProgressDialog()
                Logger.LOGGER.throwing("", 1, throwable, throwable.message)
                isSuccess = false
                message = getString(R.string.dialog_save_reminder_failed)
                closeDialog()
            }
            override fun onSuccess(altaExitosa: Boolean) {
                homeActivity!!.ocultaProgressDialog()
                if(altaExitosa){
                    isSuccess = true
                    message = getString(R.string.dialog_save_reminder_success)
                    //closeDialog()
                    homeActivity!!.despliegaModal(
                            false,
                            false,
                            "¡Alta Exitosa!",
                            getString(R.string.dialog_save_reminder_success
                            ),
                            {
                                closeDialog()
                                (activity as HomeActivity?)!!.goToCalendarFromProducts()
                            })
                }else{
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

    fun setReminder(reminder: FavoritoBean) {
        this.reminder = reminder
    }

    private fun getIcon(){
        if(reminder.procod!=null){
            var producto =
                    SigmaBdManager.getProducto(
                            reminder.procod,
                            OnFailureListener.BasicOnFailureListener(
                            "Error PRODUCTO",
                            "Error al Obtener Producto"
                            )
                    )

            if(producto!=null && producto.icono!=null){
                var icono =
                        SigmaBdManager.getIcono(
                                producto.icono,
                                OnFailureListener.BasicOnFailureListener(
                                    "Error ICONO",
                                    "Error al Obtener Icono"
                                )
                        )

                if(icono!=null) {
                    iv_icon_dialog.setImageDrawable(Utilities.getIcono(icono.ruta))
                }
            }
        }
    }

    protected fun showKeyboard() {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT)
        imm.showSoftInput(activity!!.findViewById(R.id.content), InputMethodManager.SHOW_IMPLICIT)
    }

    fun setMessageListener(onMessageToSnackBarListener: OnMessageToSnackBarListener) {
        this.onMessageToSnackBarListener = onMessageToSnackBarListener
    }

    interface OnMessageToSnackBarListener {
        fun onMessageToSnackBarListener(boolean: Boolean, message: String)
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

}