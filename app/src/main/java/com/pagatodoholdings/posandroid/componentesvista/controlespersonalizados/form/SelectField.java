package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.form;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.SelectFieldBinding;

public class SelectField extends FrameLayout {

    private static final float VALUE_VERTICAL_CENTER = 0.5f;

    private final SelectFieldBinding binding;
    private final ConstraintSet expanded;
    private final ConstraintSet colapsed;

    public SelectField(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.select_field, this, false);

        TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFields);
        if (customAttrs.hasValue(R.styleable.CustomFields_field_label)) {
            binding.etLabel.setText(customAttrs.getString(R.styleable.CustomFields_field_label));
        }

        binding.sInput.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        expanded = new ConstraintSet();
        expanded.clone(binding.clContainer);

        colapsed = new ConstraintSet();
        colapsed.clone(binding.clContainer);

        colapsed.setVisibility(R.id.s_input, View.GONE);
        colapsed.setVerticalBias(R.id.et_label, VALUE_VERTICAL_CENTER);
        colapsed.applyTo(binding.clContainer);

        binding.sInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.sInput.performClick();
            }
        });


        binding.clContainer.setOnClickListener(v -> {
            if (binding.sInput.getAdapter()!=null) {
                setExpanded();
                binding.clContainer.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.clContainer.getWindowToken(), 0);
            }
        });



        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOT IMPLEMENTED
            }
        };
        binding.sInput.setOnItemSelectedListener(listener);

        addView(binding.getRoot());

    }

    private void setExpanded(){
        Transition transition = new AutoTransition();
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                binding.sInput.performClick();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onTransitionPause(Transition transition) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onTransitionResume(Transition transition) {
                //NOT IMPLEMENTED
            }
        });
        TransitionManager.beginDelayedTransition(binding.clContainer, transition);
        expanded.applyTo(binding.clContainer);
    }

    public SelectFieldBinding getBinding() {
        return binding;
    }

}
