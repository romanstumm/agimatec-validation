package com.agimatec.utility.validation.example;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 09:13:50 <br/>
 *
 */
public class BusinessObjectAddress {
    private String city, country;
    private BusinessObject owner;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BusinessObject getOwner() {
        return owner;
    }

    public void setOwner(BusinessObject owner) {
        this.owner = owner;
    }
}
