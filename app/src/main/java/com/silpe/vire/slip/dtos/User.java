package com.silpe.vire.slip.dtos;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.models.Persistent;

import java.util.Locale;

public class User implements Persistent<User>, Parcelable {

    private String uid;

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String occupation;
    private String company;

    private long signature;

    public User() {
        uid = "";
        email = "";
        phoneNumber = "";
        firstName = "";
        lastName = "";
        occupation = "";
        company = "";
        signature = 0;
    }

    public User(String uid) {
        this();
        this.uid = uid;
    }

    public User(String uid, String email, String phoneNumber,
                String firstName, String lastName,
                String occupation, String company, long signature) {
        this();
        setUid(uid);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setFirstName(firstName);
        setLastName(lastName);
        setOccupation(occupation);
        setCompany(company);
        setSignature(signature);
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        occupation = in.readString();
        company = in.readString();
        signature = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Exclude
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @Exclude
    public String getDescription() {
        return String.format("%s @ %s", getOccupation(), getCompany());
    }

    @Exclude
    public StorageReference getProfilePictureReference(Context context) {
        return FirebaseStorage.getInstance()
                .getReference()
                .child(context.getString(R.string.database_users))
                .child(getUid())
                .child(context.getString(R.string.database_profilePicture));
    }

    @Exclude
    public DatabaseReference getDatabaseReference(Context context) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(context.getString(R.string.database_users))
                .child(getUid());
    }

    public String getUid() {
        return uid;
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getSignature() {
        return signature;
    }

    public void setSignature(long signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid.equals(user.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String encode() {
        return String.format(Locale.US, "%s&%s&%s&%s&%s&%s&%s&%d",
                uid, email, phoneNumber, firstName, lastName, occupation, company, signature);
    }

    @Override
    public User decode(String serial) {
        final String[] kv = serial.split("&");

        return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], kv[6], Long.valueOf(kv[7]));
    }

    @Override
    public String toString() {
        return String.format("%s - %s, %s", uid, firstName, lastName);
    }

    @Override
    public int describeContents() {
        return Parcelable.PARCELABLE_WRITE_RETURN_VALUE;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(occupation);
        dest.writeString(company);
        dest.writeLong(signature);
    }
}
