package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment.CommonDialogFragmentCallBackWithCancel
import com.pagatodoholdings.posandroid.databinding.FragmentPayServicesBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.ListServicesActivity
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.PayServicesAdapter
import com.pagatodoholdings.posandroid.secciones.calendar.helpers.GridSpacingItemDecoration
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices
import com.pagatodoholdings.posandroid.utils.Logger
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class PayServicesFragment : AbstractFragment(), PayServicesAdapter.OnComercioClickListener, ReminderDialog.OnMessageToSnackBarListener{

    lateinit var binding: FragmentPayServicesBinding
    private var payServicesAdapter: PayServicesAdapter? = null
    private var reminderDialogFragment: ReminderDialog= ReminderDialog()
    private var listProducts: MutableList<Productos>? = null
    private var productosSearch: ArrayList<Productos>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPayServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        payServicesAdapter = PayServicesAdapter(context!!, this)

        binding.rvServices!!.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.rvServices!!.adapter = payServicesAdapter
        binding.rvServices.addItemDecoration(GridSpacingItemDecoration(3, 20, false))

        getPrincipalProducts()
        searchComponent()

        binding.cvOtherPays!!.setOnClickListener {

            var newReminder = FavoritoBean()
            newReminder.procod = null

            if(!(activity as ListServicesActivity).existFavorite) {
                showDialogBotonAceptar(R.layout.dialog_register_favourite, resources.getString(R.string.comenzar),
                        object : CommonDialogFragmentCallBackWithCancel {
                            override fun onCancel() {
                                //No implementation
                            }

                            override fun onAccept() {
                                (activity as ListServicesActivity).showAddFavouriteDialog(reminderDialogFragment, newReminder)
                            }
                        })
            }else{
                (activity as ListServicesActivity).showAddFavouriteDialog(reminderDialogFragment, newReminder)
            }
        }
    }

    private fun searchComponent(){


        if (binding.searchView != null) {

            binding.searchView.setIconifiedByDefault(false)
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                   return findProducts(query)
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(newText.length == 0){
                        getPrincipalProducts()
                    }
                    return false
                }
            })
        }
    }

    private fun findProducts(query: String): Boolean{
        productosSearch = null
        productosSearch = ArrayList<Productos>()
        for (producto in listProducts!!) {
            if (producto.descripcion.toLowerCase().contains(query.toLowerCase())) {
                productosSearch!!.add(producto)
            }
        }
        if (productosSearch!!.size!! > 0) {
            fillSearchRecycler(productosSearch!!)
        } else if (productosSearch!!.size == 0) {
            Toast.makeText(activity.applicationContext, R.string.No_se_encuentra, Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun fillSearchRecycler(productosSearch: ArrayList<Productos>) {
        if(!productosSearch.isNullOrEmpty()){
            payServicesAdapter!!.replaceData(productosSearch)
        }
    }


    fun showDialogBotonAceptar(layout: Int, buttonText: String?,
                               callback: CommonDialogFragmentCallBackWithCancel) { //NOSONAR
        val alert = AlertDialog.Builder(ContextThemeWrapper((activity as ListServicesActivity), R.style.AppTheme))
        val layoutInflater = LayoutInflater.from((activity as ListServicesActivity))
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(layout, null)
        alert.setCancelable(true)
        alert.setView(view)
        val alertDialog = alert.create()
        Objects.requireNonNull(alertDialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnComenzar: Button = view.findViewById(R.id.btnComenzar)
        btnComenzar.text = buttonText
        btnComenzar.textSize = 14f
        btnComenzar.setOnClickListener { view1: View? ->
            callback.onAccept()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }



        private fun getProductos() {
            listProducts = SigmaBdManager.getProductos(BasicOnFailureListener("Error ListProducts", "Error al obtener Lista de Productos"))
            if(!listProducts.isNullOrEmpty()) {
                payServicesAdapter!!.replaceData(listProducts!!)
            }
        }

        private fun addRemainingProducts(){
            val listRemaininProducts = SigmaBdManager.getProductos(BasicOnFailureListener("Error ListProducts", "Error al obtener Lista de Productos"))
            if(!listRemaininProducts.isNullOrEmpty()){
                for(product in listRemaininProducts){
                    if(!listProducts!!.contains(product)){
                        listProducts!!.add(product)
                    }//end if
                } //end for
                payServicesAdapter!!.replaceData(listProducts!!)
            }
        }

        private fun getPrincipalProducts(){
            val service = FavouritesServices()
            service.consultaFavoritosPrincipales(object : RetrofitListener<ResponseBody> {
                override fun showMessage(s: String) {/*No implementation*/}
                override fun onFailure(throwable: Throwable) {
                    getProductos()
                    Logger.LOGGER.throwing("PRODUCTOS", 1, throwable, "Error al consultar los Productos Principales")
                }
                override fun onSuccess(response: ResponseBody) {
                    getProductsPrincipals(response)
                    addRemainingProducts()
                }
            },ApiData.APIDATA.datosSesion.idSesion,
                    MposApplication.getInstance().datosLogin.datosTpv.tpvcod)
        }

        fun getProductsPrincipals(response: ResponseBody){
            var array = getProductsCode(response)
            listProducts = ArrayList<Productos>()
            if(!array.isNullOrEmpty()){
                for(code in array){
                    var product = SigmaBdManager.getProducto(code, BasicOnFailureListener("Error ListProducts","Error al obtener Codigo Productos"))
                    if(product!=null){
                        listProducts!!.add(product)
                    }
                }
            }
        }


        fun  getProductsCode(response: ResponseBody): List<String>{
            var tutorials: ArrayList<String> = ArrayList()
            if(response!=null) {
                var jsonObject: JSONObject = JSONObject(response.string());
                var s = jsonObject.get("productos").toString()

                val gson = Gson()
                val arrayTutorialType = object : TypeToken<ArrayList<String>>() {}.type
                tutorials = gson.fromJson(s, arrayTutorialType)
            }

            return tutorials
        }

        override fun onFailure(p0: Throwable?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isTomandoBack(): Boolean {
           (activity as ListServicesActivity).loadHome()
            return true
        }

        override fun onMessageToSnackBarListener(isSuccess: Boolean, message: String) {
            if (isSuccess) {
                (activity as ListServicesActivity).existFavorite = true
                activity.showSuccessSnackBar(binding.root, message)
            } else {
                activity.showErrorSnackBar(binding.root, message)
            }
        }

        override fun onComercioClickListener(producto: Productos) {
            var newReminder = FavoritoBean()
            newReminder.procod = producto.codigo

            if(!(activity as ListServicesActivity).existFavorite) {
                showDialogBotonAceptar(R.layout.dialog_register_favourite, resources.getString(R.string.comenzar),
                        object : CommonDialogFragmentCallBackWithCancel {
                            override fun onCancel() {
                                //No implementation
                            }

                            override fun onAccept() {
                                (activity as ListServicesActivity).showAddFavouriteDialog(reminderDialogFragment, newReminder)
                            }
                        })
            }else{
                (activity as ListServicesActivity).showAddFavouriteDialog(reminderDialogFragment, newReminder)
            }

        }
}
