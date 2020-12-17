package com.pagatodoholdings.posandroid.secciones.recargas

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.pagatodoholdings.posandroid.R
import com.pagatodoholdings.posandroid.secciones.ticket.ETicket
import kotlinx.android.synthetic.main.send_ticket_dialog.view.*
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones
import net.fullcarga.android.api.data.respuesta.Respuesta

class SendTicketEmailDialog : DialogFragment() {
    private lateinit var inflatedView: View
    private var respuesta: Respuesta? = null
    private var operaciones: Operaciones? = null
    private var ticketSendInteractionListener: TicketSendInteractionListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            setStyle(STYLE_NO_TITLE, R.style.DialogStyle)

            inflatedView = inflater.inflate(R.layout.send_ticket_dialog, null)
            inflatedView.ticketFragmentLoadingBar.visibility = View.GONE
            inflatedView.sendButton.setOnClickListener(onSendClickedListener)
            inflatedView.btnCerrar.setOnClickListener {
                //(activity as HomeActivity).restauraHome()
                dismiss()
                ticketSendInteractionListener?.onEmailSentSuccessfully()
            }
            inflatedView.btn_regresar.setOnClickListener {
                dismiss()
                ticketSendInteractionListener?.onEmailSendCancel()
            }

            builder.setView(inflatedView)
                    .create()
        } ?: throw IllegalStateException("SendTicketEmailDialog: Activity cannot be null")
    }

    /**
     * Setear los argumentos justo antes de inflar el fragmento
     * */
    fun setArgs(respuesta: Respuesta, operaciones: Operaciones, ticketSendInteractionListener: TicketSendInteractionListener?) {
        this.ticketSendInteractionListener = ticketSendInteractionListener
        this.operaciones = operaciones
        this.respuesta = respuesta
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(false)
    }

    private val onSendClickedListener = View.OnClickListener {
        val emailText = inflatedView.editText3.text.trim()

        if (emailText.isEmpty()) {
            inflatedView.editText3.error = "Ingresa el correo electrónico"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            inflatedView.editText3.error = getString(R.string.email_invalido)
        } else {
            inflatedView.ticketFragmentLoadingBar.visibility = View.VISIBLE

            val imm = it.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            inflatedView.sendButton.isEnabled = false
            inflatedView.btn_regresar.isEnabled = false

            ETicket(operaciones, respuesta, null, emailText.toString(), object : ETicket.EnvioMailInterfece {
                override fun onFailMail() {
                    Toast.makeText(activity,
                            getString(R.string.error_envio_correo),
                            Toast.LENGTH_LONG).show()
                    restoreButtons()
                }

                override fun onSuccesMail() {
                    inflatedView.groupForm.visibility = ConstraintLayout.GONE
                    inflatedView.ticketFragmentLoadingBar.visibility = View.GONE
                    inflatedView.groupSuccess.visibility = ConstraintLayout.VISIBLE
                }
            }).init(true)
        }
    }

    private fun restoreButtons() {
        inflatedView.sendButton.isEnabled = true
        inflatedView.btn_regresar.isEnabled = true

        inflatedView.ticketFragmentLoadingBar.visibility = View.GONE
    }

    interface TicketSendInteractionListener {
        fun onEmailSentSuccessfully()
        fun onEmailSendCancel()
    }
}