package com.pagatodoholdings.posandroid.secciones.qrcode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pagatodoholdings.posandroid.databinding.FragmentDefaultQrBinding

import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class DefaultQrFragment : AbstractFragment() {


    lateinit var binding: FragmentDefaultQrBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDefaultQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivClose.setOnClickListener({
            (activity as HomeActivity).closeFragment(this)
        })
    }

    override fun onFailure(p0: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun isTomandoBack(): Boolean {
        (activity as HomeActivity).regresaMenu()
        return true
    }


}
