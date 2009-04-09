package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.NotEmpty;

public interface Person {
    @NotEmpty
    String getFirstName();

    String getMiddleName();

    @NotEmpty
    String getLastName();
}