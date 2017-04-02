package com.silpe.vire.slip.collection;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.AccountActivity;

public class CollectionActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        User user = getIntent().getParcelableExtra(AccountActivity.RESULT_USER);
        setContentView(R.layout.activity_collection);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(String.format("%s's Collection", user.getFullName()));
            actionBar.setSubtitle("");
        }
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            CollectionView collectionView = (CollectionView) findViewById(R.id.collectionList);
            collectionView.setAdapter(new CollectionAdapter(this));
            collectionView.setLayoutManager(new CollectionLayoutManager(this));
            ref = ref.child(getString(R.string.database_connections)).child(user.getUid());
            ref.addChildEventListener(new UserListEventListener(collectionView));
            ref.addListenerForSingleValueEvent(new UserListRetrievalListener(collectionView));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
