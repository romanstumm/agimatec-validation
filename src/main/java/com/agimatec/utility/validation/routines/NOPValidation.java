package com.agimatec.utility.validation.routines;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;

/**
 * Description: DO NOTHING VALIDATION (can be used to turn off standard validation)<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 16:51:28 <br/>
 *
 */
public class NOPValidation implements Validation {
    public void validate(ValidationContext context, ValidationListener listener) {
        // do nothing
    }
}
