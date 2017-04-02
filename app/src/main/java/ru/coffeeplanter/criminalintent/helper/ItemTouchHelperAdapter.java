package ru.coffeeplanter.criminalintent.helper;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    /**
     * Вызывется при удалении элемента списка с помошью свайпа.
     *
     * @param position — индекс элемента.
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    void onItemDismiss(int position);

}
