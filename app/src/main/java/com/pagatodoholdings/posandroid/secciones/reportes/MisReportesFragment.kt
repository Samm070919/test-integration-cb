package com.pagatodoholdings.posandroid.secciones.reportes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_mis_reportes.*

class MisReportesFragment : AbstractFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mis_reportes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        on_back.setOnClickListener {
            loadHome()
        }
    }

    private fun loadHome() {
        (activity as HomeActivity).restauraHome()
    }


    override fun onFailure(p0: Throwable?) {
    }

    override fun isTomandoBack(): Boolean {
        return false
    }
}