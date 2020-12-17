package com.pagatodoholdings.posandroid.manager

import com.pagatodo.sigmalib.emv.DecodeData

interface AdqTransactionCallback {
    fun showSolicitarNip()
    fun qposNoConectado()
    fun qposDesconectado()
    fun procesandoTransaccion()
    fun errorDeOperacion(mensaje: String?)
    fun qposNoResponse()
    fun operacionCancelada()
    fun onSucces(reqFirma: Boolean, response: Any?, decodeData: DecodeData?)
    fun requestCuotas()
}