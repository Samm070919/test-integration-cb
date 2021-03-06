package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ya_ganaste_cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu
import kotlinx.android.synthetic.main.fragment_ya_ganaste_cards.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YaGanasteCardsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YaGanasteCardsFragment : AbstractConfigMenu() {
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
        return inflater.inflate(R.layout.fragment_ya_ganaste_cards, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_back.setOnClickListener {
            loadSettings(this)
        }
    }

    private fun loadSettings(fragment : Fragment){
        activity!!.supportFragmentManager.beginTransaction().remove(fragment).commit()
        if (parentFragment != null) {
            (parentFragment as DialogFragment?)!!.dismiss()
        } else {
            (activity as HomeActivity?)!!.cargarFragmentMiCuenta()
        }
    }

    override fun isTomandoBack(): Boolean {
        return super.isTomandoBack()

        loadSettings(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YaGanasteCardsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                YaGanasteCardsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
