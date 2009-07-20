package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Pattern;

public class Engine {
    @Pattern.List({
        @Pattern(regexp = "^[A-Z0-9-]+$", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "must contain alphabetical characters only"),
        @Pattern(
                regexp = "^....-....-....$", message = "must match ....-....-....")})
    public String serialNumber;


}