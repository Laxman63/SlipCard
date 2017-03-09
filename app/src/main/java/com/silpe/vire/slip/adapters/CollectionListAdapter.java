package com.silpe.vire.slip.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionListAdapter extends ArrayAdapter<CollectionListItem> {

    private final Map<CollectionListItem, Integer> mIdMap = new HashMap<>();

    public CollectionListAdapter(Context context, int layoutId, int textViewResourceId, List<CollectionListItem> items) {
        super(context, layoutId, textViewResourceId, items);
        final int size = items.size();
        for (int i = 0; i < size; i++) {
            mIdMap.put(items.get(i), i);
        }
    }

    @Override
    public
    @NonNull
    View getView(int i, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.collection_card_preview, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CollectionListItem item = getItem(i);
        holder.getName().setText(item.fullName);
        holder.getDescription().setText(item.description);
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(item.pictureRef)
                .error(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.empty_profile, null))
                .into(holder.getPicture());
        return view;
    }

    @Override
    public long getItemId(int position) {
        return mIdMap.get(getItem(position));
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        private View row;
        private Icon icon;
        private TextView firstLine;
        private TextView secondLine;

        ViewHolder(View row) {
            this.row = row;
        }

        TextView getName() {
            if (this.firstLine == null) {
                this.firstLine = (TextView) row.findViewById(R.id.card_name);
            }
            return this.firstLine;
        }

        TextView getDescription() {
            if (this.secondLine == null) {
                this.secondLine = (TextView) row.findViewById(R.id.card_description);
            }
            return this.secondLine;
        }

        Icon getPicture() {
            if (this.icon == null) {
                this.icon = (Icon) row.findViewById(R.id.card_picture);
            }
            return this.icon;
        }

    }

}
