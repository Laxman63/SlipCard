package com.silpe.vire.slip.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.collection.CollectionLayoutManager;
import com.silpe.vire.slip.components.BasicValueEventListener;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.components.RoundedBitmap;
import com.silpe.vire.slip.dtos.GeoMessage;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModelListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment implements SessionModelListener<User> {

    public static final int RESULT_MESSAGE = 543;

    public static MessageFragment newInstance(User user) {
        MessageFragment frag = new MessageFragment();
        frag.user = user;
        return frag;
    }

    private User user;

    private RecyclerView listView;

    public MessageFragment() {
        this.user = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        listView = (RecyclerView) view.findViewById(R.id.itemsRecyclerView);
        Button writeBtn = (Button) view.findViewById(R.id.message_writeButton);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageFragment.this.getContext(), LeaveNoteActivity.class);
                intent.putExtra(AccountActivity.RESULT_USER, user);
                startActivityForResult(intent, RESULT_MESSAGE);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(getString(R.string.database_messages))
                        .addListenerForSingleValueEvent(new BasicValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<GeoMessage> msgs = new ArrayList<>();
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot child : childSnapshot.getChildren()) {
                                        GeoMessage msg = child.getValue(GeoMessage.class);
                                        if (msg == null) continue;
                                        msgs.add(msg);
                                    }
                                }
                                listView.setAdapter(new Adapter(msgs));
                                listView.setLayoutManager(new CollectionLayoutManager(getContext()));
                                refreshLayout.setRefreshing(false);
                            }
                        });
            }
        });
        return view;
    }


    @Override
    public void valueUpdated(User user) {
        this.user = user;
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<GeoMessage> msgs;

        Adapter(List<GeoMessage> msgs) {
            this.msgs = msgs;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            GeoMessage msg = msgs.get(position);
            holder.getMessage().setText(msg.getMessage());
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_users))
                    .child(msg.getUid())
                    .addListenerForSingleValueEvent(new BasicValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            holder.getName().setText(user.getFullName());
                            Glide.with(MessageFragment.this)
                                    .using(new FirebaseImageLoader())
                                    .load(user.getProfilePictureReference(MessageFragment.this.getContext()))
                                    .asBitmap()
                                    .centerCrop()
                                    .signature(new TimestampSignature(user.getSignature()))
                                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile_round, null))
                                    .into(new RoundedBitmap(holder.getProfile(), MessageFragment.this.getContext()));
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return msgs.size();
        }
    }

}

class ViewHolder extends RecyclerView.ViewHolder {

    private final View view;
    private ProfilePictureView profile;
    private TextView message;
    private TextView name;

    ViewHolder(View view) {
        super(view);
        this.view = view;
    }


    public ProfilePictureView getProfile() {
        if (profile == null) profile = (ProfilePictureView) view.findViewById(R.id.card_picture);
        return profile;
    }


    public TextView getMessage() {
        if (message == null) message = (TextView) view.findViewById(R.id.card_message);
        return message;
    }

    public TextView getName() {
        if(name == null) name = (TextView) view.findViewById(R.id.card_username);
        return name;
    }


}
