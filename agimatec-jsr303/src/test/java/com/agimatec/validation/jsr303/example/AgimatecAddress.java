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
    private String email;

    public AgimatecAddress() {
    }

    public AgimatecAddress(String email) {
        this.email = email;
    }

    // do not provided getters & setters to test that value access
    // of combined constraints directly use the private field 'email'
}
