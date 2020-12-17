package com.pagatodoholdings.posandroid.secciones.sales.calculate

import java.math.BigDecimal

interface Additions {
    fun getMontotal(perPropopina: String?): BigDecimal?
    fun getSubTotal(amount: String?): BigDecimal?
    fun getImpuestos(amount: String?): String?
    fun getPropina(perPro: String?): String?
}