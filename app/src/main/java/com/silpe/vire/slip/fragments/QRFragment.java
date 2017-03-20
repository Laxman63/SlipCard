package com.silpe.vire.slip.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.silpe.vire.slip.R;
import com.silpe.vire.slip.encode.BarcodeFormat;
import com.silpe.vire.slip.encode.BitMatrix;
import com.silpe.vire.slip.encode.EncodeHintType;
import com.silpe.vire.slip.encode.ErrorCorrectionLevel;
import com.silpe.vire.slip.encode.QRCodeWriter;
import com.silpe.vire.slip.encode.WriterException;

import java.util.Hashtable;

public class QRFragment extends Fragment {
    private static final String UID = "mUidView";

    private String mUid;
    //private EditText mUidTextField;
    private Bitmap qrCode;

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
            try {
                qrCode = encodeQR(mUid);
            } catch (WriterException e) {
                qrCode = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        if (qrCode != null) {
            ImageView qrView = (ImageView) view.findViewById(R.id.qr_code);
            qrView.setImageBitmap(qrCode);
        }
        /*TextView uidText = (TextView) view.findViewById(R.id.camera_uid);
        uidText.setText(mUid);

        Button addButton = (Button) view.findViewById(R.id.camera_addUser_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddUser();
            }
        });

        mUidTextField = (EditText) view.findViewById(R.id.camera_inputField);*/
        return view;
    }

    private static Bitmap encodeQR(String val) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 256;
        BitMatrix matrix = qrCodeWriter.encode(val, BarcodeFormat.QR_CODE, size, size, hintMap);
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    /*
    private void doAddUser() {
        final String uid = mUidTextField.getText().toString();
        if (mUid.equals(uid)) {
            mUidTextField.setError(getContext().getString(R.string.error_add_self));
        } else if (TextUtils.isEmpty(uid)) {
            mUidTextField.setError(getContext().getString(R.string.error_field_required));
        } else {
            final AtomicInteger counter = new AtomicInteger(2);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getContext().getString(R.string.database_users))
                    .child(uid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && counter.decrementAndGet() == 0) {
                                addUid(uid, reference);
                            } else {
                                mUidTextField.setError(getContext().getString(R.string.error_invalid_uid));
                            }
                        }
                    });
            reference.child(getContext().getString(R.string.database_connections))
                    .child(mUid)
                    .orderByValue()
                    .equalTo(uid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                mUidTextField.setError(getContext().getString(R.string.error_user_added));
                            } else if (counter.decrementAndGet() == 0) {
                                addUid(uid, reference);
                            }
                        }
                    });
        }
    }

    private void addUid(String uid, DatabaseReference reference) {
        reference.child(getContext().getString(R.string.database_connections))
                .child(mUid)
                .push()
                .setValue(uid);
        Toast.makeText(getContext(), R.string.addUser_success, Toast.LENGTH_SHORT).show();
    }

    private abstract class UserQueryListener implements ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            mUidTextField.setError(getContext().getString(R.string.error_database));
        }
    }*/

}