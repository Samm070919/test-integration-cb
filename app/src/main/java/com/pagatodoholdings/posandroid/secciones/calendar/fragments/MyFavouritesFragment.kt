package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R

import com.pagatodoholdings.posandroid.databinding.FragmentMyFavouritesBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.ListServicesActivity
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.FavouritesPanelAdapter
import com.pagatodoholdings.posandroid.secciones.calendar.models.PanelItem
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices
import com.pagatodoholdings.posandroid.utils.Logger

/**
 * A simple [Fragment] subclass.
 */
class MyFavouritesFragment : AbstractFragment(), FavouritesPanelAdapter.OnItemPanelClickListener {

    lateinit var binding: FragmentMyFavouritesBinding
    private var favouritesPanelAdapter: FavouritesPanelAdapter? = null
    var homeActivity : HomeActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favouritesPanelAdapter = FavouritesPanelAdapter(context!!, this)
        binding.rvFavourites!!.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.rvFavourites!!.adapter = favouritesPanelAdapter

        getListPanelFavorites().let { favouritesPanelAdapter!!.replaceData(it) }
        binding.ivClose.setOnClickListener({
            loadMyRemindersFragment()
        })


    }

    private fun getListPanelFavorites() : List<PanelItem>{
        val listPanelFavorites: MutableList<PanelItem> = ArrayList()

        //Llenar lista de Favoritos
        for(favorite in homeActivity!!.listaFavoritos){
            var panelItem = PanelItem()
                if(favorite.procod!=null){
                    var producto = SigmaBdManager.getProducto(favorite.procod,OnFailureListener.BasicOnFailureListener("ERROR", "Error"))
                    panelItem.alias = favorite.alias
                    if(producto!= null) {
                        panelItem.imageInt = (producto.icono)
                    }else{
                        panelItem.imageInt = (null)
                    }
                    listPanelFavorites.add(panelItem)
                }else{
                    panelItem.alias = favorite.alias
                    panelItem.imageInt = null
                    listPanelFavorites.add(panelItem)
                }
        }

        var panelItem = PanelItem()
        panelItem.alias = "Más"
        panelItem.imageInt = R.drawable.ic_mas
        listPanelFavorites.add(panelItem)

        return listPanelFavorites
    }

    override fun onItemPanelClickListener(panelItem: PanelItem) {
        //Implementar click de favoritos

        when (panelItem.alias) {
            "Más" -> {
                var existFavorite = true;
                val intent = Intent(activity, ListServicesActivity::class.java)
                intent.putExtra("existFavorite", existFavorite)
                startActivity(intent)
            }
            else -> {
                /*
                val bottomSheet = panelItem?.let {
                    ReminderBottomSheetDialog((object : ReminderBottomSheetDialog.BottomSheetListener {
                        override fun onDelete(panelItem: PanelItem?) {
                            if (panelItem != null) {
                                //deleteReminder(panelItem)
                            }
                        }
                    }), it)
                }
                fragmentManager?.let {
                    bottomSheet?.show(it, "BottomSheetCalendar")
                }

                 */
            }
        }
    }

    private fun loadMyRemindersFragment() {
        val loadMyReminders = MyRemindersCalendarFragments()
        loadMyReminders.homeActivity = homeActivity
        (activity as HomeActivity).cargarFragmentsCuenta(View.GONE,
                (activity as HomeActivity).generaListener(loadMyReminders))
    }

    override fun onFailure(p0: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isTomandoBack(): Boolean {
        (activity as HomeActivity).regresaMenu()
        return true
    }


}
