package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.Length;
import com.agimatec.validation.constraints.NotNull;

public class Country {
    @NotNull
    private String name;
    @Length(max = 2)
    private String ISO2Code;
    @Length(max = 3)
    private String ISO3Code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getISO2Code() {
        return ISO2Code;
    }

    public void setISO2Code(String ISO2Code) {
        this.ISO2Code = ISO2Code;
    }

    public String getISO3Code() {
        return ISO3Code;
    }

    public void setISO3Code(String ISO3Code) {
        this.ISO3Code = ISO3Code;
    }
}