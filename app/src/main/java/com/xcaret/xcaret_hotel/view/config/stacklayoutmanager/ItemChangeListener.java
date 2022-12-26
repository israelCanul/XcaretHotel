package com.xcaret.xcaret_hotel.view.config.stacklayoutmanager;

import android.view.View;

public interface ItemChangeListener {
    /**
     *
     * @param itemView the new item in the base position
     * @param position the item's  position in list
     */
    void onItemChange(View itemView, int position);
}
