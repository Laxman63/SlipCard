package com.silpe.vire.slip.adapters;

import com.google.firebase.storage.StorageReference;

public class CollectionListItem {

    final String uid;
    final String fullName;
    final String description;
    final StorageReference pictureRef;

    public CollectionListItem(String uid, String fullName, String description, StorageReference pictureRef) {
        this.uid = uid;
        this.fullName = fullName;
        this.description = description;
        this.pictureRef = pictureRef;
    }

}
