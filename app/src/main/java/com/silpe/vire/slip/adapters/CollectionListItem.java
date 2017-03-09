package com.silpe.vire.slip.adapters;

import android.view.View;
import android.widget.TextView;

import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.Icon;

public class CollectionListItem {

    private View row;
    private Icon icon;
    private TextView firstLine;
    private TextView secondLine;

    CollectionListItem(View row) {
        this.row = row;
    }

    TextView getNameText() {
        if (this.firstLine == null) {
            this.firstLine = (TextView) row.findViewById(R.id.card_name);
        }
        return this.firstLine;
    }

    TextView getDescriptionText() {
        if (this.secondLine == null) {
            this.secondLine = (TextView) row.findViewById(R.id.card_description);
        }
        return this.secondLine;
    }

    Icon getProfilePicture() {
        if (this.icon == null) {
            this.icon = (Icon) row.findViewById(R.id.card_picture);
        }
        return this.icon;
    }

}
