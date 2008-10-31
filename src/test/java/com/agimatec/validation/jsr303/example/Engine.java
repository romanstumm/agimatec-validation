package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.Pattern;
import com.agimatec.validation.constraints.Patterns;

public class Engine {
    @Patterns({
        @Pattern(regex = "^[A-Z0-9-]+$",
                message = "must contain alphabetical characters only"),
        @Pattern(
                regex = "^....-....-....$", message = "must match ....-....-....")})
    public String serialNumber;


}