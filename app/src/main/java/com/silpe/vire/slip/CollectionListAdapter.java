package com.silpe.vire.slip;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silpe.vire.slip.components.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionListAdapter extends ArrayAdapter<String> {

    private final Map<String, Integer> mIdMap = new HashMap<>();

    public CollectionListAdapter(Context context, int layoutId, int textViewResourceId, List<String> objects) {
        super(context, layoutId, textViewResourceId, objects);
        final int size = objects.size();
        for (int i = 0; i < size; i++) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.card_collection_listitem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.getFirstLine().setText("xd");
        holder.getSecondLine().setText("yolo");
        return convertView;
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

class ViewHolder {
    private View row;
    private Icon icon;
    private TextView firstLine;
    private TextView secondLine;

    ViewHolder(View row) {
        this.row = row;
    }

    TextView getFirstLine() {
        if (this.firstLine == null) {
            this.firstLine = (TextView) row.findViewById(R.id.firstLine);
        }
        return this.firstLine;
    }

    TextView getSecondLine() {
        if (this.secondLine == null) {
            this.secondLine = (TextView) row.findViewById(R.id.secondLine);
        }
        return this.secondLine;
    }

    Icon getIcon() {
        if (this.icon == null) {
            this.icon = (Icon) row.findViewById(R.id.itemIcon);
        }
        return this.icon;
    }

}