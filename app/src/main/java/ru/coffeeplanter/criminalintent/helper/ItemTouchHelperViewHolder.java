package ru.coffeeplanter.criminalintent.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

public interface ItemTouchHelperViewHolder {

    /**
     * Вызывается в случае, когда {@link ItemTouchHelper} определяет начало свайпа.
     * Реализация должна подветить элемент, чтобы показать, что он находится в активном состоянии.
     */
    void onItemSelected();


    /**
     * Вызывается, когда {@link ItemTouchHelper} закончил свайп.
     * С активного элемента нужно снять подсветку.
     */
    void onItemClear();

}
