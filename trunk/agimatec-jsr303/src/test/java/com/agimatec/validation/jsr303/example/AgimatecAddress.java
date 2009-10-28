package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.AgimatecEmail;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.10.2009 <br/>
 * Time: 11:58:37 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecAddress {
    @AgimatecEmail
    String zipCode;

    public AgimatecAddress() {
    }

    public AgimatecAddress(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
