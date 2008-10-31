package javax.validation;

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