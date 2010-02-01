package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.FeaturesCapable;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 12:29:52 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidationCollectorFeature implements ValidationCollector {
    private final FeaturesCapable feature;

    public ValidationCollectorFeature(FeaturesCapable meta) {
        this.feature = meta;
    }

    public void addValidation(ConstraintValidation validation) {
        feature.addValidation(validation);
    }
}
