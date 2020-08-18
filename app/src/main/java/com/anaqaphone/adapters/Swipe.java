package com.anaqaphone.adapters;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class Swipe extends ItemTouchHelper.SimpleCallback {

    private SwipeListener listener;

    public Swipe(int dragDirs, int swipeDirs, SwipeListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        CartAdapter.Cart_Holder holder = (CartAdapter.Cart_Holder) viewHolder;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (dX > 0) {
                holder.llRight.setVisibility(View.VISIBLE);
                holder.llLeft.setVisibility(View.GONE);
            } else {
                holder.llRight.setVisibility(View.GONE);
                holder.llLeft.setVisibility(View.VISIBLE);
            }

        }
        getDefaultUIUtil().onDraw(c, recyclerView, holder.consForeground, dX, dY, actionState, isCurrentlyActive);

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        CartAdapter.Cart_Holder holder = (CartAdapter.Cart_Holder) viewHolder;

        getDefaultUIUtil().onDrawOver(c, recyclerView, holder.consForeground, dX, dY, actionState, isCurrentlyActive);

    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {


        if (viewHolder != null) {
            CartAdapter.Cart_Holder holder = (CartAdapter.Cart_Holder) viewHolder;

            getDefaultUIUtil().onSelected(holder.consForeground);


        }


    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder != null) {
            CartAdapter.Cart_Holder holder = (CartAdapter.Cart_Holder) viewHolder;

            getDefaultUIUtil().clearView(holder.consForeground);


        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwipe(viewHolder.getAdapterPosition(), direction);

        }


    }

    public interface SwipeListener {
        void onSwipe(int pos, int dir);
    }
}
