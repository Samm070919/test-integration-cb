package com.pagatodoholdings.posandroid.secciones.registro

import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.PaisConfig

interface ConfigPaisListener {
    fun onFetchUrlForCountry(country: Country)
    fun onReturnConfig(): PaisConfig?
    fun onReturnCountry(): Country?
}