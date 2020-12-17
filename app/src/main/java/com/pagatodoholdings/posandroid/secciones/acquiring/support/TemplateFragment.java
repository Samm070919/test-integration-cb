package com.pagatodoholdings.posandroid.secciones.acquiring.support;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

public abstract class TemplateFragment<T> extends AbstractFragment {

    protected T binding;
    protected int res;
    protected HomeActivity homeActivity;
    protected Resources resources;
    public abstract void init();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        setHasOptionsMenu(true);
    }

    public abstract void setListener(HomeActivity homeActivity);
}
