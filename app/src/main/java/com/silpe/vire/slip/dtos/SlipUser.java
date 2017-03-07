package com.silpe.vire.slip.dtos;

public class SlipUser {

    public String uid;
    public String email;
    public String firstName;
    public String lastName;
    public String occupation;
    public String company;

    public long signature;

    public SlipUser() {
    }

    public SlipUser(String uid, String email, String firstName, String lastName, String occupation, String company, long signature) {
        this();
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
        this.company = company;
        this.signature = signature;
    }

}
