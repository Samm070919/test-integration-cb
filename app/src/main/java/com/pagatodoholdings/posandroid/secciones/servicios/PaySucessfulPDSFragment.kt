package com.pagatodoholdings.posandroid.secciones.servicios

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO
import com.pagatodoholdings.posandroid.MposApplication
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country
import com.pagatodoholdings.posandroid.databinding.FragmentPagoExitosoPdsBinding
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity
import com.pagatodoholdings.posandroid.secciones.recargas.SendTicketEmailDialog
import com.pagatodoholdings.posandroid.secciones.retrofit.FavoritoBean
import com.pagatodoholdings.posandroid.utils.Logger
import com.pagatodoholdings.posandroid.utils.Utilities
import com.pagatodoholdings.posandroid.utils.UtilsDate
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos
import net.fullcarga.android.api.data.respuesta.AbstractRespuesta
import net.fullcarga.android.api.data.respuesta.Respuesta
import java.math.BigDecimal

class PaySucessfulPDSFragment : AbstractFragment {
    //datos
    private var flag : String
    private var icono: Drawable
    private var montoTotal: BigDecimal = BigDecimal("0.00")
    private var referenciaPago : String
    private var autorizacion : String
    private var folio : String
    //binding
    private lateinit var binding: FragmentPagoExitosoPdsBinding
    private var producto : Productos? = null
    private var operacion : Operaciones
    private var respuesta : AbstractRespuesta
    //private var aActivity : AbstractActivity? = null

    constructor(flag : String,
                iconoServicio: Drawable,
                montoTotalServicio: BigDecimal,
                referencia : String,
                autorizacion : String,
                folioF : String,
                producto : Productos,
                operacion: Operaciones,
                respuesta: AbstractRespuesta
    ){
        this.flag = flag
        this.icono = iconoServicio
        this.montoTotal = montoTotalServicio
        this.referenciaPago = referencia
        this.autorizacion = folioF
        this.folio = autorizacion
        this.producto = producto
        this.operacion = operacion
        this.respuesta = respuesta

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPagoExitosoPdsBinding.inflate(inflater, container, false)
        //aActivity = activity as AbstractActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconoPagoExitoso.setImageDrawable(icono)
        binding.tvFechaPago.setText(UtilsDate.getDateNewFormat())
        binding.tvReferenciaUnica.setText(referenciaPago.chunked(4).joinToString(" "))
        binding.tvAutorizacionNumero.setText(autorizacion.chunked(3).joinToString(" "))
        binding.tvFolio.setText(folio.chunked(4).joinToString(" "))
        if(flag.equals("PDS")){
            initUIFromPDS()
        }else if(flag.equals("TAE")){
            initUIFromTae()
        }
    }

    @SuppressLint("NewApi")
    private fun initUIFromPDS(){
        binding.llReferenciaUnica.visibility = View.VISIBLE
        binding.llFolio.visibility = View.VISIBLE
        binding.llAutorizacion.visibility = View.VISIBLE
        var newReminder = FavoritoBean()
        newReminder.procod = producto!!.codigo

        binding.tvMontoPagado.setMonto (montoTotal.toPlainString())

        binding.btnTerminar.setText(getString(R.string.txt_finish))


        binding.ivShareTicket.setOnClickListener({
            Toast.makeText(context,"Compartiendo ticket",Toast.LENGTH_LONG).show()
        })
        binding.btnTerminar.setOnClickListener{
            cargarTicketFragment(operacion,respuesta)
            //(activity as HomeActivity).regresaMenu()
        }
        /*
        binding.btnCerrarPago.setOnClickListener({
            (activity as HomeActivity).regresaMenu()
        })
         */
    }

    @SuppressLint("NewApi")
    private fun initUIFromTae(){
        binding.tvPagoTxt.setText("Recarga Exitosa")
        binding.llReferenciaUnica.visibility = View.VISIBLE
        binding.llFolio.visibility = View.VISIBLE
        binding.llAutorizacion.visibility = View.GONE
        var newReminder = FavoritoBean()
        newReminder.procod = producto!!.codigo

        //Montos para todos los paises
        binding.tvMontoPagado.setMonto (montoTotal.toPlainString())
        binding.btnTerminar.setText(getString(R.string.txt_finish))


        binding.ivShareTicket.setOnClickListener({
            Toast.makeText(context,"Compartiendo ticket",Toast.LENGTH_LONG).show()
        })
        binding.btnTerminar.setOnClickListener{
            cargarTicketFragment(operacion,respuesta)
            //(activity as HomeActivity).regresaMenu()
        }

    }

    override fun onFailure(p0: Throwable?) {
        Logger.LOGGER.throwing(p0!!.message, 1, p0, getString(R.string.error_con_bd))
    }

    override fun isTomandoBack(): Boolean {
        return false;
    }

    private fun cargarTicketFragment(
            operacion: Operaciones,
            respuesta: Respuesta) {
                val dialog = SendTicketEmailDialog()
        dialog.isCancelable = false
        dialog.setArgs(respuesta, operacion, object : SendTicketEmailDialog.TicketSendInteractionListener {
            override fun
                    onEmailSentSuccessfully() {
                if(preferenceManager.getValue(
                                TIPO_USUARIO,
                                ""
                        ).equals(Utilities.TipoUsuario.MERC.getTipo()) ||
                        preferenceManager.getValue(
                                TIPO_USUARIO,
                                ""
                        ).equals(Utilities.TipoUsuario.PROF.getTipo())
                ){
                            goToSaveFavorites()
                        }
            }

           override fun onEmailSendCancel() {
               goToSaveFavorites()
           }
        })
       dialog.show((activity as HomeActivity).supportFragmentManager, "SendTicketEmailDialog")

    }

    private fun goToSaveFavorites() {
        val newReminder = FavoritoBean()
        newReminder.procod = getProduct(operacion)?.codigo
        val transaction = (activity as HomeActivity).supportFragmentManager.beginTransaction()
        val dialogGuardarServicio = DialogGuardarServicio(icono, getProduct(operacion)!!)
        dialogGuardarServicio.setReminder(newReminder)
        dialogGuardarServicio.setType("Añadir")
        dialogGuardarServicio.setOnDismissListener(DialogInterface.OnDismissListener {
            (activity as HomeActivity).restauraHome()
        })
        dialogGuardarServicio.setMessageListener(object : DialogGuardarServicio.OnMessageToSnackBarListener {
            override fun onMessageToSnackBarListener(boolean: Boolean, message: String) {
                //(activity as HomeActivity).restauraHome()
            }
        })
        dialogGuardarServicio.show(transaction, "DialogGuardarServicio")
    }

    private fun getProduct(operacion: Operaciones): Productos? {
        return SigmaBdManager.getProducto(operacion,
                OnFailureListener.BasicOnFailureListener(TAG, "ErrorAlConsultar"))
    }

}