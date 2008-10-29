package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.NotNull;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:47:15 <br/>
 * Copyright: Agimatec GmbH
 */
public class BusinessAddress extends Address {
    private String company;

    @NotNull
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
