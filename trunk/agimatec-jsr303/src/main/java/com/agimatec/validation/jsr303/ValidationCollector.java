package com.agimatec.validation.jsr303;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 12:27:22 <br/>
 * Copyright: Agimatec GmbH
 */
public interface ValidationCollector {
     void addValidation(ConstraintValidation validation);
}
