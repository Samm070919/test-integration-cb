package com.pagatodoholdings.posandroid.secciones.inicio;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.SlidesIntroWalletBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import java.util.ArrayList;
import java.util.List;

public class SlideIntroWallet extends AbstractActivity {//NOSONAR

    private SlidesIntroWalletBinding binding;

    @Override
    protected void initUi() {

        binding = DataBindingUtil.setContentView(this, R.layout.slides_intro_wallet);
        binding.sliderPager.setAdapter(new CustomPager());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding.clMain.setBackgroundResource(R.color.colorWhiteBackground);

        final View backgroundDotsSlides = binding.getRoot().findViewById(R.id.states_clyo);
        backgroundDotsSlides.setBackgroundResource(R.color.colorWhiteBackground);
        final ImageView ivMarker = backgroundDotsSlides.findViewById(R.id.iv_marker);
        ivMarker.setBackgroundResource(R.drawable.circleselected);
        binding.indicador.changeIndicators(binding.sliderPager.getAdapter().getCount(), R.drawable.circle_unselected);
        binding.tvSaltarIntro.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        binding.tvSaltarIntro.setText(R.string.saltar_intro);
        binding.sliderPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //No se ocupa
            }

            @Override
            public void onPageSelected(final int position) {

                binding.indicador.animateStateChange(position);
                if (position>0){
                    binding.clMain.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteBackground));
                    binding.tvSaltarIntro.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    backgroundDotsSlides.setBackgroundResource(R.color.colorWhite);
                    ivMarker.setBackgroundResource(R.drawable.circleselected);
                    binding.indicador.changeIndicators(binding.sliderPager.getAdapter().getCount(), R.drawable.circle_unselected);
                    if (position == binding.sliderPager.getAdapter().getCount()-1){
                        binding.tvSaltarIntro.setText(R.string.continuar);
                    }else {
                        binding.tvSaltarIntro.setText(R.string.saltar_intro);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                //No se ocupa
            }
        });

        binding.tvSaltarIntro.setOnClickListener(v -> cambiaDeActividad(InicioActivity.class));
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return new ArrayList<>();
    }

    @Override
    protected void realizaAlPresionarBack() {
        //No se ocupa
    }

    public class CustomPager extends PagerAdapter {


        int[] slides = new int[] {
                R.layout.slides_intro_1,
                R.layout.slides_intro_2,
                R.layout.slides_intro_3};

        @Override
        public int getCount() {
            return slides.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
            return object.equals(view);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

            final LayoutInflater layoutInflater = LayoutInflater.from(SlideIntroWallet.this);

            final ViewGroup layout = (ViewGroup) layoutInflater.inflate(slides[position], container, false);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull final Object object) {

            container.removeView((View)object);
        }
    }
}
