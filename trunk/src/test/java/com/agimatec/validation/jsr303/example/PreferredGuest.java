package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Digits;

public class PreferredGuest extends Customer {
    @Digits(integer = 10, fraction = 0)
    private String guestCreditCardNumber;

    public String getGuestCreditCardNumber() {
        return guestCreditCardNumber;
    }

    public void setGuestCreditCardNumber(String guestCreditCardNumber) {
        this.guestCreditCardNumber = guestCreditCardNumber;
    }
}