package com.silpe.vire.slip.components;

import android.content.Context;
import android.widget.TextView;

public interface LocationDisplayComponent {

    TextView getCityView();

    TextView getLocationView();

    Context getContext();

}