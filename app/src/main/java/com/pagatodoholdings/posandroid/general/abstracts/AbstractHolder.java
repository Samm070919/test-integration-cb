package com.pagatodoholdings.posandroid.general.abstracts;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.pagatodoholdings.posandroid.R;


public abstract class AbstractHolder<T> extends RecyclerView.ViewHolder {
    protected View mainView;
    protected ImageView civPressView;

    public AbstractHolder(final View itemView) {
        super(itemView);
        this.mainView = itemView;
        civPressView = mainView.findViewById(R.id.icono_producto_presionado);
    }

    public abstract void onBind(final T object);

    public void setSelect(final boolean select) {
        mainView.setSelected(select);
    }

    public View getMainView() {
        return mainView;
    }

    public void setItemInPressedMode(final int visible) {
        civPressView.setVisibility(visible);
    }

}