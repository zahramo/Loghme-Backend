package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.*;

import java.util.ArrayList;

public class UserInfo {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private int credit;

    public int getCredit() {
        return credit;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserInfo(User user){
        this.credit = user.getCredit();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
    }
}
