package com.silpe.vire.slip.dtos;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

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

    private Integer connections;
    private Long signature;
    @Nullable
    private Double latitude;
    @Nullable
    private Double longitude;

    public User() {
        uid = "";
        email = "";
        phoneNumber = "";
        firstName = "";
        lastName = "";
        occupation = "";
        company = "";
        connections = 0;
        signature = 0L;
        latitude = null;
        longitude = null;
    }

    public User(String uid) {
        this();
        this.uid = uid;
    }

    public User(String uid, String email, String phoneNumber,
                String firstName, String lastName,
                String occupation, String company) {
        this(uid, email, phoneNumber, firstName, lastName, occupation, company,
                0, 0, null, null);
    }

    public User(String uid, String email, String phoneNumber,
                String firstName, String lastName,
                String occupation, String company, int connections, long signature,
                @Nullable Double latitude, @Nullable Double longitude) {
        this();
        setUid(uid);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setFirstName(firstName);
        setLastName(lastName);
        setOccupation(occupation);
        setCompany(company);
        setConnections(connections);
        setSignature(signature);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        occupation = in.readString();
        company = in.readString();
        connections = in.readInt();
        signature = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
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

    @Exclude
    public DatabaseReference getConnectionsReference(Context context) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(context.getString(R.string.database_connections))
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

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public Long getSignature() {
        return signature;
    }

    public void setSignature(Long signature) {
        this.signature = signature;
    }

    @Nullable
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nullable Double longitude) {
        this.longitude = longitude;
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
        return String.format(Locale.US, "%s&%s&%s&%s&%s&%s&%s&%d&%d",
                uid, email, phoneNumber, firstName, lastName, occupation, company,
                connections, signature) +
                '&' + (latitude == null ? NULL : latitude) +
                '&' + (longitude == null ? NULL : longitude);
    }

    @Override
    public User decode(String serial) {
        final String[] kv = serial.split("&");
        if (kv.length == 8) {
            // uid, email, phoneNumber, firstName, lastName, occupation, company, signature
            return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], kv[6],
                    0, Long.parseLong(kv[7]), null, null);
        } else if (kv.length == 9) {
            return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], kv[6],
                    Integer.parseInt(kv[7]), Long.parseLong(kv[8]), null, null);
        } else {
            Double latitude = NULL.equals(kv[9]) ? null : Double.parseDouble(kv[9]);
            Double longitude = NULL.equals(kv[10]) ? null : Double.parseDouble(kv[10]);
            return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], kv[6],
                    Integer.parseInt(kv[7]), Long.parseLong(kv[8]),
                    latitude, longitude);
        }
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
        dest.writeInt(connections);
        dest.writeLong(signature);
        if (latitude != null) dest.writeDouble(latitude);
        if (longitude != null) dest.writeDouble(longitude);
    }
}
