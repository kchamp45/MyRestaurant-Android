package com.epicodus.myrestaurant.util;

/**
 * Created by kimlu on 10/28/17.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
