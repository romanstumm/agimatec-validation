package com.agimatec.validation.jsr303.example;

/**
 * Description: bean used to test constraints described in XML<br/>
 * User: roman <br/>
 * Date: 25.11.2009 <br/>
 * Time: 09:00:45 <br/>
 * Copyright: Agimatec GmbH
 */
public class XmlEntitySampleBean {
    private String zipCode;
    private String valueCode;

    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }
}
