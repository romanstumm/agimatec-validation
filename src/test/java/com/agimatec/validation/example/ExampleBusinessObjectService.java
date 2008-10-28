package com.agimatec.validation.example;

import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.integration.Validate;

import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:51:59 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface ExampleBusinessObjectService {
    /** validated method example */
    @Validate
    public void saveBusinessObject(@Validate BusinessObject object, Object other);

    @Validate
    public void saveBusinessObjects(@Validate BusinessObject[] object);

    /** explicit validation example */
    public ValidationResults validateBusinessObject(
            @Validate("BusinessObject")BusinessObject object);

    /** query by example - example */
    public List<BusinessObject> findBusinessObjects(BusinessObject example);
}
