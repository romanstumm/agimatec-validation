package com.agimatec.validation.model;


/**
 * Description: The interface to collect errors found during validation<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 13:18:24 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface ValidationListener {
    /**
     * Error notification sent by a Validation.
     *
     * @param reason  a constant describing the reason. This is normally the key of the
     *                feature that was violated in the object 'owner' for property 'propertyName'
     * @param context - contains
     *                bean =         the object that contains the error (owner)
     *                propertyName = the Name of the attribute that caused the error
     */
    void addError(String reason, ValidationContext context);
}
