package com.silpe.vire.slip.dtos;

import com.silpe.vire.slip.models.Persistent;

import java.util.Locale;

public class User implements Persistent<User> {

    private String uid;

    private String email;
    private String firstName;
    private String lastName;
    private String occupation;
    private String company;

    private long signature;

    public User() {

    }

    public User(String uid, String email, String firstName, String lastName, String occupation, String company, long signature) {
        this();
        setUid(uid);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setOccupation(occupation);
        setCompany(company);
        setSignature(signature);
    }

    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

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

    private void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    private void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCompany() {
        return company;
    }

    private void setCompany(String company) {
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
        return String.format(Locale.US, "%s&%s&%s&%s&%s&%s&%d",
                uid, email, firstName, lastName, occupation, company, signature);
    }

    @Override
    public User decode(String serial) {
        final String[] kv = serial.split("&");
        return new User(kv[0], kv[1], kv[2], kv[3], kv[4], kv[5], Long.valueOf(kv[6]));
    }


}
