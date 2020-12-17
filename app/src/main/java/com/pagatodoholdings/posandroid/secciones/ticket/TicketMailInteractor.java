package com.pagatodoholdings.posandroid.secciones.ticket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.InteractorListener;
import com.pagatodoholdings.posandroid.secciones.retrofit.ApiMposServicesInterface;
import com.pagatodoholdings.posandroid.secciones.ticket.models.EmailTicket;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TicketMailInteractor {

    private static final String TAG = TicketMailInteractor.class.getSimpleName();

    private InteractorListener interactorListener;
    private final ApiMposServicesInterface service;

    public TicketMailInteractor(final String baseUrl) {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void sendMailPDF(final String tpvCod, final String sesionID, final String operacionName, final boolean isPCI, final File pdf, final String mail, final String reflocal) {
        final Call<Boolean> call = service.ticket(
                MultipartBody.Part.createFormData("file", pdf.getName(), RequestBody.create(MediaType.parse("application/pdf"), pdf)),
                RequestBody.create(MultipartBody.FORM, mail),
                RequestBody.create(MultipartBody.FORM, operacionName),
                RequestBody.create(MultipartBody.FORM, String.valueOf(isPCI)),
                RequestBody.create(MultipartBody.FORM, reflocal),
                sesionID,
                tpvCod
        );

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final Call<Boolean> call, final Response<Boolean> response) {
                if (response.body() != null) {
                    Logger.LOGGER.fine(TAG, "Operacion Realizada UploadFile");
                    interactorListener.onResponse(response);
                } else {
                    interactorListener.onResponse(response.message());
                }
            }

            @Override
            public void onFailure(final Call<Boolean> call, final Throwable exc) {
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                interactorListener.onResponse(exc);
            }
        });
    }

    public void sendMailHtml(final String sesionID, final String email, final String html64, final String operacionName, final boolean isPCI, final String reflocal, final String tpvCod ) {
        EmailTicket emailTicket = new EmailTicket();
        emailTicket.setEmail(email);
        emailTicket.setHtml(html64);
        emailTicket.setOperacion(operacionName);
        emailTicket.setPci(isPCI);
        emailTicket.setRefLocal(reflocal);


        final Call<Boolean> call = service.eTicket(
                sesionID,
                tpvCod,
                emailTicket
        );

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final Call<Boolean> call, final Response<Boolean> response) {
                if (response.body() != null) {
                    Logger.LOGGER.fine(TAG, "Operacion Realizada email Send");
                    interactorListener.onResponse(response);
                } else {
//
                    interactorListener.onResponse(response);
                }
            }

            @Override
            public void onFailure(final Call<Boolean> call, final Throwable exc) {
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                interactorListener.onResponse(exc);
            }
        });
    }

    public void setInteractorListener(final InteractorListener interactorListener) {
        this.interactorListener = interactorListener;
    }
}
