package com.agimatec.utility.validation.xml;

import static com.agimatec.utility.validation.model.Features.Property.*;
import com.agimatec.utility.validation.model.MetaProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:27:30 <br/>
 * Copyright: Agimatec GmbH 2008
 */
@XStreamAlias("property")
public class XMLMetaProperty extends XMLMetaElement {

    @XStreamAsAttribute()
    private Number maxValue;
    @XStreamAsAttribute()
    private Number minValue;

    private String regexp;

    @XStreamAsAttribute()
    private String timeLag;


    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getTimeLag() {
        return timeLag;
    }

    public void setTimeLag(String timeLag) {
        this.timeLag = timeLag;
    }

    @Override
    public void mergeInto(MetaProperty prop) throws ClassNotFoundException {
        super.mergeInto(prop);   // call super!
        if (getMaxValue() != null) {
            prop.putFeature(MAX_VALUE, getMaxValue());
        }
        if (getMinValue() != null) {
            prop.putFeature(MIN_VALUE, getMinValue());
        }
        if (getRegexp() != null) {
            prop.putFeature(REG_EXP, getRegexp());
        }
        if (getTimeLag() != null) {
            prop.putFeature(TIME_LAG, getTimeLag());
        }
    }
}
