package com.silpe.vire.slip.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.PickerBuilder;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

import java.io.File;

public class ShowFragment extends Fragment {

    public ShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        final User user = SessionModel.get().getUser(getContext());
        ((TextView) view.findViewById(R.id.show_firstName)).setText(user.getFirstName());
        ((TextView) view.findViewById(R.id.show_lastName)).setText(user.getLastName());
        ((TextView) view.findViewById(R.id.show_occupation)).setText(user.getOccupation());
        ((TextView) view.findViewById(R.id.show_company)).setText(user.getCompany());
        ((TextView) view.findViewById(R.id.show_email)).setText(user.getEmail());
        ((TextView) view.findViewById(R.id.show_uid)).setText(user.getUid());

        final ImageView imageView = (ImageView) view.findViewById(R.id.show_profile_picture);
        if (user.getSignature() > 0) {
            StorageReference sRef = FirebaseStorage.getInstance().getReference();
            sRef = sRef.child(getString(R.string.database_users)).child(user.getUid()).child(getString(R.string.database_profile_picture));
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(sRef)
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PickerBuilder(ShowFragment.this.getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                        .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                            @Override
                            public void onImageReceived(Uri imageUri) {
                                StorageReference sRef = FirebaseStorage.getInstance().getReference();
                                sRef = sRef.child(getString(R.string.database_users)).child(user.getUid()).child(getString(R.string.database_profile_picture));
                                UploadTask uploadTask = sRef.putFile(imageUri);
                                TimestampSignature signature = new TimestampSignature();
                                user.setSignature(signature.getSignature());
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                dbRef = dbRef.child(getString(R.string.database_users)).child(user.getUid());
                                dbRef.setValue(user);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    }
                                });
                                Glide.with(ShowFragment.this.getContext())
                                        .load(new File(imageUri.getPath()))
                                        .signature(signature)
                                        .into(imageView);
                            }
                        })
                        .setImageName("profile_picture")
                        .setImageFolderName("slipPics")
                        .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                            @Override
                            public void onPermissionRefused() {

                            }
                        })
                        .start();
            }
        });
        return view;
    }

}
