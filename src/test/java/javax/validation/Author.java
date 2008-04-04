package javax.validation;

import java.util.List;

public class Author {
    @NotEmpty(groups = "last")
    private String firstName;
    @NotEmpty(groups = "first")
    private String lastName;
    @Length(max = 30, groups = "last")
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