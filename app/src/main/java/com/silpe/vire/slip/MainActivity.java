package com.silpe.vire.slip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Navigation toolbar which will transition between screens
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        setSupportActionBar(toolbar);
        final String[] tabs = {
                "home", "show", "yolo"
        };
        tabHost.setup();
        // Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec(tabs[0]);
        spec.setContent(R.id.tab1);
        spec.setIndicator(tabs[0]);
        tabHost.addTab(spec);
        // Tab 2
        spec = tabHost.newTabSpec(tabs[1]);
        spec.setContent(R.id.tab2);
        spec.setIndicator(tabs[1]);
        tabHost.addTab(spec);
        // Tab 3
        spec = tabHost.newTabSpec(tabs[2]);
        spec.setContent(R.id.tab3);
        spec.setIndicator(tabs[2]);
        tabHost.addTab(spec);

        /**
         * List view to display a user's card collection
         */
        final ListView collectionList = (ListView) findViewById(R.id.collectionList);
        final List<String> values = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
            values.add("xd" + i);
        }
        final CollectionListAdapter collectionAdapter =
                new CollectionListAdapter(this, R.layout.card_collection_listitem, R.id.secondLine,values);
        collectionList.setAdapter(collectionAdapter);
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                final ViewPropertyAnimator viewPropertyAnimator = view.animate().setDuration(1000).alpha(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            values.remove(item);
                            collectionAdapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                } else {
                    viewPropertyAnimator.withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            values.remove(item);
                            collectionAdapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                }
            }
        });

        /**
         * Floating action button which will open up the QR code scanner
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
