package com.silpe.vire.slip.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import com.silpe.vire.slip.R;

public class DoubleBackHandler {

    private Activity activity;
    private boolean doubleTapped;

    public DoubleBackHandler(Activity activity) {
        this.activity = activity;
        doubleTapped = false;
    }

    public void onBackPressed() {
        if (doubleTapped) {
            doExit();
            return;
        }
        doubleTapped = true;
        Toast.makeText(activity, activity.getString(R.string.double_back_prompt), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new ResetDoubleTap(), activity.getResources().getInteger(R.integer.double_tap_time));
    }

    private void doExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.finishAffinity();
        } else {
            Intent intent = new Intent(activity, activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private class ResetDoubleTap implements Runnable {
        @Override
        public void run() {
            doubleTapped = false;
        }
    }

}