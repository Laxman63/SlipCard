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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.adapters.CollectionListAdapter;
import com.silpe.vire.slip.adapters.CollectionListItem;
import com.silpe.vire.slip.dtos.SlipUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            ref = ref.child(getString(R.string.database_connections)).child(user.getUid());
            ref.addListenerForSingleValueEvent(new UserListRetrievalListener(view, this));
        }
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
        final List<CollectionListItem> items = new ArrayList<>();
        final AtomicInteger[] c = {new AtomicInteger((int) dataSnapshot.getChildrenCount())};
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.database_users));

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String uid = snapshot.getValue(String.class);
            ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SlipUser user = dataSnapshot.getValue(SlipUser.class);
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    ref = ref.child(getString(R.string.database_users)).child(user.uid).child(getString(R.string.database_profile_picture));
                    items.add(new CollectionListItem(user.uid,
                            String.format("%s %s", user.firstName, user.lastName),
                            String.format("%s @ %s", user.occupation, user.company),
                            ref));
                    if (c[0].decrementAndGet() == 0) {
                        CollectionListAdapter collectionAdapter = new CollectionListAdapter(
                                context, R.layout.collection_card_preview, R.id.card_description, items);
                        collectionList.setAdapter(collectionAdapter);
                        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                // TODO go to the profile
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}