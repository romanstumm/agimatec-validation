package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.FrenchZipCode;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:47:35 <br/>
 * Copyright: Agimatec GmbH
 */
public class FrenchAddress {
    @FrenchZipCode(size = 7)
    String zipCode;

    public FrenchAddress() {
    }

    public FrenchAddress(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
