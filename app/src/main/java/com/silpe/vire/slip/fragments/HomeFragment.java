package com.silpe.vire.slip.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.adapters.CollectionListAdapter;
import com.silpe.vire.slip.dtos.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref = ref.child(getString(R.string.database_connections)).child(user.getUid());
            ref.addListenerForSingleValueEvent(new UserListRetrievalListener(view, this));
        }
        /*
         * TODO
         * -- Display an error if the user is unexpectedly logged out
         * -- Return the user to the login page
         */
        return view;
    }

}

/**
 * After retrieving a user's list of connections, this listener will fire and start retrieving the
 * user information for these connections and fire a load listener afterwards.
 */
class UserListRetrievalListener implements ValueEventListener {

    private final ListView collectionList;
    private final Context context;

    UserListRetrievalListener(final View view, final Fragment fragment) {
        this.collectionList = (ListView) view.findViewById(R.id.collectionList);
        this.context = fragment.getContext();
    }

    private String getString(int stringId) {
        return context.getString(stringId);
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final AtomicInteger syncer = new AtomicInteger((int) dataSnapshot.getChildrenCount());
        final List<User> users = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.database_users));
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            final String uid = snapshot.getValue(String.class);
            ref.child(uid).addListenerForSingleValueEvent(
                    new OnUserInfoRetrievedListener(collectionList, context, syncer, users));
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        /*
         * TODO
         * -- If loading the connections list fails, Slip should
         *    -> Display a weak message on the list saying that Slip could not load contacts
         *    -> Prompt the user to refresh the list by swiping down
         */
    }
}

/**
 * A listener that fires once a user information query completes or fails.
 */
class OnUserInfoRetrievedListener implements ValueEventListener {

    private final ListView collectionList;
    private final Context context;
    private final AtomicInteger syncer;
    private final List<User> users;

    OnUserInfoRetrievedListener(
            final ListView collectionList,
            final Context context,
            final AtomicInteger syncer,
            final List<User> users) {
        this.collectionList = collectionList;
        this.context = context;
        this.syncer = syncer;
        this.users = users;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        users.add(user);
        sync();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        /*
         * TODO
         * -- Should a particular user fail to load, Slip should append that user to the end
         *    of a buffer stack and attempt to reload that user later
         */
        sync();
    }

    private void sync() {
        if (syncer.decrementAndGet() == 0) {
            CollectionListAdapter collectionAdapter = new CollectionListAdapter(
                    context, users,
                    R.layout.collection_card_preview,
                    R.id.card_description);
            collectionList.setAdapter(collectionAdapter);
            collectionList.setOnItemClickListener(new OnUserClickedListesner());
        }
    }
}

class OnUserClickedListesner implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * TODO
         * -- Take the user to the connection's profile page upon tapping his card.
         */
    }
}