package com.silpe.vire.slip.dtos;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.silpe.vire.slip.models.Persistent;

import java.util.Locale;

public class User implements Persistent<User> {

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



    @Exclude
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @Exclude
    public String getDescription() {
        return String.format("%s @ %s", getOccupation(), getCompany());
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

        //Oops, not every user have 8 fields
        //Todo find a way to decode with any number of fields given now only support 8
        /*for (int i = 0 ; i <kv.length; i ++) {
            Log.d("String", i+": "+kv[i]);
        }*/
        if (kv.length ==7){
            return new User(kv[0], kv[1],""+911, kv[2], kv[3], kv[4], kv[5], Long.valueOf(kv[6]));
        }

        return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], kv[6], Long.valueOf(kv[7]));
    }


}
