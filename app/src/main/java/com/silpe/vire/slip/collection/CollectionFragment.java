package com.silpe.vire.slip.collection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.models.SessionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectionFragment extends Fragment {


    public static CollectionFragment newInstance(FragmentManager fragmentManager) {
        CollectionFragment fragment = new CollectionFragment();
        fragment.fragmentManager = fragmentManager;
        return fragment;
    }

    private FragmentManager fragmentManager;

    public CollectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final User user = SessionModel.get().getUser(getContext());
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            CollectionView collectionView = (CollectionView) view.findViewById(R.id.collectionList);
            collectionView.setAdapter(new CollectionAdapter(fragmentManager));
            collectionView.setLayoutManager(new CollectionLayoutManager(getContext()));
            ref = ref.child(getString(R.string.database_connections)).child(user.getUid());
            ref.addChildEventListener(new UserListEventListener(collectionView));
            ref.addListenerForSingleValueEvent(new UserListRetrievalListener(collectionView));
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

    private final CollectionView collectionView;

    UserListRetrievalListener(final CollectionView collectionView) {
        this.collectionView = collectionView;
    }

    private String getString(int stringId) {
        return collectionView.getContext().getString(stringId);
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
                    new UserInfoRetrievalListener(collectionView, syncer, users));
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
class UserInfoRetrievalListener implements ValueEventListener {

    private final CollectionView collectionView;
    private final AtomicInteger syncer;
    private final List<User> users;

    UserInfoRetrievalListener(
            final CollectionView collectionView,
            final AtomicInteger syncer,
            final List<User> users) {
        this.collectionView = collectionView;
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
            collectionView.update(users);
        }
    }

}

class UserListEventListener implements ChildEventListener {

    private final CollectionView collectionView;
    private final DatabaseReference reference;

    UserListEventListener(final CollectionView collectionView) {
        this.collectionView = collectionView;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    private static abstract class BaseChildListener implements ValueEventListener {
        @Override
        public void onCancelled(DatabaseError error) {
            /*
             * TODO
             * -- Analyze the nature of the error and
             *    -> Handle it appropriately
             *    -> Handle it gracefully or
             *    -> Display an error message
             */
        }
    }

    /**
     * Current user has added another connection. This method will fire upon adding a new connection
     * and will trigger the {@code CollectionView} to update with the new connection.
     *
     * @param snapshot the snapshot containing the new connection
     * @param s        the key of the previous ordered child
     */
    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        String uid = snapshot.getValue(String.class);
        reference.child(getString(R.string.database_users)).child(uid)
                .addValueEventListener(new OnChildAddedListener());
    }

    private class OnChildAddedListener extends BaseChildListener {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            /*
             * TODO
             * -- Ensure that when inserting new children, chronological order is followed
             *    -> Sorted by most recently added
             */
            collectionView.update(user);
        }
    }

    /**
     * Since the list of a user's connections should only have elements added to it or removed
     * from it, this method should never be called. This is because the list of a user's
     * connections contains only the UIDs of his connections. The UIDs will never change, and
     * will simply be added or removed.
     *
     * @param snapshot the snapshot of the changed item
     * @param s        the key of the changed item
     */
    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        String uid = snapshot.getValue(String.class);
        collectionView.remove(uid);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private String getString(int stringId) {
        return collectionView.getContext().getString(stringId);
    }

}