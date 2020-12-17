package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.databinding.FragmentMyFavouritesDefaultBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.ListServicesActivity
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.FavouritesPanelAdapter
import com.pagatodoholdings.posandroid.secciones.calendar.helpers.GridSpacingItemDecoration
import com.pagatodoholdings.posandroid.secciones.calendar.models.PanelItem
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
//import kotlinx.android.synthetic.main.fragment_my_favourites_default.*

class MyFavouritesDefaultFragment : AbstractFragment(), FavouritesPanelAdapter.OnItemPanelClickListener {

    lateinit var binding: FragmentMyFavouritesDefaultBinding
    private var favouritesPanelAdapter: FavouritesPanelAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyFavouritesDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.ivClose.setOnClickListener({
            loadCalendarPresentationFragment()
        })

        favouritesPanelAdapter = FavouritesPanelAdapter(context!!, this)
        binding.rvFavoritos!!.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        binding.rvFavoritos!!.adapter = favouritesPanelAdapter
        binding.rvFavoritos!!.addItemDecoration(GridSpacingItemDecoration(4, 15, false))

        setItemsToPanel()
    }

    fun setItemsToPanel() {
        var listItems: MutableList<PanelItem> = ArrayList()
        for (i in 0..7) {
            val item = PanelItem()
            when (i) {
                0 -> {
                    item.alias = "Luz"
                    item.imageInt = R.drawable.ic_luz
                }
                1 -> {
                    item.alias = "Cable"
                    item.imageInt = R.drawable.ic_cable
                }
                2 -> {
                    item.alias = "Gas"
                    item.imageInt = R.drawable.ic_gas
                }
                3 -> {
                    item.alias = "Recargas"
                    item.imageInt = R.drawable.ic_recargas
                }
                4 -> {
                    item.alias = "Más"
                    item.imageInt = R.drawable.ic_mas
                }
                5 -> {
                    item.alias = "Más"
                    item.imageInt = R.drawable.ic_mas
                }
                6 -> {
                    item.alias = "Más"
                    item.imageInt = R.drawable.ic_mas
                }
                7 -> {
                    item.alias = "Ver más"
                    item.imageInt = R.drawable.ic_ojo_azul
                }
            }
            listItems.add(item)
        }
        favouritesPanelAdapter!!.replaceData(listItems)
    }

    private fun loadCalendarPresentationFragment() {
        val calendarPresentationFragment = CalendarPresentationFragment()
        (activity as HomeActivity).cargarFragmentsCuenta(View.GONE,
                (activity as HomeActivity).generaListener(calendarPresentationFragment))
    }

    override fun onFailure(p0: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isTomandoBack(): Boolean {
        loadCalendarPresentationFragment()
        return true
    }

    override fun onItemPanelClickListener(panelItem: PanelItem) {
        val intent = Intent(activity, ListServicesActivity::class.java)
        intent.putExtra("existFavorite",false)
        startActivity(intent)
    }


}
