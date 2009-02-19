package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Patterns;

public class Engine {
    @Patterns({
        @Pattern(regexp = "^[A-Z0-9-]+$", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "must contain alphabetical characters only"),
        @Pattern(
                regexp = "^....-....-....$", message = "must match ....-....-....")})
    public String serialNumber;


}