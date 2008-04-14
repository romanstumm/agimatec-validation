package com.agimatec.utility.validation;

/**
 * Description: Interface for a single validation <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 10:04:39 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface Validation {
  /**
   * Perform a single validation routine
   * Validate the object or property according to the current ValidationContext.
   * @param context - to access the property, value, constraints
   */
  void validate(ValidationContext context);
}
