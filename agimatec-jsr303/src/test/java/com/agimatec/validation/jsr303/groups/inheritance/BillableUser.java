package com.agimatec.validation.jsr303.groups.inheritance;

import com.agimatec.validation.jsr303.groups.Billable;
import com.agimatec.validation.jsr303.groups.BillableCreditCard;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 05.10.2009 <br/>
 * Time: 12:18:31 <br/>
 * Copyright: Agimatec GmbH
 */
public class BillableUser {
    @NotNull
    private String firstname;

    @NotNull(groups = Default.class)
    private String lastname;

    @NotNull(groups = {Billable.class})
    private BillableCreditCard defaultCreditCard;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public BillableCreditCard getDefaultCreditCard() {
        return defaultCreditCard;
    }

    public void setDefaultCreditCard(BillableCreditCard defaultCreditCard) {
        this.defaultCreditCard = defaultCreditCard;
    }
}