package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.ZipCodeCityCoherence;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ZipCodeCityCoherence
public class Address {
    @NotNull
    @Size(max = 30)
    private String addressline1;
    @Size(max = 30)
    private String addressline2;
    @Size(max = 11)
    private String zipCode;
    @NotNull
    @Valid
    private Country country;
    private String city;

    public String getAddressline1() {
        return addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Size(max = 30)
    @NotNull
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Check conherence on the overall object
     * Needs basic checking to be green first
     */
    public interface HighLevelCoherence {
    }

    /**
     * Check both basic constraints and high level ones.
     * High level constraints are not checked if basic constraints fail.
     */
    @GroupSequence(value = {Address.class, HighLevelCoherence.class})
    public interface Complete {
    }
}