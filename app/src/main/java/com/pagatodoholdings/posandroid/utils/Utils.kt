package com.pagatodoholdings.posandroid.utils

import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodo.sigmalib.util.SigmaUtil
import org.apache.commons.lang3.text.WordUtils
import java.text.NumberFormat

fun capitalizeWords(string: String) : String{
    val delimiters = charArrayOf(' ', '_')
    return WordUtils.capitalizeFully(string, *delimiters)
}

fun setAmountFormat(amount : String) : String{
    val importe = SigmaUtil.cleanImporteInput(amount, getNumberFormat())
    val formatted: String = getNumberFormat().format(importe)

    return formatted
}

fun getNumberFormat() : NumberFormat {
    return SigmaBdManager.getInputNumberFormat(BasicOnFailureListener("Utils", "Error al obtener numberFormat"))
}

fun capitalizeWordsLowerCase(string : String) : String{
    return WordUtils.capitalizeFully(string)
}

fun cleanAmount(field: String): String {
    val amount = SigmaUtil.cleanImporteInput(
            field,
            getNumberFormat()
    )

    return amount.toString()
}

fun isNullOrBlank(string: String?) = string.isNullOrBlank()