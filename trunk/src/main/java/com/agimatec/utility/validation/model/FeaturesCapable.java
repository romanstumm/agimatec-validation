package com.agimatec.utility.validation.model;

import org.apache.commons.collections.FastHashMap;

import java.io.Serializable;
import java.util.Map;

import com.agimatec.utility.validation.Validation;

/**
 * Description: abstract superclass of meta objects that support a map of features.<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 10:29:57 <br/>
 *
 */
public abstract class FeaturesCapable implements Serializable {
    private FastHashMap features = new FastHashMap();
    /** key = validation id, value = the validation */
    private Validation[] validations = new Validation[0];

    public FeaturesCapable() {
        features.setFast(true);
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void optimizeRead(boolean fast) {
        features.setFast(fast);
    }

    public <T> T getFeature(String key) {
        return (T) features.get(key);
    }

    public <T> T getFeature(String key, T defaultValue) {
        final T v = (T) features.get(key);
        if (v == null) {
            return (features.containsKey(key)) ? null : defaultValue;
        } else {
            return v;
        }
    }

    /** convenience method. */
    public <T> void putFeature(String key, T value) {
        features.put(key, value);
    }

    /** create a deep copy! (copy receiver and copy properties) */
    public <T extends FeaturesCapable> T copy() {
        try {
            T self = (T) clone();
            copyInto(self);
            return self;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("cannot clone() " + this, e);
        }
    }

    protected <T extends FeaturesCapable> void copyInto(T target) {
        target.features = (FastHashMap) features.clone();
        if (validations != null) {
            target.validations = validations.clone();
        }
    }

    public Validation[] getValidations() {
        return validations;
    }

    public void addValidation(Validation validation) {
        if (validations.length == 0) {
            validations = new Validation[1];
        } else {
            Validation[] newvalidations = new Validation[validations.length + 1];
            System.arraycopy(validations, 0, newvalidations, 0, validations.length);
            validations = newvalidations;
        }
        validations[validations.length - 1] = validation;
    }

    public boolean hasValidation(Validation aValidation) {
        if (validations == null) return false;
        for (Validation validation : validations) {
            if (validation.equals(aValidation)) return true;
        }
        return false;
    }
}
