package com.silpe.vire.slip.collection;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.Icon;
import com.silpe.vire.slip.dtos.User;

import java.util.List;

/**
 * TODO
 * -- Extend the {@code RecyclerView} class and make our own implementation that will attempt to
 * refresh the connections display list when swiping down
 * -- Display a loading indicator while querying database
 */
class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private Icon profilePicture;
        private TextView fullName;
        private TextView description;

        ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        Icon getProfilePicture() {
            if (profilePicture == null) {
                profilePicture = (Icon) view.findViewById(R.id.card_picture);
            }
            return profilePicture;
        }

        TextView getFullName() {
            if (fullName == null) {
                fullName = (TextView) view.findViewById(R.id.card_name);
            }
            return fullName;
        }

        TextView getDescription() {
            if (description == null) {
                description = (TextView) view.findViewById(R.id.card_description);
            }
            return description;
        }

        private Context getContext() {
            return view.getContext();
        }

        private String getString(int stringId) {
            return getContext().getString(stringId);
        }

        private Drawable getPlaceholderProfilePicture() {
            return ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.empty_profile, null);
        }

    }

    /**
     * TODO
     * -- Replace with a better collection that
     * -> Is synchronized and supports multiple threads
     * -> Permits easier user updating from UID
     * -> Gracefully passes this update to the adapter
     */
    private CollectionHashList users;

    CollectionAdapter() {
        users = new CollectionHashList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = users.get(getItemCount() - position - 1);
        final StorageReference pRef;
        pRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child(holder.getString(R.string.database_users))
                .child(user.getUid())
                .child(holder.getString(R.string.database_profile_picture));
        holder.getFullName().setText(user.getFullName());
        holder.getDescription().setText(user.getDescription());
        Glide.with(holder.getContext())
                .using(new FirebaseImageLoader())
                .load(pRef)
                .error(holder.getPlaceholderProfilePicture())
                .into(holder.getProfilePicture());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    boolean update(User user) {
        return users.update(user, this);
    }

    void update(List<User> users) {
        this.users.update(users, this);
    }

    void remove(String uid) {
        users.remove(uid, this);
    }

}
