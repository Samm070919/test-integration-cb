package com.pagatodoholdings.posandroid.secciones.servicios

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.pagatodo.sigmalib.util.SigmaUtil
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextPassword
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment
import com.pagatodoholdings.posandroid.databinding.FragmentPagoServiciosBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref
import com.pagatodoholdings.posandroid.secciones.acquiring.SettingsPref.ESTA_AUTORIZADO
import com.pagatodoholdings.posandroid.secciones.dialogs.PDSErrorDialog
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosTPV
import com.pagatodoholdings.posandroid.secciones.retrofit.UsuarioValidaPassService
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.ManejadorFragments
import com.pagatodoholdings.posandroid.utils.Utilities
import com.pagatodoholdings.posandroid.utils.enums.MediosPago
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.LiteralesOperacion
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import net.fullcarga.android.api.data.respuesta.AbstractRespuesta
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno
import net.fullcarga.android.api.formulario.Parametro
import net.fullcarga.android.api.oper.TipoOperacion
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class PagoServiciosFragment(icono: Drawable,
                            monto: String,
                            numeroRef: String,
                            numeroTel: String,
                            cargoServicio: BigDecimal,
                            tOperacion: TipoOperacion,
                            mOperacion: Operaciones,
                            val parametros: Map<String, Parametro>
        //producto : Productos
) : AbstractFragment() {

    //se define los valores de los textos
    private val integerSize = 22.0f
    private val decimalSize = 12.0f
    private val currencySize = 14.0f

    //define monto,referencia,cargo
    private var iconoServicio: Drawable

    private var numeroReferencia: String
    private var numeroTelefono: String
    private val cargoPorServicio: BigDecimal

    //operacion
    private var datosOperacion: TransaccionesBD? = null
    protected var producto: Productos? = null
    private var tipoOperacion: TipoOperacion
    private var operacion: Operaciones = mOperacion
    protected var literal: LiteralesOperacion? = null

    //variable
    private lateinit var montoTotal: String

    //Preferencia
    private var pref: SettingsPref? = null

    //binding
    lateinit var binding: FragmentPagoServiciosBinding
    private lateinit var montoServicio: String
    private var homeActivity: HomeActivity? = null
    private lateinit var manejadorFragments: ManejadorFragments
    private val fromPDS = "PDS"

    private val TAG = AbstractFragment::class.java.simpleName

    init {
        iconoServicio = icono
        numeroReferencia = numeroRef
        numeroTelefono = numeroTel
        cargoPorServicio = cargoServicio
        tipoOperacion = tOperacion
        operacion = mOperacion

        montoServicio = monto
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivity = getActivity() as HomeActivity

        literal = SigmaBdManager.getLiteralOperacion(
                operacion,
                object : OnFailureListener.BasicOnFailureListener(
                        TAG,
                        "ErrorAlConsultar") {
                })

        producto = SigmaBdManager.getProducto(
                operacion,
                object : OnFailureListener.BasicOnFailureListener(
                        TAG,
                        "ErrorAlConsultar") {
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPagoServiciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SettingsPref.getInstance()//instancia de la preferencia
        manejadorFragments = ManejadorFragments(homeActivity!!.supportFragmentManager)
        initUI()
    }

    @SuppressLint("NewApi")
    fun initUI() {

        //se llena la barra del menu
        binding.formularioHeader.toolbar4.setNavigationIcon(R.drawable.ic_icono_back)
        binding.formularioHeader.tvTitulo.text = getString(R.string.subtitulo1_condiciones)
        binding.formularioHeader.toolbar4.setNavigationOnClickListener(View.OnClickListener { close() })
        //se llena el saldo y se asigna el icono del servicio
        binding.ivIconServicio.setImageDrawable(iconoServicio)
        if (homeActivity!!.saldo != null) {
            binding.tvSaldoConfirmacion.setMonto( SigmaBdManager.formatoSaldo( homeActivity!!.saldo, BasicOnFailureListener(this.TAG, "Error al obtener ")))
        }
        //fecha
        binding.tvFecha.text = getDate()


        if(isConvertible(montoServicio)){
            binding.llMontoServicio.visibility = View.VISIBLE
            binding.llCargoServicio.visibility = View. VISIBLE
            binding.llMontoTotal.visibility = View.VISIBLE

            binding.tvMontoServicio.setMonto(montoServicio)
            binding.tvMontoCargo.setMonto(cargoPorServicio.toPlainString())
            binding.tvMontoTotal.setMonto( (BigDecimal(montoServicio).add(cargoPorServicio)).toPlainString() )
            montoTotal = (BigDecimal(montoServicio).add(cargoPorServicio)).toPlainString()
        }else{
            binding.llMontoServicio.visibility = View.GONE
            binding.llCargoServicio.visibility = View. GONE
            binding.llMontoTotal.visibility = View.GONE
        }

        binding.tvReferenciaConfirmacion.text = numeroReferencia.chunked(4).joinToString(" ")
        //boton confirmacion
        binding.btnPagarConfirmacion.setOnClickListener {

            if (Utilities.isNumeric(montoServicio)) {
                if ((homeActivity!!.saldo.compareTo(BigDecimal(montoTotal)) == -1)
                ) {
                    val insufficientBalanceDialog =
                            PDSErrorDialog("from_pagos_servicios", homeActivity)
                    val fm = activity.supportFragmentManager
                    insufficientBalanceDialog.show(fm, "Error dialog")
                } else {
                    dialogoAutorizacion()
                }
            }else{
                dialogoAutorizacion()
            }
        }

        if(parametros.size > 1){
            binding.llTelefono.visibility = View.VISIBLE
            var index = 0
            for(param in parametros){
                when(index){
                    0 -> {
                        binding.tvReferenciaConfirmacion.text = param.key
                        binding.tvReferenciaTitle.text = (parametros[param.key]
                                ?: error("")).literal
                        }
                    1 -> {
                        binding.tvTelefonoConfirmacion.text = param.key
                        binding.tvTelefonoTitle.text = (parametros[param.key]
                                ?: error("")).literal
                    }
                }
                index++
                if(index >= 2)
                    break
            }
        }
    }

    private fun close() {
        getActivity()!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    fun isConvertible (montoServicio:String):Boolean{
        try{
            montoServicio.toBigDecimal()
            return true
        }catch ( exc: NumberFormatException){
            return false
        }
    }

    override fun onFailure(p0: Throwable?) {
        Logger.LOGGER.throwing(p0!!.message, 1, p0, getString(R.string.error_con_bd))
    }

    override fun isTomandoBack(): Boolean {
        return false
    }

    //obtener fecha
    fun getDate(): String {
        var date = ""
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        date = "" + day + " de " + getMonthText(month) + " " + year
        return date
    }

    //obetener mes con el formato
    private fun getMonthText(monthNumber: Int) = when (monthNumber) {
        0 -> "Enero"
        1 -> "Febrero"
        2 -> "Marzo"
        3 -> "Abril"
        4 -> "Mayo"
        5 -> "Junio"
        6 -> "Julio"
        7 -> "Agosto"
        8 -> "Septiembre"
        9 -> "Octubre"
        10 -> "Noviembre"
        11 -> "Diciembre"
        else -> ""
    }

    //hacer la transaccion del cobro
    private fun doTransaction() {
        tipoOperacion = getTipoOperacion(operacion)
        datosOperacion = TransaccionesBD()


        if(isConvertible(montoServicio)) {
            datosOperacion?.total = montoTotal
            datosOperacion?.importe = montoServicio.toString()
            datosOperacion?.impuesto = cargoPorServicio.toFloat()
        }

        val transaccion = TransaccionFactory.crearTransacion<AbstractTransaccion>(
                tipoOperacion,
                { abstractRespuesta: AbstractRespuesta ->
                    if (abstractRespuesta.isCorrecta) {
                        Utilities.analyticsLogEventVenta(getActivity(), firebaseAnalytics)
                        homeActivity!!.ocultaProgressDialog()
                        if (abstractRespuesta is RespuestaTrxCierreTurno) {
                            openPaySuccessfull(abstractRespuesta, operacion)
                        }
                    } else {
                        homeActivity!!.ocultaProgressDialog()
                        homeActivity!!.despliegaModal(true, false, "Error", abstractRespuesta.msjError) {
                            (activity as HomeActivity).regresaMenu()
                        }
                        Logger.LOGGER.throwing(TAG, 1, Throwable(), abstractRespuesta.msjError)
                    }
                }) { throwable: Throwable ->
            homeActivity!!.ocultaProgressDialog()
            homeActivity!!.despliegaModal(true, false, "Error", throwable.message) {
                (activity as HomeActivity).regresaMenu()
            }
            Logger.LOGGER.throwing(TAG, 1, throwable, throwable.message)
        }

        val fields = ArrayList<String>()
        if(parametros.size > 1){
            for (param in parametros){
                fields.add(param.key)
            }
        }else {
            fields.add(numeroReferencia)
        }
        transaccion.withFields(fields)
        transaccion.withProcod(operacion.producto)
        Logger.LOGGER.info(TAG, datosOperacion.toString())
        homeActivity!!.muestraProgressDialog(getString(R.string.cargando))
        transaccion.realizarOperacion()
    }

    private fun dialogoAutorizacion() {

        val dialog = Dialog(activity, R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_autorizacion_compra)
        val etPass = dialog.findViewById(R.id.et_pass_autorizacion) as EditTextPassword
        val yesBtn = dialog.findViewById(R.id.btn_continuar_autorizacion) as BotonClickUnico

        yesBtn.setOnClickListener {
            if (etPass.texto.trim().isEmpty() || etPass.texto.trim().length < 6) {
                showErrorDialog(true, false, "Error en la Contraseña",
                        "Ingrese una Contraseña Correcta", ModalFragment.CommonDialogFragmentCallBack { })
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
                        doTransaction()
                    }

                    override fun onFailure(throwable: Throwable) {
                        homeActivity?.ocultaProgressDialog()
                        Logger.LOGGER.throwing(TAG, 1, throwable
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

    private fun cleanAmount(field: String, numberFormat: NumberFormat): String {
        val amount = SigmaUtil.cleanImporteInput(
                montoTotal,
                numberFormat
        )

        if (validaMonto(montoTotal, amount)) {
            homeActivity!!.ocultaProgressDialog()
        }

        datosOperacion!!.importe = amount.toString()
        val field1 = ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(amount.toString())

        return field1
    }

    private fun openPaySuccessfull(abstractRespuesta: RespuestaTrxCierreTurno, operacion: Operaciones) {
        datosOperacion?.reflocal = abstractRespuesta.camposCierreTurno.refLocal
        datosOperacion?.refremota = abstractRespuesta.camposCierreTurno.refRemota
        datosOperacion?.refcliente = abstractRespuesta.camposCierreTurno.refCliente
        datosOperacion?.descproducto = abstractRespuesta.camposCierreTurno.descripcionProducto
        datosOperacion?.importe = abstractRespuesta.camposCierreTurno.importe.toString()
        datosOperacion?.medioPago = MediosPago.OTRO

        val mProducto = SigmaBdManager.getProducto(operacion, OnFailureListener.BasicOnFailureListener(TAG, ""))
        Utilities.guardarSigmaTransacciones(datosOperacion, operacion, mProducto.cierreTurno)
        val pagoExitosoFragment = PaySucessfulPDSFragment(
                fromPDS,
                iconoServicio,
                BigDecimal(ApiData.APIDATA.datosSesion.datosTPV.rellenarImporte(abstractRespuesta.camposCierreTurno.importe))
                ,
                abstractRespuesta.camposCierreTurno.refCliente.trim(),
                abstractRespuesta.camposCierreTurno.refLocal.trim(),
                abstractRespuesta.camposCierreTurno.refRemota.trim(),
                producto!!,
                operacion,
                abstractRespuesta
        )
        manejadorFragments.cargarFragmentPantallaCompleta(pagoExitosoFragment, homeActivity)
    }

    private fun showErrorDialog(
            esDeError: Boolean,
            tieneCancelar: Boolean,
            cabecera: String,
            cuerpo: String,
            callback: ModalFragment.CommonDialogFragmentCallBack) {
        ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody(cuerpo)
                .setSingleButton(!tieneCancelar)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(false)
                .ponEsError(esDeError)
                .ponInterfaceCallBack(callback)
                .build()
                .show(this.fragmentManager!!, "PDS")
    }

    private fun getTipoOperacion(operacion: Operaciones): TipoOperacion {
        return when (operacion.operacion) {
            com.pagatodoholdings.posandroid.utils.Constantes.VENTA -> TipoOperacion.VENTA
            com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_X -> TipoOperacion.CONSULTA_X
            com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_Z -> TipoOperacion.CONSULTA_Z
            com.pagatodoholdings.posandroid.utils.Constantes.DEVOLUCION -> TipoOperacion.DEVOLUCION
            else -> null!!
        }
    }

    private fun validaMonto(editText: String, monto: BigDecimal): Boolean {
        val impUtils = ImportesUtils(
                monto,
                BigDecimal(producto!!.importeMin.toString()),
                BigDecimal(producto!!.importeMax.toString()),
                BigDecimal(producto!!.incremento.toString())
        )
        if (impUtils.isMayor) {
            //editText.requestFocus()
            //editText.setError(activity!!.getString(R.string.Invalidate_Importe_Mayor))
            return true
        } else if (impUtils.isMenor) {
            //editText.requestFocus()
            //editText.setError(activity!!.getString(R.string.Invalidate_Importe_Menor))
            return true
        } else if (!impUtils.isMultiplo) {
            //editText.requestFocus()
            //editText.setError(activity!!.getString(R.string.Invalidate_Importe_Incremento))
            return true
        }
        return false
    }
}