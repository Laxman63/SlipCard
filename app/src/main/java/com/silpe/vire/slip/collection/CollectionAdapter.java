package com.silpe.vire.slip.collection;

import android.content.Context;
import android.content.Intent;
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
import com.silpe.vire.slip.fragments.AccountActivity;
import com.silpe.vire.slip.fragments.ConnectionActivity;
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
    private CollectionHashList mUsers;
    private FragmentManager mFragmentManager;
    private Context mContext;
    private int mReplaceId;

    CollectionAdapter(FragmentManager fragmentManager, int replaceId, Context context) {
        mFragmentManager = fragmentManager;
        mContext = context;
        mReplaceId = replaceId;
        mUsers = new CollectionHashList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUsers.get(getItemCount() - position - 1);
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
        holder.getToplevel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ConnectionActivity.class);
                intent.putExtra(AccountActivity.RESULT_USER, user);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    boolean update(User user) {
        return mUsers.update(user, this);
    }

    void update(List<User> users) {
        this.mUsers.update(users, this);
    }

    void remove(String uid) {
        mUsers.remove(uid, this);
    }

}

class CollectionCardClickListener implements View.OnClickListener {

    private final User mUser;
    private final FragmentManager mFragmentManager;
    private final int mReplaceId;

    CollectionCardClickListener(User user, FragmentManager fragmentManager, int replaceId) {
        mUser = user;
        mFragmentManager = fragmentManager;
        mReplaceId = replaceId;
    }

    @Override
    public void onClick(View v) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(mReplaceId, ConnectionFragment.newInstance(mUser), null)
                .addToBackStack(null)
                .commit();
    }
}