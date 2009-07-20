package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

public class Author {
    @NotEmpty(groups = Last.class)
    private String firstName;
    @NotEmpty(groups = First.class)
    private String lastName;
    @Size(max = 40, groups = First.class)
    private String company;

    @Valid
    private List<Address> addresses;

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}