package com.pagatodoholdings.posandroid.secciones.sales.binding

import java.math.BigDecimal

data class BreakdownData(
        var amount: BigDecimal = BigDecimal.ZERO,
        var impuesto: BigDecimal = BigDecimal.ZERO,
        var propina: BigDecimal = BigDecimal.ZERO)