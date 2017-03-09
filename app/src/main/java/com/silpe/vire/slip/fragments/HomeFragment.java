package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.adapters.CollectionListAdapter;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.adapters.CollectionListItem;
import com.silpe.vire.slip.dtos.SlipUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class  HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final ListView collectionList = (ListView) view.findViewById(R.id.collectionList);
        if (user != null) {
            ref = ref.child(getString(R.string.database_connections)).child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<CollectionListItem> items = new ArrayList<>();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.database_users));
                    final AtomicInteger[] c = {new AtomicInteger((int) dataSnapshot.getChildrenCount())};
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uid = snapshot.getValue(String.class);
                        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                SlipUser user = dataSnapshot.getValue(SlipUser.class);
                                items.add(new CollectionListItem(user.uid,
                                        String.format("%s %s", user.firstName, user.lastName),
                                        String.format("%s @ %s", user.occupation, user.company),
                                        null));
                                if (c[0].decrementAndGet() == 0) {
                                    CollectionListAdapter collectionAdapter = new CollectionListAdapter(
                                            HomeFragment.this.getContext(), R.layout.collection_card_preview, R.id.card_description, items);
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
            });
        }
        return view;
    }

}
