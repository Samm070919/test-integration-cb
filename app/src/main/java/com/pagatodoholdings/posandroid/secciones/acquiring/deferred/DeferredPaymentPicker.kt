package com.pagatodoholdings.posandroid.secciones.acquiring.deferred

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pagatodo.sigmalib.EmvManager
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodoholdings.posandroid.R
import kotlinx.android.synthetic.main.fragment_deferred_payment.view.*

class DeferredPaymentPicker : DialogFragment() {
    private lateinit var inflatedView: View
    var deferredInteractionListener: DeferredInteractionListener? = null

    companion object {
        private val TAG = DeferredPaymentPicker::class.java.simpleName
        private const val ARG_EMV_PERFIL_ID = "arg-emv-perfil-id"

        fun newInstance(emvPerfilId: Int): DeferredPaymentPicker {
            val args = Bundle()
            args.putInt(ARG_EMV_PERFIL_ID, emvPerfilId)

            val fragment = DeferredPaymentPicker()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            setStyle(STYLE_NO_TITLE, R.style.DialogStyle)

            inflatedView = inflater.inflate(R.layout.fragment_deferred_payment, null)

            val emvPerfilId = arguments?.getInt(ARG_EMV_PERFIL_ID) ?: -1
            val perfilEmvApp = EmvManager.getFullPerfil(emvPerfilId, BasicOnFailureListener(TAG, "Error al obtener el Perfil EMV"))
            val emvRangoBinCuotasList = EmvManager.getRangoCuotas(perfilEmvApp.perfilesEmv.lstCuotasMes)

            if (emvRangoBinCuotasList.isNotEmpty()) {
                val emvRangoBinCuotas = emvRangoBinCuotasList.first()
//                val feeList = (emvRangoBinCuotas.cuotasmin
//                        ..emvRangoBinCuotas.cuotasmax)
//                        .step(emvRangoBinCuotas.cuotasinc).toList()
                val feeList: MutableList<String> = arrayListOf()
                for (x in emvRangoBinCuotas.cuotasmin
                        ..emvRangoBinCuotas.cuotasmax
                        step emvRangoBinCuotas.cuotasinc) {
                    feeList.add(x.toString())
                }
                inflatedView.pickerQuotas.maxValue = feeList.size
                inflatedView.pickerQuotas.minValue = emvRangoBinCuotas.cuotasmin
                inflatedView.pickerQuotas.displayedValues = feeList.toTypedArray()

                inflatedView.btnConfirmacion.setOnClickListener {
                    val value = inflatedView.pickerQuotas.value - 1
                    val feeChosen = feeList[value].toInt()
                    deferredInteractionListener?.onValueChosen(feeChosen)
                    dismiss()
                }

                builder.setView(inflatedView)
            } else {
                Handler(activity?.mainLooper).postDelayed({
                    deferredInteractionListener?.onValueChosen(1)
                    dismiss()
                }, 150)
            }

            builder.create()
        } ?: throw IllegalStateException("DeferredPaymentPicker: Activity cannot be null")
    }

    interface DeferredInteractionListener {
        fun onValueChosen(int: Int)
    }
}