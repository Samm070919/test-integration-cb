package com.pagatodoholdings.posandroid.secciones.servicios

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodo.sigmalib.reportetrx.TransaccionesBD
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion
import com.pagatodo.sigmalib.transacciones.TransaccionFactory
import com.pagatodo.sigmalib.util.Constantes
import com.pagatodo.sigmalib.util.ImportesUtils
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.*
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment.CommonDialogFragmentCallBack
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ESTA_AUTORIZADO
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosTPV
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.UsuarioValidaPassService
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.ManejadorFragments
import com.pagatodoholdings.posandroid.utils.Utilities
import com.pagatodoholdings.posandroid.utils.capitalizeWords
import com.pagatodoholdings.posandroid.utils.enums.MediosPago
import com.santalu.maskedittext.MaskEditText
import kotlinx.android.synthetic.main.dialog_tae_confirmacion.*
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.LiteralesOperacion
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import net.fullcarga.android.api.data.respuesta.AbstractRespuesta
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno
import net.fullcarga.android.api.formulario.Formato
import net.fullcarga.android.api.formulario.Parametro
import net.fullcarga.android.api.oper.TipoOperacion
import org.apache.commons.lang3.text.WordUtils
import java.math.BigDecimal
import java.util.*

class DialogTaeConfirmacion(
        private var icono: Drawable,
        private var parametros:  Map<String,Parametro>,
        private var operacion: Operaciones,
        private var flag: Boolean
) : DialogFragment() {
    private var datosOperacion: TransaccionesBD? = null
    private var tipoOperacion: TipoOperacion? = null
    protected var firebaseAnalytics: FirebaseAnalytics? = null
    protected var aActivity: AbstractActivity? = null
    protected var literal: LiteralesOperacion? = null
    protected var producto: Productos? = null
    private var homeActivity: HomeActivity? = null
    private var pref: SettingsPref? = null
    private var numRef: String = "";
    protected var preferenceManager: PreferenceManager? = null
    protected var manejadorFragments: ManejadorFragments? = null
    private val TAG = AbstractFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
        isCancelable = false
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        aActivity = activity as AbstractActivity?
        literal =
                SigmaBdManager.getLiteralOperacion(
                        operacion,
                        object : BasicOnFailureListener(
                                TAG,
                                "ErrorAlConsultar"
                        ) {

                        })

        producto =
                SigmaBdManager.getProducto(
                        operacion,
                        object : BasicOnFailureListener(
                                TAG,
                                "ErrorAlConsultar"
                        ) {

                        })

        homeActivity = activity as HomeActivity
        preferenceManager = MposApplication.getInstance().preferenceManager
        manejadorFragments = ManejadorFragments(activity!!.supportFragmentManager)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_tae_confirmacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager


        addParams()


        iv_icon_dialog.setImageDrawable(icono)

        btn_regresar.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_recargar.setOnClickListener {
            pref = SettingsPref.getInstance()

            dialogoAutorizacion()

            dismiss()

        }
    }

    fun executeOperacion() { //NOSONAR
        tipoOperacion = getTipoOperacion(operacion)
        datosOperacion = TransaccionesBD()
        //datosOperacion?.importe = monto

        val transaccion = TransaccionFactory.crearTransacion<AbstractTransaccion>(
                tipoOperacion,
                { abstractRespuesta: AbstractRespuesta ->
                    if (abstractRespuesta.isCorrecta) {
                        Utilities.analyticsLogEventVenta(activity, firebaseAnalytics)
                        aActivity!!.ocultaProgressDialog()
                        if (abstractRespuesta is RespuestaTrxCierreTurno) {
                            successVenta(abstractRespuesta)
                        }/* else {
                            successConsulta(abstractRespuesta as RespuestaTrx)
                        }*/
                    } else {
                        aActivity!!.ocultaProgressDialog()
                        val delimiters = charArrayOf(' ', '_')
                        val mensajeError = WordUtils.capitalizeFully(abstractRespuesta.msjError, *delimiters)
                        aActivity!!.despliegaModal(
                                true,
                                false,
                                literal!!.valor,
                                mensajeError,
                                CommonDialogFragmentCallBack {
                                    //aFragment!!.hideViewElements(binding.clContenedor)
                                    (aActivity as HomeActivity).regresaMenu()
                                })
                        Logger.LOGGER.throwing(
                                TAG,
                                1,
                                Exception(abstractRespuesta.msjError),
                                abstractRespuesta.msjError)
                    }
                }) { throwable: Throwable ->
            aActivity!!.ocultaProgressDialog()
            val delimiters = charArrayOf(' ', '_')
            val mensajeThrowable = WordUtils.capitalizeFully(throwable.message, *delimiters)
            aActivity!!.despliegaModal(
                    true,
                    false,
                    literal!!.valor,
                    mensajeThrowable,
                    CommonDialogFragmentCallBack {
                        //hideViewElements(binding.clContenedor)
                        homeActivity?.regresaMenu()
                    })
            Logger.LOGGER.throwing(TAG, 1, throwable, throwable.message)
        }

        val fields = ArrayList<String>()
        val numberFormat = SigmaBdManager.getNumberFormat(
                BasicOnFailureListener(
                        "AbstractFragment",
                        homeActivity!!.getString(R.string.text_format_error)
                )
        )

        for (parametro in parametros.keys) {
            Logger.LOGGER.fine(TAG,Gson().toJson(parametro))
            fields.add(parametro)
        }
        transaccion.withFields(fields)
        transaccion.withProcod(operacion.producto)
        Logger.LOGGER.info(TAG, datosOperacion.toString())
        Logger.LOGGER.info(TAG, datosOperacion.toString())
        //aActivity!!.muestraProgressDialog(getString(R.string.cargando))
        transaccion.realizarOperacion()
    }

    private fun getTipoOperacion(operacion: Operaciones): TipoOperacion? {
        return when (operacion.operacion) {
            com.pagatodoholdings.posandroid.utils.Constantes.VENTA -> TipoOperacion.VENTA
            com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_X -> TipoOperacion.CONSULTA_X
            com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_Z -> TipoOperacion.CONSULTA_Z
            com.pagatodoholdings.posandroid.utils.Constantes.DEVOLUCION -> TipoOperacion.DEVOLUCION
            else -> null
        }
    }

    private fun successVenta(result: RespuestaTrxCierreTurno) { //NOSONAR +Local Se usa correctamente por el Api minima
        datosOperacion?.reflocal = result.camposCierreTurno.refLocal
        datosOperacion?.importe = result.camposCierreTurno.importe.toString()
        datosOperacion?.refcliente = result.camposCierreTurno.refCliente
        datosOperacion?.descproducto = result.camposCierreTurno.descripcionProducto
        datosOperacion?.medioPago = MediosPago.OTRO
        ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(result.camposCierreTurno.importe.toPlainString())
        var importeResultado: BigDecimal = BigDecimal(ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(result.camposCierreTurno.importe))
        val mProducto = SigmaBdManager.getProducto(operacion, BasicOnFailureListener(TAG, ""))
        Utilities.guardarSigmaTransacciones(datosOperacion, operacion, mProducto.cierreTurno)
        val pagoExitosoFragment = PaySucessfulPDSFragment(
                FROM_TAE,
                icono,
                importeResultado
                ,
                result.camposCierreTurno.refCliente.trim(),
                result.camposCierreTurno.refLocal.trim(),
                result.camposCierreTurno.refRemota.trim(),
                producto!!,
                operacion,
                result
        )
        manejadorFragments!!.cargarFragmentPantallaCompleta(pagoExitosoFragment, homeActivity)
    }

    private fun validaMonto(editText: MontoViewCustom, monto: BigDecimal): Boolean {
        val impUtils = ImportesUtils(
                monto,
                BigDecimal(producto!!.importeMin.toString()),
                BigDecimal(producto!!.importeMax.toString()),
                BigDecimal(producto!!.incremento.toString())
        )
        if (impUtils.isMayor) {
            editText.requestFocus()
            editText.setError(activity!!.getString(R.string.Invalidate_Importe_Mayor))
            return true
        } else if (impUtils.isMenor) {
            editText.requestFocus()
            editText.setError(activity!!.getString(R.string.Invalidate_Importe_Menor))
            return true
        } else if (!impUtils.isMultiplo) {
            editText.requestFocus()
            editText.setError(activity!!.getString(R.string.Invalidate_Importe_Incremento))
            return true
        }
        return false
    }

    private fun validaMonto(monto: BigDecimal): Boolean {
        val impUtils = ImportesUtils(
                monto,
                BigDecimal(producto!!.importeMin.toString()),
                BigDecimal(producto!!.importeMax.toString()),
                BigDecimal(producto!!.incremento.toString())
        )
        if (impUtils.isMayor) {
            Toast.makeText(
                    homeActivity,
                    activity!!.getString(R.string.Invalidate_Importe_Mayor),
                    Toast.LENGTH_SHORT
            ).show()
            return true
        } else if (impUtils.isMenor) {
            Toast.makeText(
                    homeActivity,
                    activity!!.getString(R.string.Invalidate_Importe_Menor),
                    Toast.LENGTH_SHORT
            ).show()
            return true
        } else if (!impUtils.isMultiplo) {
            Toast.makeText(
                    homeActivity,
                    activity!!.getString(R.string.Invalidate_Importe_Incremento),
                    Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }

//    private fun cleanAmount(field: String, numberFormat: NumberFormat): String {
//        val amount = SigmaUtil.cleanImporteInput(
//                monto,
//                numberFormat
//        )
//
//        if (validaMonto(amount)) {
//            aActivity!!.ocultaProgressDialog()
//        }
//
//        datosOperacion!!.importe = amount.toString()
//
//        val field1 = ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(amount.toString())
//
//        return field1
//    }

//    private fun cleanData(field: String, tipo: Formato.Tipo, numberFormat: NumberFormat): String {
//        if (tipo == Formato.Tipo.IMPORTE || tipo == Formato.Tipo.IMPORTE_SIN_VAL) {
//            val amount = SigmaUtil.cleanImporteInput(
//                    et_monto_recarga
//                            .editText
//                            .text
//                            .toString(),
//                    numberFormat
//            )
//
//            if (validaMonto(et_monto_recarga, amount)) {
//                aActivity!!.ocultaProgressDialog()
//            }
//
//            datosOperacion!!.importe = amount.toString()
//
//            val field1 = ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(amount.toString())
//            return field1
//        } else {
//            return field
//        }
//    }

    private fun dialogoAutorizacion() {

        val dialog = Dialog(activity as FragmentActivity, R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_autorizacion_compra)
        val etPass = dialog.findViewById(R.id.et_pass_autorizacion) as EditTextPassword
        val yesBtn = dialog.findViewById(R.id.btn_continuar_autorizacion) as BotonClickUnico

        yesBtn.setOnClickListener {
            if (etPass.texto.trim().isEmpty() || etPass.texto.trim().length < 6) {
                showErrorDialog(
                        true,
                        false,
                        "Error en la Contraseña",
                        "Ingrese una Contraseña Correcta",
                        ModalFragment.CommonDialogFragmentCallBack { }
                )
            } else {
                dialog.dismiss()
                cargarConfirmacion(etPass.texto.toString())
            }
        }
        dialog.show()
    }

    private fun cargarConfirmacion(password: String) {

        homeActivity?.muestraProgressDialog("Cargando")

        val service = UsuarioValidaPassService()
        service.getPassUser(
                MposApplication.getInstance().datosLogin.cliente.email.toString(),
                password,
                preferenceManager!!.getValue(Constantes.Preferencia.NUMERO_SERIE, ""),
                object : RetrofitListener<DatosTPV> {

                    override fun onSuccess(p0: DatosTPV?) {
                        homeActivity?.ocultaProgressDialog()
                        pref!!.saveData(ESTA_AUTORIZADO, true)

                        kotlin.run {
                            executeOperacion()
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        homeActivity?.ocultaProgressDialog()
                        Logger.LOGGER.throwing("OBTENER VALIDACION", 1, throwable
                                , "Error al validar usuario")
                    }

                    override fun showMessage(p0: String?) {
                        showErrorDialog(true,
                                false,
                                "Error en la Contraseña",
                                "Ingrese una Contraseña Correcta",
                                ModalFragment.CommonDialogFragmentCallBack { })
                    }

                })
    }

    private fun showErrorDialog(
            esDeError: Boolean,
            tieneCancelar: Boolean,
            cabecera: String,
            cuerpo: String,
            callback: ModalFragment.CommonDialogFragmentCallBack) {
        val transaction = homeActivity!!.supportFragmentManager.beginTransaction()
        ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody(cuerpo)
                .setSingleButton(!tieneCancelar)
                .setAccept("Aceptar")
                .setCancel("Cancelar")
                .setCanceledOnTouchOutside(false)
                .ponEsError(esDeError)
                .ponInterfaceCallBack(callback)
                .build()
                .show(transaction, "TAE")
    }

    private fun goToSaveFavorites() {
        val newReminder = FavoritoBean()
        newReminder.procod = getProduct(operacion)?.codigo
        val transaction = homeActivity!!.supportFragmentManager.beginTransaction()
        val dialogGuardarServicio = DialogGuardarServicio(icono, getProduct(operacion)!!)
        dialogGuardarServicio.setReminder(newReminder)
        dialogGuardarServicio.setType("Añadir")
        dialogGuardarServicio.setOnDismissListener(DialogInterface.OnDismissListener { })
        dialogGuardarServicio.setMessageListener(object : DialogGuardarServicio.OnMessageToSnackBarListener {
            override fun onMessageToSnackBarListener(boolean: Boolean, message: String) {
                homeActivity?.onBackPressed()
            }
        })
        dialogGuardarServicio.show(transaction, "DialogGuardarServicio")
    }

    private fun getProduct(operacion: Operaciones): Productos? {
        return SigmaBdManager.getProducto(operacion,
                BasicOnFailureListener(TAG, "ErrorAlConsultar"))
    }

    companion object {
        const val FROM_TAE: String = "TAE"
    }


    private fun addParams() {


        for (entry in parametros.entries) {
            val key: String = entry.key
            val value: Parametro = entry.value
            // ...

            Log.e("tipo", value.formato.tipo.toString())
            Log.e("literal", value.literal)
            if (value.formato.tipo == Formato.Tipo.NUMERICO) {
                if (value.literal.toLowerCase().contains("referencia de pago")) {
                    val textView: TextView
                    textView = TextView(context, null)
                    textView.setText(R.string.txt_referencia_unica)
                    params.addView(textView)
                    val view: InputNumeroServicioUnico
                    view = getEditTextNumeroServicio(context, null)
                    view.initData(value)

                    view.texto = key
                    params.addView(view)
                } else if (value.literal == "PROMO CODE" || value.literal == "DNI" || value.literal == "VALE" || value.literal == "DNI CANJEADOR" || value.literal == "COMPROBANTE" || value.literal == "ID PAGO" || value.literal == "SUMINISTRO" || value.literal == "COD. CLIENTE" || value.literal == "COD CLIENTE" || value.literal == "ID PAGO" || value.literal == "Planes") {
                    val view: EditTextFormulario
                    view = getEditTextFormulario(context, null)
                    view.initData(value)
                    view.setTextoFijo(key)
                    params.addView(view)
                } else {
                    val textView: TextView
                    textView = TextView(context, null)
                    if (value.literal.toLowerCase().contains("numero celular") || value.literal == "TELEFONO") {
                        textView.setText(R.string.numero_telefonico)
                    } else {
                        textView.text = capitalizeWords(value.literal)
                    }
                    textView.setTextColor(resources.getColor(R.color.textHintField))
                    params.addView(textView)
                    val view: EditTextFormulario
                    view = getEditTextFormulario(context, null)
                    view.initData(value)
                    view.setTextoFijo(key)
                    params.addView(view)
                }
            } else if (value.formato.tipo == Formato.Tipo.TELEFONO) {
                val textView: TextView
                textView = TextView(context, null)
                textView.setText(R.string.numero_telefonico)
                textView.setTextColor(resources.getColor(R.color.grey_lines))
                params.addView(textView)
                //view.initData(param);
                if (Country.PERU.itemIsoCode == MposApplication.getInstance().datosLogin.pais.codigo) {
                    val view: MaskEditText
                    view = getMaskEditText(context, null)
                    view.inputType = InputType.TYPE_CLASS_PHONE
                    view.mask = Country.PERU.itemMask
                    view.editableText.append(key)
                    params.addView(view)
                } else if (Country.COLOMBIA.itemIsoCode == MposApplication.getInstance().datosLogin.pais.codigo) {
                    val view: MaskEditText
                    view = getMaskEditText(context, null)
                    view.inputType = InputType.TYPE_CLASS_PHONE
                    view.mask = Country.COLOMBIA.itemMask
                    view.editableText.append(key)
                    params.addView(view)
                }
                val recyclerView: RecyclerView
                recyclerView = RecyclerView(context!!, null)
                params.addView(recyclerView)
            } else if (value.formato.tipo == Formato.Tipo.TELEFONO_USUARIO) {
                val textView: TextView
                textView = TextView(context, null)
                textView.setText(R.string.numero_telefonico)
                params.addView(textView)
                if (Country.PERU.itemIsoCode == MposApplication.getInstance().datosLogin.pais.codigo) {
                    val view: MaskEditText
                    view = getMaskEditText(context, null)
                    view.inputType = InputType.TYPE_CLASS_PHONE
                    view.mask = Country.PERU.itemMask
                    view.editableText.append(key)
                    params.addView(view)
                } else if (Country.COLOMBIA.itemIsoCode == MposApplication.getInstance().datosLogin.pais.codigo) {
                    val view: MaskEditText
                    view = getMaskEditText(context, null)
                    view.inputType = InputType.TYPE_CLASS_PHONE
                    view.mask = Country.COLOMBIA.itemMask
                    view.editableText.append(key)
                    params.addView(view)
                }
            } else if (value.formato.tipo == Formato.Tipo.ENUMERACION) {
                val spinnerFormulario: SpinnerFormulario = getSpinnerFormulario(context!!, value)!!
                params.addView(spinnerFormulario)
            } else if (value.formato.tipo == Formato.Tipo.ALFANUMERICO) {
                val view: EditTextFormulario
                view = getEditTextFormulario(context, null)
                view.initData(value)
                view.setTextoFijo(key)
                params.addView(view)
                val recyclerView: RecyclerView
                recyclerView = RecyclerView(context!!, null)
                params.addView(recyclerView)
            }
            else if( value.formato.tipo == Formato.Tipo.IMPORTE  || value.formato.tipo == Formato.Tipo.IMPORTE_SIN_VAL  ){
                val textView: TextView
                textView = TextView(context, null)
                textView.setText("Importe")
                textView.setTextColor(resources.getColor(R.color.grey_lines))
                params.addView(textView)

                var Importe =   SigmaBdManager.formatoSaldo(ApiData.APIDATA.datosSesion.datosTPV.convertirImporte( key), OnFailureListener {
                    //None
                })
                var  parametroMoneda = SigmaBdManager.getParametroFijo("0021", BasicOnFailureListener(this.javaClass.simpleName, "Error al obtener parametro fijo"))

                val textViewimporte: TextView
                textViewimporte = TextView(context, null)
                textViewimporte.setText(Importe.replace(parametroMoneda, " " + parametroMoneda + " "))
                textViewimporte.setTextColor(resources.getColor(R.color.colorAzulImporte))
                textViewimporte.setTextSize(20F)
                params.addView(textViewimporte)
                val recyclerView: RecyclerView
                recyclerView = RecyclerView(context!!, null)
                params.addView(recyclerView)
            }
        }

    }

    private fun getSpinnerFormulario(context: Context, param: Parametro): SpinnerFormulario? {
        return SpinnerFormulario(context, param)
    }

    private fun getEditTextPhoneNumber(
            context: Context,
            attrs: AttributeSet
    ): InputPhoneNumber {
        return InputPhoneNumber(context, attrs)
    }

    private fun getEditTextNumeroServicio(
            context: Context?,
            attrs: AttributeSet?
    ): InputNumeroServicioUnico {
        return InputNumeroServicioUnico(context, attrs)
    }

    private fun getEditTextFormulario(
            context: Context?,
            attrs: AttributeSet?
    ): EditTextFormulario {
        return EditTextFormulario(context, attrs)
    }

    private fun getMaskEditText(
            context: Context?,
            attrs: AttributeSet?
    ): MaskEditText {
        return MaskEditText(context!!, attrs)
    }


}