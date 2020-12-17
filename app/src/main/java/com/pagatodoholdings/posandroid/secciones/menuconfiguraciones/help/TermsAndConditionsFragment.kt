package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TermsAndConditionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TermsAndConditionsFragment(var file : File) : AbstractConfigMenu() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_back_legales.setOnClickListener {
            loadSettings(this)
        }

        pdfViewPager.fromFile(file).load()
    }

    fun loadSettings(fragment: Fragment?) {
        getActivity()!!.supportFragmentManager.beginTransaction().remove(fragment!!).commit()
        if (parentFragment != null) {
            (parentFragment as DialogFragment?)!!.dismiss()
        } else {
            (activity as HomeActivity).cargarFragmentMiCuenta()
        }
    }

    override fun isTomandoBack(): Boolean {
        loadSettings(this)

        return true
    }
}
