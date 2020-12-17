package com.pagatodoholdings.posandroid.utils;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria.AccountAdapter;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria.CardsAdapter;

public class SwipeController extends ItemTouchHelper.SimpleCallback  {
    SwipeControllerListener listener;

    public SwipeController(int dragDirs, int swipeDirs, SwipeControllerListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && viewHolder instanceof AccountAdapter.CardViewHolder) {
            final View foregroundView = ((AccountAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }else if (viewHolder != null){
            final View foregroundView = ((CardsAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof AccountAdapter.CardViewHolder) {
            final View foregroundView = ((AccountAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }else{
            final View foregroundView = ((CardsAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof AccountAdapter.CardViewHolder) {
            final View foregroundView = ((AccountAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }else{
            final View foregroundView = ((CardsAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof AccountAdapter.CardViewHolder) {
            final View foregroundView = ((AccountAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }else{
            final View foregroundView = ((CardsAdapter.CardViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }


    public interface SwipeControllerListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
