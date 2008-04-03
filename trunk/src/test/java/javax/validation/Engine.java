package javax.validation;

public class Engine {
    @Patterns({
    @Pattern(regex = "^[A-Z0-9-]+$",
            message = "must contain alphabetical characters only"),
    @Pattern(
            regex = "^....-....-....$", message = "must match ....-....-....")})
    public String serialNumber;


}