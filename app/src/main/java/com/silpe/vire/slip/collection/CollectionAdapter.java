package com.silpe.vire.slip.collection;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.ConnectionFragment;
import com.silpe.vire.slip.image.TimestampSignature;

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
        private ProfilePictureView profilePicture;
        private LinearLayoutCompat toplevel;
        private TextView fullName;
        private TextView description;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        private ProfilePictureView getProfilePicture() {
            if (profilePicture == null) {
                profilePicture = (ProfilePictureView) view.findViewById(R.id.card_picture);
            }
            return profilePicture;
        }

        private LinearLayoutCompat getToplevel() {
            if (toplevel == null) {
                toplevel = (LinearLayoutCompat) view.findViewById(R.id.card_toplevel);
            }
            return toplevel;
        }

        private TextView getFullName() {
            if (fullName == null) {
                fullName = (TextView) view.findViewById(R.id.card_name);
            }
            return fullName;
        }

        private TextView getDescription() {
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

    }

    /**
     * TODO
     * -- Replace with a better collection that
     * -> Is synchronized and supports multiple threads
     * -> Permits easier user updating from UID
     * -> Gracefully passes this update to the adapter
     */
    private CollectionHashList users;
    private FragmentManager fragmentManager;

    CollectionAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
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
                .child(holder.getString(R.string.database_profilePicture));
        holder.getFullName().setText(user.getFullName());
        holder.getDescription().setText(user.getDescription());
        if (user.getSignature() > 0) {
            Glide.with(holder.getContext())
                    .using(new FirebaseImageLoader())
                    .load(pRef)
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(holder.getContext().getResources(), R.drawable.empty_profile, null))
                    .into(holder.getProfilePicture());
        } else {
            holder.getProfilePicture().setImageResource(R.drawable.empty_profile);
        }
        holder.getToplevel().setOnClickListener(new CollectionCardClickListener(user, fragmentManager));
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

class CollectionCardClickListener implements View.OnClickListener {

    private static final String CONNECTION_FRAGMENt = "connection";

    private final User user;
    private final FragmentManager fragmentManager;

    CollectionCardClickListener(User user, FragmentManager fragmentManager) {
        this.user = user;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(R.id.toplevel, ConnectionFragment.newInstance(user), CONNECTION_FRAGMENt)
                .addToBackStack(CONNECTION_FRAGMENt)
                .commit();
    }
}