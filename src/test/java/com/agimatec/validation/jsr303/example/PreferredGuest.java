package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.CreditCard;

public class PreferredGuest extends Customer {
    @CreditCard
    private String guestCreditCardNumber;

    public String getGuestCreditCardNumber() {
        return guestCreditCardNumber;
    }

    public void setGuestCreditCardNumber(String guestCreditCardNumber) {
        this.guestCreditCardNumber = guestCreditCardNumber;
    }
}