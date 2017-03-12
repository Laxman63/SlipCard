package com.silpe.vire.slip.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    TextView mFirstNameView, mLastNameView, mOccupationView, mCompanyView, mEmailView, mPhoneNumberView, mUidView;

    public ShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        final SessionModel session = SessionModel.get();
        final User user = session.getUser(getContext());

        mFirstNameView = ((TextView) view.findViewById(R.id.show_firstName));
        mLastNameView = ((TextView) view.findViewById(R.id.show_lastName));
        mOccupationView = ((TextView) view.findViewById(R.id.show_occupation));
        mCompanyView = ((TextView) view.findViewById(R.id.show_company));
        mEmailView = ((TextView) view.findViewById(R.id.show_email));
        mPhoneNumberView = ((TextView) view.findViewById(R.id.show_phone));
        mUidView = ((TextView) view.findViewById(R.id.show_uid));

        mFirstNameView.setText(user.getFirstName());
        mLastNameView.setText(user.getLastName());
        mOccupationView.setText(user.getOccupation());
        mCompanyView.setText(user.getCompany());
        mEmailView.setText(user.getEmail());
        mUidView.setText(user.getUid());

        String phoneNumber = user.getPhoneNumber();
        mPhoneNumberView.setText(phoneNumber.isEmpty() ? "+ Add a phone number" : phoneNumber);


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
        } else {
            imageView.setImageResource(R.drawable.empty_profile);
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
