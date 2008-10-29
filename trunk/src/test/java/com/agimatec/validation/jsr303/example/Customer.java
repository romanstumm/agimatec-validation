package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.Email;
import com.agimatec.validation.constraints.NotNull;
import com.agimatec.validation.constraints.Password;

public class Customer implements Person {
    private String firstName;
    private String middleName;
    private String lastName;
    @NotNull
    private String customerId;
    @Password(robustness = 5)
    private String password;

    @Email
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}