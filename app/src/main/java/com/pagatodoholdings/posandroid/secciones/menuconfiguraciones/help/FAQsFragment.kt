package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.help

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu
import kotlinx.android.synthetic.main.fragment_f_a_qs.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FAQsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FAQsFragment : AbstractConfigMenu() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var flag : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        flag = false;
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_f_a_qs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAnswers()

        btn_kit.setOnClickListener {
            displayAnswer(tv_kit_answer, btn_kit)
        }

        btn_delivery_question.setOnClickListener {
            displayAnswer(tv_time_delivery_answer, btn_delivery_question)
        }

        btn_registro_question.setOnClickListener {
            displayAnswer(tv_registro_answer, btn_registro_question)
        }

        btn_micro_datafono_question.setOnClickListener {
            displayAnswer(tv_micro_datafono_answer, btn_micro_datafono_question)
        }

        btn_devolucion_question.setOnClickListener {
            displayAnswer(tv_devolucion_answer, btn_devolucion_question)
        }

        btn_charge_question.setOnClickListener {
            displayAnswer(tv_charge_answer, btn_charge_question)
        }

        btn_service_pay_question.setOnClickListener {
            displayAnswer(tv_payment_answer, btn_service_pay_question)
        }

        btn_recharge_question.setOnClickListener {
            displayAnswer(tv_recharge_answer, btn_recharge_question)
        }

        btn_return.setOnClickListener {
            loadSettings(this)
        }
    }

    fun loadSettings(fragment: Fragment?) {
        getActivity()!!.supportFragmentManager.beginTransaction().remove(fragment!!).commit()
        if (parentFragment != null) {
            (parentFragment as DialogFragment?)!!.dismiss()
        } else {
            (activity as HomeActivity).cargarFragmentMiCuenta()
        }
    }

    private fun displayAnswer(tv : TextView, btn : Button){
        if(tv.visibility.equals(View.VISIBLE)){
            tv.visibility = View.GONE
            btn.setTextColor(resources.getColor(R.color.coloproducttext))
        }else if(tv.visibility.equals(View.GONE)){
            //tv.text = addColorToNumbers(tv)
            btn.setTextColor(resources.getColor(R.color.blue_ya_ganaste))
            tv.visibility = View.VISIBLE
        }
    }

    private fun addColorToNumbers(answer : String) : SpannableStringBuilder{
        var arrStr = arrayOf(answer.split("\n"))
        var i = 0
        var resultStr = SpannableStringBuilder("")


        while (i < arrStr.get(0).size){
            var row = SpannableStringBuilder(arrStr.get(0).get(i))
            row.setSpan(ForegroundColorSpan(
                    resources.getColor(R.color.blue_ya_ganaste)),
                    0,
                    row.indexOf(" "),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )

            resultStr = resultStr.append(row).append("\n\n")
            i++
        }

        return resultStr
    }

    override fun isTomandoBack(): Boolean {
        loadSettings(this)

        return true
    }

    private fun setAnswers(){
        tv_kit_answer.text = addColorToNumbers(getString(R.string.que_incluye_kit_answer))
        tv_time_delivery_answer.text = addColorToNumbers(getString(R.string.compra_desde_bogota))
        tv_registro_answer.text = addColorToNumbers(getString(R.string.registro_answer))
        tv_micro_datafono_answer.text = addColorToNumbers(getString(R.string.datafono_answer))
        tv_charge_answer.text = addColorToNumbers(getString(R.string.charge_answer))
        tv_payment_answer.text = addColorToNumbers(getString(R.string.pay_service_answer))
        tv_recharge_answer.text = addColorToNumbers(getString(R.string.recharge_answer))
        tv_devolucion_answer.text = addColorToNumbers(getString(R.string.devolucion_answer))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FAQs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FAQsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
