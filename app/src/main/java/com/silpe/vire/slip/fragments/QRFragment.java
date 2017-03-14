package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.R;

import java.util.concurrent.atomic.AtomicInteger;

public class QRFragment extends Fragment {
    private static final String UID = "mUidView";

    private String mUid;
    private EditText mUidTextField;

    public QRFragment() {
    }

    public static QRFragment newInstance(String uid) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putString(UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString(UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        TextView uidText = (TextView) view.findViewById(R.id.camera_uid);
        uidText.setText(mUid);

        Button addButton = (Button) view.findViewById(R.id.camera_addUser_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddUser();
            }
        });

        mUidTextField = (EditText) view.findViewById(R.id.camera_inputField);
        return view;
    }

    private void doAddUser() {
        final String uid = mUidTextField.getText().toString();
        if (mUid.equals(uid)) {
            mUidTextField.setError(getContext().getString(R.string.error_add_self));
            mUidTextField.requestFocus();
        } else if (TextUtils.isEmpty(uid)) {
            mUidTextField.setError(getContext().getString(R.string.error_field_required));
            mUidTextField.requestFocus();
        } else {
            final AtomicInteger counter = new AtomicInteger(2);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getContext().getString(R.string.database_users))
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (counter.decrementAndGet() == 0) addUid(uid);
                            } else {
                                mUidTextField.setError(getContext().getString(R.string.error_invalid_uid));
                                mUidTextField.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mUidTextField.setError(getContext().getString(R.string.error_database));
                            mUidTextField.requestFocus();
                        }
                    });
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getContext().getString(R.string.database_connections))
                    .child(mUid)
                    .orderByValue()
                    .equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                mUidTextField.setError(getContext().getString(R.string.error_user_added));
                                mUidTextField.requestFocus();
                            } else {
                                if (counter.decrementAndGet() == 0) addUid(uid);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mUidTextField.setError(getContext().getString(R.string.error_database));
                            mUidTextField.requestFocus();
                        }
                    });
        }
    }

    private void addUid(String uid) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getContext().getString(R.string.database_connections))
                .child(mUid)
                .push()
                .setValue(uid);
        Toast.makeText(getContext(), R.string.addUser_success, Toast.LENGTH_SHORT);
    }

}
