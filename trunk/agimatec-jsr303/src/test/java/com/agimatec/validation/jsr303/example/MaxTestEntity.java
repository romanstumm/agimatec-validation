package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Max;
import java.math.BigDecimal;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 17.11.2009 <br/>
 * Time: 15:21:45 <br/>
 * Copyright: Agimatec GmbH
 */
public class MaxTestEntity {
    @Max(100)
    private String text;
    private String property;

    @Max(300)
    private long longValue;

    private BigDecimal decimalValue;

    public String getText() {
        return text;
    }

    @Max(200)
    public String getProperty() {
        return property;
    }

    public long getLongValue() {
        return longValue;
    }

    @Max(400)
    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }
}
