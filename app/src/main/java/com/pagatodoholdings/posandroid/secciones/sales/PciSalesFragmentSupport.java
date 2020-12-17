package com.pagatodoholdings.posandroid.secciones.sales;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Objects;

public abstract class PciSalesFragmentSupport<T> extends Fragment {

    protected PciSalesActivity activity;
    protected T binding;
    protected Resources resources;


    public abstract void init();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (PciSalesActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        resources = Objects.requireNonNull(getContext()).getResources();
    }
}
