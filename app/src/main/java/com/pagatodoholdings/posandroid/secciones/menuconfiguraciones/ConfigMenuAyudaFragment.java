package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuAyudaBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.Logger;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ConfigMenuAyudaFragment extends AbstractConfigMenu {

    private FragmentConfigMenuAyudaBinding binding;
    private int questionSelected = -1;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {

        binding = FragmentConfigMenuAyudaBinding.inflate(infalter, container, false);

        binding.TextViewCuestion4Answer.setEllipsize(TextUtils.TruncateAt.START);

        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuAyudaFragment.this));

        binding.TextViewCuestion1.setOnClickListener(v -> configQuestion1());

        binding.TextViewCuestion2.setOnClickListener(v -> configQuestion2());

        binding.TextViewCuestion3.setOnClickListener(v -> configQuestion3());


        binding.TextViewCuestion4.setOnClickListener(v -> configQuestion4());

        binding.TextViewCuestion5.setOnClickListener(v -> configQuestion5());


        binding.TextViewCuestion6.setOnClickListener(v -> configQuestion6());

        binding.cardViewCorreo.setOnClickListener(v -> correoAyuda());

        binding.cardViewLlamar.setOnClickListener(v -> llamarAyuda());

        binding.cardViewWhats.setOnClickListener(v -> chatWhats());

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
    }

    private void configQuestion1() {
        boolean isSelected = false;
        if(binding.TextViewCuestion1Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion1Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion1Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 0;
        }
        setTextQuestion(binding.TextViewCuestion1, isSelected);
    }

    private void configQuestion2() {
        boolean isSelected = false;
        if(binding.TextViewCuestion2Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion2Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion2Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 1;
        }
        setTextQuestion(binding.TextViewCuestion2, isSelected);
    }

    private void configQuestion3() {
        boolean isSelected = false;
        if(binding.TextViewCuestion3Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion3Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion3Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 2;
        }
        setTextQuestion(binding.TextViewCuestion3, isSelected);
    }

    private void configQuestion4() {
        boolean isSelected = false;
        if(binding.TextViewCuestion4Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion4Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion4Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 3;
        }
        setTextQuestion(binding.TextViewCuestion4, isSelected);
    }

    private void configQuestion5() {
        boolean isSelected = false;
        if(binding.TextViewCuestion5Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion5Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion5Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 4;
        }
        setTextQuestion(binding.TextViewCuestion5, isSelected);
    }

    private void configQuestion6() {
        boolean isSelected = false;
        if(binding.TextViewCuestion6Answer.getVisibility() == VISIBLE)
        {
            binding.TextViewCuestion6Answer.setVisibility(GONE);
            questionSelected = -1;
        }else{
            isSelected = true;
            binding.TextViewCuestion6Answer.setVisibility(VISIBLE);
            hideOtherQuestions();
            questionSelected = 5;
        }
        setTextQuestion(binding.TextViewCuestion6, isSelected);
    }

    private void setTextQuestion(TextView question, boolean isSelected){
        if (isSelected){
            question.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        }else{
            question.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        }
    }

    private void hideOtherQuestions() {
        if (questionSelected == -1)
            return;
        TextView question;
        TextView answer;
        switch (questionSelected){
            case 0:
                question = binding.TextViewCuestion1;
                answer = binding.TextViewCuestion1Answer;
                break;
            case 1:
                question = binding.TextViewCuestion2;
                answer = binding.TextViewCuestion2Answer;
                break;
            case 2:
                question = binding.TextViewCuestion3;
                answer = binding.TextViewCuestion3Answer;
                break;
            case 3:
                question = binding.TextViewCuestion4;
                answer = binding.TextViewCuestion4Answer;
                break;
            case 4:
                question = binding.TextViewCuestion5;
                answer = binding.TextViewCuestion5Answer;
                break;
            case 5:
            default:
                question = binding.TextViewCuestion6;
                answer = binding.TextViewCuestion6Answer;
                break;
        }
        answer.setVisibility(GONE);
        setTextQuestion(question, false);
    }

    private void llamarAyuda()
    {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:"+getResources().getString(R.string.title_phone_ayuda)));
        try {
            startActivity(i);
        }catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.title_ayuda_dialog_llamada_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void chatWhats()
    {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.title_text_ayuda));
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try{
            startActivity(sendIntent);
        }catch (Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.title_ayuda_dialog_whats_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void correoAyuda()
    {
        String[] to = {getResources().getString(R.string.title_email_destinatario_ayuda)}; //Direcciones email  a enviar.
        String[] cc = {""}; //Direcciones email con copia.

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.title_body_N)  + " : " + MposApplication.getInstance().getDatosLogin().getCliente().getNombre()
                +  "\n" + getResources().getString(R.string.title_body_C) + " : " + MposApplication.getInstance().getDatosLogin().getCliente().getEmail()
                +  "\n" +  getResources().getString(R.string.title_body_Nu) + " : " + MposApplication.getInstance().getDatosLogin().getCliente().getTelefono() );
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.title_email_asunto_ayuda));
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar Correo Electrónico."));
            Logger.LOGGER.fine("CORREO ELECTRÓNICO", "Enviando Correo Electrónico");
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.title_ayuda_dialog_email_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }

}
