package com.silpe.vire.slip.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.SlipUser;
import com.silpe.vire.slip.image.PickerBuilder;
import com.silpe.vire.slip.models.SessionModel;

public class ShowFragment extends Fragment {

    public ShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SlipUser user = SessionModel.get().getUser();
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ((TextView) view.findViewById(R.id.show_firstName)).setText(user.firstName);
        ((TextView) view.findViewById(R.id.show_lastName)).setText(user.lastName);
        ((TextView) view.findViewById(R.id.show_occupation)).setText(user.occupation);
        ((TextView) view.findViewById(R.id.show_company)).setText(user.company);
        ((TextView) view.findViewById(R.id.show_email)).setText(user.email);
        ((TextView) view.findViewById(R.id.show_uid)).setText(user.uid);
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        ref = ref.child(getString(R.string.database_users)).child(user.uid).child(getString(R.string.database_profile_picture));
        ImageView imageView = (ImageView) view.findViewById(R.id.show_profile_picture);
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(ref)
                .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null)).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PickerBuilder(ShowFragment.this.getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                        .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                            @Override
                            public void onImageReceived(Uri imageUri) {
                                Toast.makeText(ShowFragment.this.getActivity(),"Got image - " + imageUri,Toast.LENGTH_LONG).show();
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
