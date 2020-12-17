package com.pagatodoholdings.posandroid.secciones.recargas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pagatodo.sigmalib.ApiData
import com.pagatodo.sigmalib.SigmaBdManager
import com.pagatodo.sigmalib.listeners.OnFailureListener
import com.pagatodo.sigmalib.listeners.OnFailureListener.BasicOnFailureListener
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController
import kotlinx.android.synthetic.main.single_recargas_element.view.*
import java.math.BigDecimal

class RecargasAdapter(
        private val myDataset : ArrayList<String>,
        val btnListener : BtnClickListener
) : RecyclerView.Adapter<RecargasAdapter.MyViewHolder>(){

    companion object{
        var mClickListenner : BtnClickListener? = null
    }

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val text = view.recarga_text_view as TextView

    }


    lateinit var parametroMoneda : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(
                        R.layout.single_recargas_element,
                        parent,
                        false
                )

        parametroMoneda = SigmaBdManager.getParametroFijo("0021", BasicOnFailureListener(this.javaClass.simpleName, "Error al obtener parametro fijo"))

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mClickListenner = btnListener

        if(myDataset[position].equals("Otro Monto")){
            holder.text.text = myDataset[position]
        }else{
         var Importe =   SigmaBdManager.formatoSaldo(ApiData.APIDATA.datosSesion.datosTPV.convertirImporte((myDataset[position])),OnFailureListener {})

            holder.text.text = Importe.replace(parametroMoneda, " " + parametroMoneda + " ")
        }


        holder.text.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(mClickListenner != null){
                    mClickListenner?.onBtnClick(myDataset.get(position))
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    open interface BtnClickListener{
        fun onBtnClick(monto : String)
    }

}