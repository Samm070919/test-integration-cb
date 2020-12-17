package com.pagatodoholdings.posandroid.secciones.movimientos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.reportetrx.Model
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.recargas.SendTicketEmailDialog
import com.pagatodoholdings.posandroid.utils.Constantes
import com.pagatodoholdings.posandroid.utils.Utilities
import com.pagatodoholdings.posandroid.utils.UtilsDate
import com.pagatodoholdings.posandroid.utils.enums.ProductCategory
import kotlinx.android.synthetic.main.fragment_detalle_movimiento.view.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class MisMovimientosDialog : AbstractFragment() {
    private lateinit var inflatedView: View
    private var model: Model? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflatedView = inflater.inflate(R.layout.fragment_detalle_movimiento, container, false)
        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedView.textConcept.isSelected = true

        inflatedView.imgButtonBack.setOnClickListener {
            val homeActivity = activity as? HomeActivity
            homeActivity?.supportFragmentManager?.popBackStack()
        }

        inflatedView.btnTerminar.setOnClickListener {
            val dialog = SendTicketEmailDialog()
            dialog.isCancelable = false
            dialog.show((activity as HomeActivity).supportFragmentManager, "SendTicketEmailDialog")
        }

        model?.let {
            inflatedView.tvFechaPago.text = getFormattedDate(it.fecha)

            val bigDecimal = if (it.importe != null) {
                BigDecimal(it.importe)
            } else {
                BigDecimal.ZERO
            }
            val currencyImport = Utilities.getFormatedImportObject(bigDecimal)
            inflatedView.tvMontoPagado.setMonto(currencyImport)
            inflatedView.tvMontoPagado.setNewTextSizeIndividualMonto(16f, 32f, 22f)
//            val numberFormatter = SigmaBdManager.getNumberFormat(BasicOnFailureListener(TAG, "Error al obtener el formatedor de saldo"))

            if (it.proCod == "SalidaDinero") {
                onMoneyOutDetected(it)
            } else if (it.proCod == "RecargaTarjeta") {
                onCardFillDetected(it)
            } else if (it.proCod == "PAGO_QR") {
                onQrPayDetected(it)
            } else if (it.proCod == "CompraKit") {
                onKitDetected(it)
            } else if (model!!.codigoOper.equals(Constantes.DEVOLUCION, ignoreCase = true)) {
                onPayCanceled(it)
            } else {
                val producto = SigmaBdManager.getProducto(it.proCod, this)

                producto.icono?.let { aInteger ->
                    val icon = SigmaBdManager.getIcono(aInteger, this)
                    val drawable = Utilities.getIcono(icon.ruta)
                    inflatedView.icono.setImageDrawable(drawable)
                }

                when (producto.categoria) {
                    ProductCategory.NA,
                    ProductCategory.TAE,
                    ProductCategory.OTRAS_RECARGAS -> onTaeDetected(it)
                    ProductCategory.PDS_EXTRA,
                    ProductCategory.PDS -> onPdsDetected(it)
                    ProductCategory.MDP_REGULADOS -> onCardSaleDetected(it)
                    else -> {
                        inflatedView.labelRefUnq.text = "Referencia Única"
                        inflatedView.labelAuthEmisor.text = "Referencia Local"
                        inflatedView.labelFolio.text = "Referencia Remota"

                        inflatedView.textRefUnq.text = model?.descProducto
                        inflatedView.textAuthEmisor.text = model?.refCliente
                        inflatedView.textFolio.text = model?.refLocal
                    }
                }
            }
        }
    }

    override fun onFailure(p0: Throwable?) {
        Log.w("MisMovimientosDialog", p0)
    }

    override fun isTomandoBack(): Boolean {
        return true
    }

    fun setArgs(model: Model) {
        this.model = model
    }

    private fun onPayCanceled(model: Model) {
        inflatedView.icono.setImageResource(R.drawable.ic_tarjeta_blue)

        inflatedView.tvMontoLabel.text = "Pago con Tarjeta"
        inflatedView.labelRefUnq.text = "Hora"
        inflatedView.labelConcept.text = "Concepto"
        inflatedView.labelTarjeta.text = "Tarjeta"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)
        inflatedView.labelConcept.visibility = View.VISIBLE
        inflatedView.labelTarjeta.visibility = View.VISIBLE

        val date = Date(model.fecha)
        val formattedHour = SimpleDateFormat("hh:mm aa", Locale.getDefault())
                .format(date)

        inflatedView.textRefUnq.text = formattedHour
        inflatedView.textConcept.text = model.descProducto
        inflatedView.textTarjeta.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
        inflatedView.textConcept.visibility = View.VISIBLE
        inflatedView.textTarjeta.visibility = View.VISIBLE
        inflatedView.cardCancelado.visibility = View.VISIBLE
    }

    private fun onCardSaleDetected(model: Model) {
        inflatedView.tvMontoLabel.text = "Pago con Tarjeta"
        inflatedView.labelRefUnq.text = "Hora"
        inflatedView.labelConcept.text = "Concepto"
        inflatedView.labelTarjeta.text = "Tarjeta"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)
        inflatedView.labelConcept.visibility = View.VISIBLE
        inflatedView.labelTarjeta.visibility = View.VISIBLE

        val date = Date(model.fecha)
        val formattedHour = SimpleDateFormat("hh:mm aa", Locale.getDefault())
                .format(date)

        inflatedView.textRefUnq.text = formattedHour
        inflatedView.textConcept.text = model.descProducto
        inflatedView.textTarjeta.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
        inflatedView.textConcept.visibility = View.VISIBLE
        inflatedView.textTarjeta.visibility = View.VISIBLE
    }

    private fun onTaeDetected(model: Model) {
        inflatedView.tvMontoLabel.text = "Monto Pagado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)

        inflatedView.textRefUnq.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
    }

    private fun onPdsDetected(model: Model) {
        inflatedView.tvMontoLabel.text = "Monto Pagado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)
        inflatedView.labelConcept.text = "Concepto"
        inflatedView.labelConcept.visibility = View.VISIBLE

        inflatedView.textRefUnq.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota
        inflatedView.textFolio.text = model.refLocal
        inflatedView.textConcept.text = "Pago de Servicios"
        inflatedView.textConcept.visibility = View.VISIBLE
    }

    private fun onQrPayDetected(model: Model) {
        inflatedView.icono.setImageResource(R.drawable.ic_icono_qr)

        inflatedView.tvMontoLabel.text = "Monto Pagado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)

        inflatedView.textRefUnq.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
    }

    private fun onMoneyOutDetected(model: Model) {
        inflatedView.icono.setImageResource(R.drawable.ic_icono_banco)

        inflatedView.tvMontoLabel.text = "Monto Retirado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelTarjeta.text = "Cuenta Bancaria"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)
        inflatedView.labelTarjeta.visibility = View.VISIBLE

        inflatedView.textRefUnq.text = "Salida de Dinero"
        inflatedView.textTarjeta.text = model.refCliente
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
        inflatedView.textTarjeta.visibility = View.VISIBLE
    }

    private fun onCardFillDetected(model: Model) {
        inflatedView.icono.setImageResource(R.drawable.ic_tarjeta_blue)

        inflatedView.tvMontoLabel.text = "Monto Rellenado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)

        inflatedView.textRefUnq.text = "Depósito a Cuenta"
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
    }

    private fun onKitDetected(model: Model) {
        inflatedView.icono.setImageResource(R.drawable.chip_pin_v)

        inflatedView.tvMontoLabel.text = "Monto Pagado"
        inflatedView.labelRefUnq.text = "Referencia Única"
        inflatedView.labelAuthEmisor.text = "Autorización Emisor"
        inflatedView.labelFolio.text = resources.getString(R.string.folio_ya_ganaste)

        inflatedView.textRefUnq.text = "Compra Lector MPos"
        inflatedView.textAuthEmisor.text = model.refRemota ?: "No Disponible"
        inflatedView.textFolio.text = model.refLocal
    }

    private fun getFormattedDate(longDate: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = longDate
        return "${calendar.get(Calendar.DAY_OF_MONTH)} ${UtilsDate.getMonthText(calendar.get(Calendar.MONTH))} ${calendar.get(Calendar.YEAR)}"
    }

    private val ticketSentListener = object : SendTicketEmailDialog.TicketSendInteractionListener {
        override fun onEmailSentSuccessfully() {
            val homeActivity = activity as? HomeActivity
            homeActivity?.onBackPressed()
        }

        override fun onEmailSendCancel() {
            val homeActivity = activity as? HomeActivity
            homeActivity?.onBackPressed()
        }
    }
}