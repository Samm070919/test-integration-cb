package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.form;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.TextFieldBinding;

public class TextField extends FrameLayout {

    private static final float VALUE_VERTICAL_CENTER = 0.5f;

    private final TextFieldBinding binding;
    private final ConstraintSet expanded;
    private final ConstraintSet colapsed;

    public TextField(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.text_field, this, false);

        TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFields);
        if (customAttrs.hasValue(R.styleable.CustomFields_field_label)) {
            binding.etLabel.setText(customAttrs.getString(R.styleable.CustomFields_field_label));
        }

        expanded = new ConstraintSet();
        expanded.clone(binding.clContainer);

        colapsed = new ConstraintSet();
        colapsed.clone(binding.clContainer);

        colapsed.constrainHeight(R.id.et_input, 0);
        colapsed.setVerticalBias(R.id.et_label, VALUE_VERTICAL_CENTER);
        colapsed.applyTo(binding.clContainer);

        binding.etInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                setExpanded();
            } else {
                if (binding.etInput.getText().toString().isEmpty()) {
                    setCollapsed();
                }
            }
        });

        OnClickListener expandAction = v -> {
            setExpanded();
            binding.clContainer.requestFocus();
            InputMethodManager imm = (InputMethodManager)   context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etInput, 0);
        };
        binding.clContainer.setOnClickListener(expandAction);

        addView(binding.getRoot());

    }

    private void setExpanded(){
        TransitionManager.beginDelayedTransition(binding.clContainer);
        expanded.applyTo(binding.clContainer);
    }

    private void setCollapsed(){
        TransitionManager.beginDelayedTransition(binding.clContainer);
        colapsed.applyTo(binding.clContainer);
    }

    public TextFieldBinding getBinding() {
        return binding;
    }
}
