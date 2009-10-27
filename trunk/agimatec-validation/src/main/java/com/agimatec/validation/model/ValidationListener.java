package com.agimatec.validation.model;

import com.agimatec.validation.ValidationResults;

import java.io.Serializable;


/**
 * Description: The interface to collect errors found during validation<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 13:18:24 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface ValidationListener {
    /**
     * Simple API to add an error reason during validation.
     * Error notification added from a {@link Validation} with context information
     * taken from the given {@link ValidationContext}.
     *
     * @param reason  a constant describing the reason. This is normally the key of the
     *                feature that was violated in the object 'owner' for property 'propertyName'
     * @param context - contains
     *                bean =         the object that contains the error (owner)
     *                propertyName = the Name of the attribute that caused the error
     */
    void addError(String reason, ValidationContext context);

    /** Alternative method to add a fully initialized {@link ValidationResults.Error} object. */
    void addError(Error error, ValidationContext context);

    /**
     * an object holding a single validation constraint violation
     * found during the validation process.
     */
    public class Error implements Serializable {
        final String reason;
        final Object owner;
        final String propertyName;

        public Error(String aReason, Object aOwner, String aPropertyName) {
            this.reason = aReason;
            this.owner = aOwner;
            this.propertyName = aPropertyName;
        }

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
