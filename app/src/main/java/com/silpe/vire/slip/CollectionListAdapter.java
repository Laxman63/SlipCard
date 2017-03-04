package com.silpe.vire.slip;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CollectionListAdapter extends ArrayAdapter<String> {

    private final Map<String, Integer> mIdMap = new HashMap<>();

    CollectionListAdapter(Context context, int layoutId, int textViewResourceId, List<String> objects) {
        super(context, layoutId, textViewResourceId, objects);
        final int size = objects.size();
        for (int i = 0; i < size; i++) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        final String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
