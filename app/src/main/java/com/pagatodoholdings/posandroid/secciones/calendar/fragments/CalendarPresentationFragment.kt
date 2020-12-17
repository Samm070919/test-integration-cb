package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pagatodoholdings.posandroid.databinding.FragmentCalendarPresentationBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.fragments.MyFavouritesDefaultFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity


/**
 * A simple [Fragment] subclass.
 */
class CalendarPresentationFragment : AbstractFragment() {

    lateinit var binding: FragmentCalendarPresentationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCalendarPresentationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.ivClose.setOnClickListener({
            (activity as HomeActivity).closeFragment(this)
        })

        binding.btnSiguiente.setOnClickListener({
            loadFavouriteDefaultFragment()
        })

    }

    private fun loadFavouriteDefaultFragment() {
        val myfavouritesDefault = MyFavouritesDefaultFragment()
        (activity as HomeActivity).cargarFragmentsCuenta(View.GONE,
                (activity as HomeActivity).generaListener(myfavouritesDefault))
    }

    override fun onFailure(p0: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isTomandoBack(): Boolean {
        (activity as HomeActivity).regresaMenu()
        return true
    }


}
