package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.pagatodoholdings.posandroid.databinding.FragmentReminderBottomSheetDialogBinding
import com.pagatodoholdings.posandroid.secciones.calendar.models.PanelItem
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean

/**
 * A simple [Fragment] subclass.
 */
class ReminderBottomSheetDialog(private val bottomSheetListener: BottomSheetListener, private val reminder: PanelItem) : BottomSheetDialogFragment() {

  lateinit var binding: FragmentReminderBottomSheetDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReminderBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    fun initViews() {
        binding.btnDelete.setOnClickListener {
            bottomSheetListener.onDelete(reminder)
            dismiss()
        }
    }

    interface BottomSheetListener {
        fun onDelete(reminder: PanelItem?)
    }

}
