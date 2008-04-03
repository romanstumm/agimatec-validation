package com.agimatec.utility.validation;

import java.io.Serializable;
import java.util.*;

/**
 * Description: Implements a contains to hold and transport validation results<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:26:55 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ValidationResults implements ValidationListener, Serializable {
    private Map<String, List<Error>> errorsByReason;
    private Map<Object, Map<String, List<Error>>> errorsByOwner;

    public void addError(String reason, ValidationContext context) {
        if (errorsByReason == null) {
            initialize();
        }
        Error error = createError(reason, context.getBean(), context.getPropertyName());

        addToReasonBucket(error);
        addToOwnerBucket(error);
    }

    protected Error createError(String reason, Object owner, String propertyName) {
        Error error = new Error();
        error.reason = reason;
        error.owner = owner;
        error.propertyName = propertyName;
        return error;
    }

    /**
     * initialize the error-buckets now when needed and
     * not on instance creation to save memory garbage.
     */
    protected void initialize() {
        errorsByReason = new LinkedHashMap();
        errorsByOwner = new LinkedHashMap();
    }

    protected void addToReasonBucket(Error error) {
        if (error.reason == null) return;

        List<Error> errors = errorsByReason.get(error.reason);
        if (errors == null) {
            errors = new ArrayList<Error>();
            errorsByReason.put(error.reason, errors);
        }
        errors.add(error);
    }

    protected void addToOwnerBucket(Error error) {
        if (error.owner == null) return;

        Map<String, List<Error>> errors = errorsByOwner.get(error.owner);
        if (errors == null) {
            errors = new HashMap<String, List<Error>>();
            errorsByOwner.put(error.owner, errors);
        }
        List<Error> list = errors.get(error.propertyName);
        if (list == null) {
            list = new ArrayList<Error>();
            errors.put(error.propertyName, list);
        }
        list.add(error);
    }

    /**
     * key = reason, value = list of errors for this reason
     *
     * @return
     */
    public Map<String, List<Error>> getErrorsByReason() {
        if (errorsByReason == null) return Collections.emptyMap();
        return errorsByReason;
    }

    /**
     * key = owner, value = map with:<br>
     * &nbsp;&nbsp; key = propertyName, value = list of errors for this owner.propertyName
     *
     * @return
     */
    public Map<Object, Map<String, List<Error>>> getErrorsByOwner() {
        if (errorsByOwner == null) return Collections.emptyMap();
        return errorsByOwner;
    }

    /**
     * @return true when there are NO errors in this validation result
     */
    public boolean isEmpty() {
        if (errorsByReason == null ||
                (errorsByReason.isEmpty() && errorsByOwner.isEmpty())) return true;
        for (List<Error> list : errorsByReason.values()) {
            if (!list.isEmpty()) return false;
        }
        for (Map<String, List<Error>> map : errorsByOwner.values()) {
            for (List<Error> list : map.values()) {
                if (!list.isEmpty()) return false;
            }
        }
        return true;
    }

    public boolean hasErrorForReason(String reason) {
        if (errorsByReason == null) return false;
        List<Error> errors = errorsByReason.get(reason);
        return errors != null && !errors.isEmpty();
    }

    /**
     * @param bean
     * @param propertyName - may be null: any property is checked
     *                     OR the name of the property to check
     * @return
     */
    public boolean hasError(Object bean, String propertyName) {
        if (errorsByOwner == null) return false;
        Map<String, List<Error>> errors = errorsByOwner.get(bean);
        if (errors == null) return false;
        if (propertyName != null) {
            List<Error> list = errors.get(propertyName);
            return list != null && !list.isEmpty();
        } else {
            for (List<Error> list : errors.values()) {
                if (!list.isEmpty()) return true;
            }
            return false;
        }
    }

    public String toString() {
        return "ValidationResults{" + errorsByReason + "}";
    }

    public static class Error implements Serializable {
        String reason;
        Object owner;
        String propertyName;

        public String getReason() {
            return reason;
        }

        public Object getOwner() {
            return owner;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String toString() {
            return "Error{" + "reason='" + reason + '\'' + ", propertyName='" +
                    propertyName + '\'' + '}';
        }
    }
}
