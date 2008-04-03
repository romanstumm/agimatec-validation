package javax.validation;

public interface Person {
    @NotEmpty
    String getFirstName();

    String getMiddleName();

    @NotEmpty
    String getLastName();
}