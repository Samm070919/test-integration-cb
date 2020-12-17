package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.reportetrx.ReporteVentasInteractor.TipoReporte
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.adapters.ViewPagerFragmentAdapter
import com.pagatodoholdings.posandroid.utils.capitalizeWords
import kotlinx.android.synthetic.main.fragment_config_menu_reporte_ventas.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.getInstance

class ConfigMenuReporteVentasFragment : AbstractFragment() {
    private val PAGE_SELECTED = "page-selected"
    private var pageSelected = 2

    private var tipoReporte: TipoReporte? = null
    private lateinit var selectionArgs: Array<String>

    override fun isTomandoBack(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_config_menu_reporte_ventas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (tipoReporte!! == TipoReporte.REPORTE_PCI) {
            image_tiporeporte.setImageResource(R.drawable.ic_m_pos)
            title_toolbar?.text = "Mis Ventas"
        } else {
            image_tiporeporte.setImageResource(R.drawable.ic_bolsa_azul)
            title_toolbar?.text = "Mis Movimientos"
        }

        pager?.adapter = ViewPagerFragmentAdapter(getActivity()!!, tipoReporte!!)

        val bigDecimal = arguments?.getSerializable(ARG_BALANCE) ?: BigDecimal.ZERO
        val numberFormatter = SigmaBdManager.getNumberFormat(OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener el formatedor de saldo"))
        tv_saldo_movimientos.setNewTextSizeIndividualMonto(16f, 32f, 22f)
        tv_saldo_movimientos.setMonto(numberFormatter.format(bigDecimal))

        val fragmentreporteVentas: ConfigMenuReporteVentasFragment = this
        ivClose.setOnClickListener {
            closeFragment(fragmentreporteVentas)
        }
        InitCalendar()// Iniciar
        pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pageSelected = position
            }
        })
        pageSelected = savedInstanceState?.getInt(PAGE_SELECTED) ?: 2
        pager?.setCurrentItem(pageSelected, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PAGE_SELECTED, pageSelected)
    }

    private fun InitCalendar() {
        var firstmont = ""
        var secondMont = ""
        var thirdMont = ""

        val input = Date()
        val spanishLocale = Locale("es", "ES")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date: LocalDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val dateTimePattern = DateTimeFormatter.ofPattern("MMMM", spanishLocale)
            firstmont = date.format(dateTimePattern)
            secondMont = date.minusMonths(1).format(dateTimePattern)
            thirdMont = date.minusMonths(2).format(dateTimePattern)
        } else {
            val cal = getInstance(spanishLocale)
            val dateTimePattern = SimpleDateFormat("MMMM", spanishLocale)

            firstmont = dateTimePattern.format(cal.time)
            cal.add(Calendar.MONTH, -1)
            secondMont = dateTimePattern.format(cal.time)
            cal.add(Calendar.MONTH, -1)
            thirdMont = dateTimePattern.format(cal.time)
        }

        TabLayoutMediator(tab_layout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = capitalizeWords(thirdMont)
                }
                1 -> {
                    tab.text = capitalizeWords(secondMont)
                }
                2 -> {
                    tab.text = capitalizeWords(firstmont)
                }
            }
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initUI(inflater, container)
        if (arguments != null) {
            tipoReporte = arguments!!.getSerializable(TIPO_REPORTE_KEY) as TipoReporte
            selectionArgs = arguments!!.getSerializable(SELECTION_ARGUMENTS_KEY) as Array<String>

        }
    }

    override fun onFailure(throwable: Throwable) {}

    companion object {
        private const val TIPO_REPORTE_KEY = "TIPO_REPORTE_KEY"
        private const val SELECTION_ARGUMENTS_KEY = "SELECTION_ARGUMENTS_KEY"
        private const val ARG_BALANCE = "arg-balance"

//        fun getInstance(tipoReporte: TipoReporte?): ConfigMenuReporteVentasFragment {
//            return getInstance(tipoReporte, *(null as Array<String?>?)!!)
//        }

        @JvmStatic
        fun getInstance(balance: BigDecimal = BigDecimal.ZERO, tipoReporte: TipoReporte?, vararg selectionArgs: String?): ConfigMenuReporteVentasFragment {
            val args = Bundle()
            args.putSerializable(ARG_BALANCE, balance)
            args.putSerializable(TIPO_REPORTE_KEY, tipoReporte)
            args.putSerializable(SELECTION_ARGUMENTS_KEY, selectionArgs)

            val configMenuReporteVentasFragment = ConfigMenuReporteVentasFragment()
            configMenuReporteVentasFragment.arguments = args
            return configMenuReporteVentasFragment
        }
    }

    private fun closeFragment(fragment: Fragment) {
        getActivity()!!.supportFragmentManager.beginTransaction().remove(fragment).commit()
        if (parentFragment != null) {
            (parentFragment as DialogFragment?)!!.dismiss()
        } else {
            (getActivity() as HomeActivity).regresaMenu()
        }
    }
}