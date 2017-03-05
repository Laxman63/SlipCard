package com.silpe.vire.slip.dtos;

public class SlipUser {

    public String uid;
    public String email;
    public String firstName;
    public String lastName;
    public String occupation;
    public String company;

    public SlipUser() {
    }

    public SlipUser(String uid, String email, String firstName, String lastName, String occupation, String company) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
        this.company = company;
    }

}
