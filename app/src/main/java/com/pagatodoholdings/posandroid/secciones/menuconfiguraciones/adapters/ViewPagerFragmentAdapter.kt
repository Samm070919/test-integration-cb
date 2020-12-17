package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pagatodo.sigmalib.reportetrx.ReporteVentasInteractor
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.BlankFragment

class ViewPagerFragmentAdapter(fa: FragmentActivity, tipoReporte: ReporteVentasInteractor.TipoReporte) : FragmentStateAdapter(fa) {

    private val ARG_OBJECT = "POSITION_MONT"
    private val ARG_OBJECT2 = "TIPO_REPORTE"
    private var Reporte = tipoReporte


    /**
     * they are 3 monts at the item account
     */
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = BlankFragment()
        fragment.arguments = Bundle().apply {
            //
            putInt(ARG_OBJECT, position)
            if (Reporte == ReporteVentasInteractor.TipoReporte.REPORTE_VENTAS) {
                putInt(ARG_OBJECT2, 1) // 1Normal
            } else {
                putInt(ARG_OBJECT2, 2) //2PCI
            }
        }

        return fragment
    }
}