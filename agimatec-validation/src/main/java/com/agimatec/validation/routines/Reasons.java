package com.agimatec.validation.routines;

import com.agimatec.validation.model.Features;

/**
 * Description: StandardReasons for validation errors found in
 * {@link com.agimatec.validation.ValidationResults}<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 13:20:43 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface Reasons extends Features.Property {
    // The reasons inherited from Features are VALIDATION features only.
    // INFO features are not meant to be validated.

    // Add more reasons here.
    String EMAIL_ADDRESS = "emailAddress";
}
