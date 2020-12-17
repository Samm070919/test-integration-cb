package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.adapters

import android.util.Log
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodoholdings.posandroid.general.models.CurrencyImport
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import java.math.BigDecimal
import java.util.*

class SaleItemFormatHelper : OnFailureListener {
    private val TAG = SaleItemFormatHelper::class.java.simpleName
    private val proCods = HashMap<String, Productos>()
    private val calendarTime = Calendar.getInstance()

    private val dbnumberformat = SigmaBdManager.getInputNumberFormat(this)
    private val posicionMoneda = SigmaBdManager.getParametroFijo("0022", this)
    private val parametroMoneda = SigmaBdManager.getParametroFijo("0021", this)
    private val parametroDecimales = SigmaBdManager.getParametroFijo("0024", this)

    fun getCurrencyFormatObject(bigDecimal: BigDecimal): CurrencyImport {
        val sFormatedImport: String = dbnumberformat.format(bigDecimal)
        var sCents = if (sFormatedImport.indexOf(parametroDecimales) != -1) sFormatedImport.substring(sFormatedImport.indexOf(parametroDecimales)) else "00"

        if (dbnumberformat.maximumFractionDigits == 0) {
            sCents = ""
        }

        val sInteger = if (sFormatedImport.indexOf(parametroDecimales) != -1) {
            sFormatedImport.replace(sCents, "")
        } else {
            sFormatedImport
        }

        return CurrencyImport(
                sInteger,
                sCents,
                parametroMoneda,
                posicionMoneda
        )
    }

    fun getProducto(prodCod: String): Productos? {
        return if (proCods.containsKey(prodCod)) {
            proCods[prodCod]
        } else {
            val producto = SigmaBdManager.getProducto(prodCod, this)
            proCods[prodCod] = producto
            producto
        }
    }

    fun getDayNumber(dateInMillis: Long): String {
        calendarTime.timeInMillis = dateInMillis
        return calendarTime.get(Calendar.DAY_OF_MONTH).toString()
    }

    override fun onFailure(p0: Throwable?) {
        Log.e(TAG, p0?.message, p0)
    }
}