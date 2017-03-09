package com.silpe.vire.slip.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 * -- Extend the {@code ListView} class and make our own implementation that will attempt to
 *    refresh the connections display list when swiping down
 * -- Display a loading indicator while querying database
 */
public class CollectionListAdapter extends ArrayAdapter<User> {

    private final Map<User, Integer> mIdMap = new HashMap<>();

    public CollectionListAdapter(Context context, List<User> users, int layoutId, int textViewId) {
        super(context, layoutId, textViewId, users);
        final int size = users.size();
        for (int i = 0; i < size; i++) {
            mIdMap.put(users.get(i), i);
        }
    }

    private String getString(int stringId) {
        return getContext().getString(stringId);
    }

    private Drawable getPlaceholderProfilePicture() {
        return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.empty_profile, null);
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        CollectionListItem item;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.collection_card_preview, parent, false);
            view.setTag(item = new CollectionListItem(view));
        } else {
            item = (CollectionListItem) view.getTag();
        }
        final User user = getItem(position);
        final StorageReference pRef;
        if (user != null) {
            pRef = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(getString(R.string.database_users))
                    .child(user.uid)
                    .child(getString(R.string.database_profile_picture));
            item.getNameText().setText(user.getFullName());
            item.getDescriptionText().setText(user.getDescription());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(pRef)
                    .error(getPlaceholderProfilePicture())
                    .into(item.getProfilePicture());
        }
        /*
         * TODO
         * -- Handle unexpected error in which the User DTO disappears between constructing
         *    the list adapter and rendering it
         *    -> Append to reload stack
         */
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

}
