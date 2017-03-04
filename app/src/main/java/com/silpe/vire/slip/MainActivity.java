package com.silpe.vire.slip;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabHost host = (TabHost)findViewById(R.id.tabHost);

        setSupportActionBar(toolbar);
        final String[] tabs = {
                "home", "show", "yolo"
        };



        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(tabs[0]);
        spec.setContent(R.id.tab1);
        spec.setIndicator(tabs[0]);
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec(tabs[1]);
        spec.setContent(R.id.tab2);
        spec.setIndicator(tabs[1]);
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec(tabs[2]);
        spec.setContent(R.id.tab3);
        spec.setIndicator(tabs[2]);
        host.addTab(spec);


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
