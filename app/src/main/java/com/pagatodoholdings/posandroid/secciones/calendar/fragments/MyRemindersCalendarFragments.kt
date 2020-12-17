package com.pagatodoholdings.posandroid.secciones.calendar.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.RetrofitListener
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment.CommonDialogFragmentCallBack
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment.CommonDialogFragmentCallBackWithCancel
import com.pagatodoholdings.posandroid.databinding.FragmentMyRemindersCalendarFragmentsBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.calendar.ListServicesActivity
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.FavouritesPanelAdapter
import com.pagatodoholdings.posandroid.secciones.calendar.adapters.ListEventsAdapter
import com.pagatodoholdings.posandroid.secciones.calendar.helpers.SwipeHelper
import com.pagatodoholdings.posandroid.secciones.calendar.models.PanelItem
import com.pagatodoholdings.posandroid.secciones.formularios.FormularioFragmentFactory
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.secciones.retrofit.FavouritesServices
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.ManejadorFragments
import kotlinx.android.synthetic.main.fragment_my_reminders_calendar_fragments.*
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MyRemindersCalendarFragments : AbstractFragment(), ListEventsAdapter.OnEventClickListener, FavouritesPanelAdapter.OnItemPanelClickListener {

    lateinit var binding : FragmentMyRemindersCalendarFragmentsBinding
    private var shouldShow = false
    private val locale = Locale("es", "MX")
    private var currentDate: Date = GregorianCalendar(locale).time
    private var calendarDateScroll: Date = GregorianCalendar(locale).time
    private val numberDayText = DateFormat.format("dd", currentDate) as String
    private val numberMonth = DateFormat.format("MM", currentDate) as String
    private val monthText = DateFormat.format("MMMM", currentDate) as String
    private val sdfCalendarTitle = DateFormat.format("MMMM yyyy", currentDate) as String
    private var eventsAdapter: ListEventsAdapter? = null
    private var favouritesPanelAdapter: FavouritesPanelAdapter? = null
    private var listDatesFavourites: MutableList<FavoritoBean> = ArrayList()
    private var listFavouritesPerMonth: MutableList<FavoritoBean>?=null
    private var fechaInicio: String =DateFormat.format("yyyy",currentDate) as String +"-"+"01"+"-"+"01"
    private var fechaFin: String =DateFormat.format("yyyy",currentDate) as String +"-"+"12"+"-"+"31"
    private var mLastClickTime: Long = 0

    //Quien instancia este Fragment debe hacer un SET del HOME ACTIVITY para poder desplegar los Dialogs en el
    var homeActivity : HomeActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyRemindersCalendarFragmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFavouritesDates()
        initViews()
    }

    fun initViews(){

        binding.root.setOnTouchListener { view, motionEvent ->
            reiniciarContador()
            false
        }

        binding.ivClose.setOnClickListener({
            homeActivity?.closeFragment(this)
        })

        binding.imvArrowRight.setOnClickListener({
            binding.compactcalendarView.scrollRight()
        })

        binding.imvArrowLeft.setOnClickListener({
            binding.compactcalendarView.scrollLeft()
        })

        binding.tvNameMonth?.text = this.sdfCalendarTitle.capitalize()
        binding.txvDayNumber?.text = numberDayText
        binding.txvDateMonth?.text = "de ${monthText.substring(0, 1).capitalize() + monthText.substring(1, monthText.length)}"

        binding.compactcalendarView.setUseThreeLetterAbbreviation(false)
        binding.compactcalendarView.setCurrentDate(currentDate)
        binding.compactcalendarView.displayOtherMonthDays(true)
        binding.compactcalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {

            override fun onDayClick(dateClicked: Date) {/*No implementation*/}

            override fun onMonthScroll(scrollDate: Date) {
                tvNameMonth?.text = (DateFormat.format("MMMM yyyy", scrollDate) as String).capitalize()
                calendarDateScroll = scrollDate
                onMonthScrollGetEvents()
            }

        })
        binding.compactcalendarView.setAnimationListener(object : CompactCalendarView.CompactCalendarAnimationListener {
            override fun onOpened() {
                binding.viewGrayLine?.visibility = View.VISIBLE
                binding.rbCalendarFlip?.isEnabled = true
            }

            override fun onClosed() {
                binding.rvReminders?.visibility = View.VISIBLE
                binding.rbCalendarFlip?.isEnabled = true
            }
        })

        val exposeCalendarListener = calendarHideOrShowAnimate()
        rbCalendarFlip?.setOnClickListener(exposeCalendarListener)
        view?.viewTreeObserver?.addOnGlobalLayoutListener {
            val calendarHeight: Int? = binding.compactcalendarView.height
            if (calendarHeight != null && binding.compactcalendarView!=null) {
                binding.compactcalendarView.setTargetHeight(calendarHeight)
                binding.compactcalendarView.invalidate()
            }
        }

        eventsAdapter = ListEventsAdapter(context!!, this)
        favouritesPanelAdapter = FavouritesPanelAdapter(context!!, this)

        binding.rvReminders!!.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        binding.rvReminders!!.adapter = favouritesPanelAdapter
        //binding.rvReminders.addItemDecoration(GridSpacingItemDecoration(4, 15, false))

        binding.rvListEvents!!.layoutManager = LinearLayoutManager(context)
        binding.rvListEvents!!.adapter = eventsAdapter
        //binding.rvListEvents!!.addItemDecoration(ItemOffsetDecoration(context!!, "6dp"))

        setSwipeButtonsToRecyclerView()
    }

    private fun onMonthScrollGetEvents() {
        binding.compactcalendarView.removeAllEvents()

        listFavouritesPerMonth = getFavouritesPerMonth(listDatesFavourites, calendarDateScroll)
        listFavouritesPerMonth?.let { eventsAdapter!!.replaceData(it) }

        val listEvent: MutableList<Event> = getEventsFromReminders((listFavouritesPerMonth as List<FavoritoBean>)) as MutableList<Event>

        binding.compactcalendarView.addEvents(listEvent)

    }

    @NonNull
    private fun calendarHideOrShowAnimate(): View.OnClickListener? {
        return View.OnClickListener {
            var radioButton: AppCompatRadioButton = it as AppCompatRadioButton
            if (!binding.compactcalendarView.isAnimating!!) {
                binding.rbCalendarFlip.isEnabled = false
                if (shouldShow) {
                    radioButton.isChecked = true
                    radioButton.background = ContextCompat.getDrawable(context!!.applicationContext, R.drawable.calendar_btn_active)
                    binding.txvCalendar.text = "Mi Calendario de Pagos"
                    binding.linMonth.visibility = View.VISIBLE
                    binding.rvReminders.visibility = View.GONE
                    binding.compactcalendarView.showCalendarWithAnimation()

                } else {
                    binding.viewGrayLine.visibility = View.GONE
                    radioButton.isChecked = false
                    radioButton.background = ContextCompat.getDrawable(context!!.applicationContext, R.drawable.calendar_btn_desactive)
                    binding.txvCalendar.text = "Mis Favoritos"
                    binding.linMonth.visibility = View.GONE
                    binding.compactcalendarView.hideCalendarWithAnimation()
                }
                shouldShow = !shouldShow
            }
        }
    }

    fun getEventsFromReminders(reminders: List<FavoritoBean>): List<Event> {

        /*
        * Los colores para los estatus son:
        *
        *  GRIS(#c4c4c4) = Vencido
        *  AZUL(#01b6e7) = pagado
        *  AZUL OSCURO (#46606a) = PENDIENTE
        * */

        var listEvents: MutableList<Event> = ArrayList()

        for (reminder in reminders) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date : Date = formatter.parse(reminder.fecha);
            var color: String = "#46606a"

            if(reminder.estado.equals("PAGADO")){
                color = "#01b6e7"
            }else if(reminder.estado.equals("PENDIENTE")){
                color = "#46606a"
            }else{
                color = "#c4c4c4"
            }

            val event = Event(Color.parseColor(color), date.time!!)
            listEvents.add(event)

        }
        return listEvents
    }

    fun getFavouritesPerMonth(reminders: List<FavoritoBean>, currentMonth: Date): MutableList<FavoritoBean>? {

        val listReminders: MutableList<FavoritoBean> = ArrayList()
        for (reminder in reminders) {
            val reminderDate = GregorianCalendar(locale)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date : Date = formatter.parse(reminder.fecha);
            reminderDate.time = date
            val calendarMonth = GregorianCalendar(locale)
            calendarMonth.time = currentMonth
            if (calendarMonth.get(Calendar.YEAR) == reminderDate.get(Calendar.YEAR)
                    && calendarMonth.get(Calendar.MONTH) == reminderDate.get(Calendar.MONTH)) {
                listReminders.add(reminder)
                listReminders!!.sortedBy { favoritoBean -> favoritoBean.fecha }
            }
        }
        return listReminders

    }

    fun getFavouritesDates() {
        homeActivity?.muestraProgressDialog("Cargando")
        val service = FavouritesServices()
        service.getFechasFavoritos(object : RetrofitListener<List<FavoritoBean>> {
            override fun showMessage(s: String) {/*No implementation*/}
            override fun onFailure(throwable: Throwable) {
                homeActivity?.ocultaProgressDialog()
                homeActivity?.despliegaModal(true, false, getString(R.string.error),
                        throwable.message, CommonDialogFragmentCallBack {homeActivity?.regresaMenu()})
                Logger.LOGGER.throwing("OBTENER FAVORITOS", 1, throwable, "Error al consultar los Favoritos")
            }

            override fun onSuccess(favoritoBeans: List<FavoritoBean>) {
                homeActivity?.ocultaProgressDialog()
                listDatesFavourites = favoritoBeans as ArrayList
                listFavouritesPerMonth = getFavouritesPerMonth(listDatesFavourites,calendarDateScroll)
                listFavouritesPerMonth?.let { eventsAdapter!!.replaceData(it) }
                getListPanelFavorites().let { favouritesPanelAdapter!!.replaceData(it) }

                onMonthScrollGetEvents()
            }
        }, fechaInicio, fechaFin,MposApplication.getInstance().datosLogin.datosTpv.tpvcod,
                ApiData.APIDATA.datosSesion.idSesion)
    }

    private fun getListPanelFavorites() : List<PanelItem>{
        val listPanelFavorites: MutableList<PanelItem> = ArrayList()


        //Llenar lista de Favoritos
        for(favorite in homeActivity!!.listaFavoritos){
            if(listPanelFavorites.size<=6) {
                var panelItem = PanelItem()

                if(favorite.procod!=null){
                    getProduct(panelItem, favorite,listPanelFavorites)
                }else{
                    panelItem.alias = favorite.alias
                    panelItem.imageInt = null
                    listPanelFavorites.add(panelItem)
                }

            }else{
                break
            }
        }


        for(i in listPanelFavorites.size..7){
            if(listPanelFavorites.size<=6){
                var panelItem = PanelItem()
                panelItem.alias = "Más"
                panelItem.imageInt = R.drawable.ic_mas
                listPanelFavorites.add(panelItem)
            }else{
                var panelItem = PanelItem()
                panelItem.alias = "Ver más"
                panelItem.imageInt = R.drawable.ic_ojo_azul
                listPanelFavorites.add(panelItem)
            }

        }

        return listPanelFavorites
    }

    private fun getProduct(panelItem: PanelItem, favoritoBean: FavoritoBean, listPanelFavorites: MutableList<PanelItem>){
        var producto = SigmaBdManager.getProducto(favoritoBean.procod, OnFailureListener.BasicOnFailureListener("ERROR", "Error"))
        panelItem.alias = favoritoBean.alias
        if(producto!= null) {
            panelItem.imageInt = (producto.icono)
        }else{
            panelItem.imageInt = (null)
        }
        listPanelFavorites.add(panelItem)
    }

    private fun setSwipeButtonsToRecyclerView() {
        val swipeHelper: SwipeHelper = object : SwipeHelper(context) {
            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: List<UnderlayButton>?) {
                underlayButtons as MutableList<UnderlayButton>

                underlayButtons.add(UnderlayButton(getString(R.string.marked_pay_title), 0, Color.parseColor("#249256"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                var reminder = (rvListEvents.adapter as ListEventsAdapter).getItem(pos)
                                if(reminder.estado.equals("PAGADO")){
                                    Toast.makeText(context, "El recordatorio ya se encuentra PAGADO", Toast.LENGTH_SHORT).show()
                                }else{
                                    onMarkAsPayed(reminder)
                                }

                            }
                        }
                ))
            }
        }
        swipeHelper.attachToRecyclerView(rvListEvents)
    }

    private fun onMarkAsPayed(reminder: FavoritoBean?) {
        homeActivity?.muestraProgressDialog("Procesando")
        val service = FavouritesServices()
        service.pagarFavorito(object : RetrofitListener<Boolean> {
            override fun showMessage(s: String) {/*No implementation*/}
            override fun onFailure(throwable: Throwable) {
                (activity as HomeActivity).ocultaProgressDialog()
                (activity as HomeActivity).despliegaModal(true, false, getString(R.string.error),
                        throwable.message, CommonDialogFragmentCallBack {reloadFragment()})
                Logger.LOGGER.throwing("PAGAR FAVORITOS", 1, throwable, "Error al Cambiar Estatús a Pagado")
            }

            override fun onSuccess(isPayed: Boolean) {
                homeActivity?.ocultaProgressDialog()
                if(isPayed){
                    reloadFragment()
                }else{
                    homeActivity?.despliegaModal(true, false, getString(R.string.error),
                            "No se pudo MARCAR COMO PAGADO. Intente más tarde.", CommonDialogFragmentCallBack {reloadFragment()})
                }
            }
        }, reminder?.fecha,
                reminder?.alias,
                ApiData.APIDATA.datosSesion.idSesion,
                MposApplication.getInstance().datosLogin.datosTpv.tpvcod)
    }

    fun deleteReminder(panelItem: PanelItem) {
        homeActivity?.muestraProgressDialog("Eliminando")
        val service = FavouritesServices()
        service.bajaFavorito(object : RetrofitListener<Boolean> {
            override fun showMessage(s: String) {/*No implementation*/}
            override fun onFailure(throwable: Throwable) {
                homeActivity?.ocultaProgressDialog()
                homeActivity?.despliegaModal(true, false, getString(R.string.error),
                        throwable.message, CommonDialogFragmentCallBack {homeActivity?.regresaMenu()})
                Logger.LOGGER.throwing("ELIMINAR FAVORITOS", 1, throwable, "Error al ELIMINAR Favorito: " + throwable.message)
            }
            override fun onSuccess(isSuccess: Boolean) {
                if(isSuccess){
                    getFavourites()
                }else{
                    homeActivity?.despliegaModal(true, false, getString(R.string.error),
                            "No se pudo realizar la Operación. Intente más Tarde", CommonDialogFragmentCallBack {homeActivity?.regresaMenu()})
                }
            }
        }, panelItem.alias,
                ApiData.APIDATA.datosSesion.idSesion,
                MposApplication.getInstance().datosLogin.datosTpv.tpvcod)
    }

    fun getFavourites() {
        val service = FavouritesServices()
        service.getFavoritos(object : RetrofitListener<List<FavoritoBean>> {
            override fun showMessage(s: String) {/*No implementation*/}
            override fun onFailure(throwable: Throwable) {
                homeActivity?.ocultaProgressDialog()
                homeActivity?.despliegaModal(true, false, getString(R.string.error),
                        throwable.message, CommonDialogFragmentCallBack {homeActivity?.regresaMenu()})
                Logger.LOGGER.throwing("OBTENER FAVORITOS", 1, throwable, "Error al consultar los Favoritos")
            }

            override fun onSuccess(favoritoBeans: List<FavoritoBean>) {
                homeActivity?.ocultaProgressDialog()
                if(!favoritoBeans.isNullOrEmpty()) {
                    homeActivity?.listaFavoritos = favoritoBeans
                    reloadFragment()
                }else{
                    homeActivity?.listaFavoritos = favoritoBeans
                    homeActivity?.regresaMenu()
                }
            }

        }, MposApplication.getInstance().datosLogin.datosTpv.tpvcod,
                ApiData.APIDATA.datosSesion.idSesion)
    }

    override fun OnCellClickListener(reminder: FavoritoBean?) {

        if (!reminder!!.procod.isNullOrEmpty()) {
            var productos: Productos = SigmaBdManager.getProducto(reminder.procod, OnFailureListener.BasicOnFailureListener("", ""))

            if(reminder.estado.equals("PAGADO")){
                homeActivity!!.showDialogReminders(R.layout.dialog_reminders, getString(R.string.cancelar), "Pagar", R.drawable.ic_ana_exitoso,
                        "¡Ya Pagaste este Servicio!", "¿Seguro que Quieres\n Pagar de Nuevo?", object : CommonDialogFragmentCallBackWithCancel {
                    override fun onCancel() { /*NO implementation*/ }

                    override fun onAccept() {
                        if (productos != null && productos.categoria != null) {
                            loadPayTransactionFragment(productos)
                        }
                    }
                })


            }else{
                if (productos != null && productos.categoria != null) {
                    loadPayTransactionFragment(productos)
                }
            }
        }else{
            onMarkAsPayed(reminder)
        }
    }


    private fun loadPayTransactionFragment(productos: Productos){
        val formularioFragment = FormularioFragmentFactory.build(productos)
        val manejadorFragments = ManejadorFragments(activity.supportFragmentManager)
        manejadorFragments.cargarFragmentPantallaCompleta(formularioFragment, activity)
    }

    override fun onItemPanelClickListener(panelItem: PanelItem) {
        //Implementar click de favoritos

        when (panelItem.alias) {
            "Ver más" -> {
                loadMyFavouritesFragment()
            }
            "Más" -> {
                var existFavorite = true;
                val intent = Intent(activity, ListServicesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("existFavorite", existFavorite)
                startActivity(intent)
            }
            else -> {
                val bottomSheet = panelItem?.let {
                    ReminderBottomSheetDialog((object : ReminderBottomSheetDialog.BottomSheetListener {
                        override fun onDelete(panelItem: PanelItem?) {
                                if ((SystemClock.elapsedRealtime() - mLastClickTime > 1000) && panelItem != null) {
                                    deleteReminder(panelItem)
                                }
                                mLastClickTime = SystemClock.elapsedRealtime()
                        }
                    }), it)
                }
                fragmentManager?.let {
                    bottomSheet?.show(it, "BottomSheetCalendar")
                }
            }
        }
    }


    override fun onFailure(p0: Throwable?) {
        //No implementation
    }

    override fun isTomandoBack(): Boolean {
        homeActivity?.closeFragment(this)
        return true
    }

    private fun reiniciarContador() {
        if (getActivity() is HomeActivity) {
            val activityHome = getActivity() as HomeActivity?
            activityHome!!.iniciarContador()
        }
    }

    private fun reloadFragment() {
        getFragmentManager()!!.beginTransaction().detach(this).attach(this).commit();
    }

    private fun loadMyFavouritesFragment() {
        val loadMyFavouritesFragment = MyFavouritesFragment()
        loadMyFavouritesFragment.homeActivity = homeActivity
        (activity as HomeActivity).cargarFragmentsCuenta(View.GONE,
                (activity as HomeActivity).generaListener(loadMyFavouritesFragment))
    }


}
