package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Max;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 18.11.2009 <br/>
 * Time: 10:00:15 <br/>
 * Copyright: Agimatec GmbH
 */
public class NoValidatorTestEntity {
    @Max(20)
    private Object anything;
    
}
