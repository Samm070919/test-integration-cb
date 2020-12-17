package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pagatodo.sigmalib.reportetrx.Model
import com.pagatodo.sigmalib.reportetrx.ReporteVentasInteractor
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.adapters.ReporteVentasAdapter
import com.pagatodoholdings.posandroid.secciones.movimientos.MisMovimientosDialog
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM = "POSITION_MONT"
private const val ARG_PARAM2 = "TIPO_REPORTE"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var positionDate: Int? = null
    private var reporteIndicator: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            positionDate = it.getInt(ARG_PARAM)
            reporteIndicator = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapterView = if (reporteIndicator == 1) {
            ReporteVentasAdapter(ReporteVentasInteractor.getVentas(
                    activity!!.contentResolver,
                    ReporteVentasInteractor.TipoReporte.REPORTE_VENTAS,
                    *emptyArray<String>()), consumer)
        } else {
            ReporteVentasAdapter(ReporteVentasInteractor.getVentas(
                    activity!!.contentResolver,
                    ReporteVentasInteractor.TipoReporte.REPORTE_PCI,
                    *emptyArray<String>()), consumer)
        }

        lista_movimientos.layoutManager = LinearLayoutManager(activity)

        val lowerLimitCal = Calendar.getInstance()
        val upperLimitCal = Calendar.getInstance()
        val subtract = when (positionDate) {
            0 -> -2
            1 -> -1
            else -> 0
        }

        upperLimitCal.add(Calendar.MONTH, subtract)
        upperLimitCal.set(Calendar.DAY_OF_MONTH, upperLimitCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        upperLimitCal.set(Calendar.HOUR_OF_DAY, upperLimitCal.getActualMaximum(Calendar.HOUR_OF_DAY))
        upperLimitCal.set(Calendar.MINUTE, upperLimitCal.getActualMaximum(Calendar.MINUTE))
        upperLimitCal.set(Calendar.SECOND, upperLimitCal.getActualMaximum(Calendar.SECOND))

        lowerLimitCal.add(Calendar.MONTH, subtract)
        lowerLimitCal.set(Calendar.DAY_OF_MONTH, upperLimitCal.getActualMinimum(Calendar.DAY_OF_MONTH))
        lowerLimitCal.set(Calendar.HOUR_OF_DAY, upperLimitCal.getActualMinimum(Calendar.HOUR_OF_DAY))
        lowerLimitCal.set(Calendar.MINUTE, upperLimitCal.getActualMinimum(Calendar.MINUTE))
        lowerLimitCal.set(Calendar.SECOND, upperLimitCal.getActualMinimum(Calendar.SECOND))

        adapterView.filterByDateRange(lowerLimitCal, upperLimitCal)

        lista_movimientos.adapter = adapterView
    }

    private val consumer = Consumer<Model> { model ->
        val misMovimiento = MisMovimientosDialog()
        val homeActivity = activity as? HomeActivity

        homeActivity?.let {
            misMovimiento.setArgs(model)
            it.cargarFragmentDetalleMis(misMovimiento)
        }
    }
}
