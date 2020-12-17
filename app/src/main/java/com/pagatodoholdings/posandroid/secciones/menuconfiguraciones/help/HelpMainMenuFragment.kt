package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.pagatodo.sigmalib.NivelMenu
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu
import com.pagatodoholdings.posandroid.secciones.productos.MenuProductosListener
import com.pagatodoholdings.posandroid.utils.Logger
import kotlinx.android.synthetic.main.fragment_help_main_menu.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HelpMainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HelpMainMenuFragment : AbstractConfigMenu() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var menuProductosListener: MenuProductosListener? = null
    private var nivelActual: NivelMenu? = null

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
        return inflater.inflate(R.layout.fragment_help_main_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_back.setOnClickListener {
            loadSettings(this)
        }

        btn_contact_phone.setOnClickListener {
            loadContactFragment()
        }

        btn_faqs.setOnClickListener {
            loadFaqs()
        }

        btn_legales.setOnClickListener {
            showTermsAndCoditions()
        }

        btn_valora_app.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.pagatodo.yawallet")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        btn_whatsapp.setOnClickListener {
            goToWhatsapp()
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

    private fun loadFaqs(){

        //final ConfigMenuAyudaFragment fragmentAyuda = new ConfigMenuAyudaFragment();
        //fragmentAyuda.setListener(getListener());
        //homeActivity.cargarFragmentsCuenta(View.GONE, homeActivity.generaListener(fragmentAyuda));
        val fragment = FAQsFragment()
        fragment.listener = listener
        (activity as HomeActivity).cargarFragments(
                View.GONE,
                (activity as HomeActivity).generaListener(fragment)
        )
    }

    private fun loadContactFragment(){
        val fragment = ContactFragment()
        fragment.listener = listener
        (activity as HomeActivity).cargarFragments(
                View.GONE,
                (activity as HomeActivity).generaListener(fragment)
        )
    }

    private fun showTermsAndCoditions() {
        /*var intent = Intent(activity, TermsAndConditions::class.java)
        activity.startActivity(intent)*/
        val file = File(context!!.cacheDir, "terminos_condicionesv1.pdf")
        if (!file.exists()) {
            var output: FileOutputStream? = null
            try {
                getActivity()!!.assets.open("terminos_condicionesv1.pdf").use { asset ->
                    output = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var size: Int
                    while (asset.read(buffer).also { size = it } != -1) {
                        output!!.write(buffer, 0, size)
                    }
                }
            } catch (exe: IOException) {
                Logger.LOGGER.throwing(AbstractFragment.TAG, 1, exe, exe.message)
            } finally {
                try {
                    if (output != null) output!!.close()
                } catch (e: IOException) {
                    Logger.LOGGER.throwing(AbstractFragment.TAG, 1, e, e.message)
                }
            }
        }

        loadTermsAndConditions(file)

        /*PDFView.with(activity)
                .setfilepath(file.absolutePath)
                .setSwipeOrientation(0)
                .start()*/
    }

    private fun loadTermsAndConditions(file : File){
        val fragment = TermsAndConditionsFragment(file)
        fragment.listener = listener
        (activity as HomeActivity).cargarFragments(
                View.GONE,
                (activity as HomeActivity).generaListener(fragment)
        )
    }

    override fun isTomandoBack(): Boolean {
        /*var intent = Intent(activity, HomeActivity::class.java)
        activity.startActivity(intent)

        return true*/

        loadSettings(this)

        return true
    }

    private fun goToWhatsapp(){
        val fragment = HelpWAFragment()
        fragment.listener = listener
        (activity as HomeActivity).cargarFragments(
                View.GONE,
                (activity as HomeActivity).generaListener(fragment)
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HelpMainMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HelpMainMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
