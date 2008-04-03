package com.agimatec.utility.validation.example;

import com.agimatec.utility.validation.ValidationResults;
import com.agimatec.utility.validation.integration.Validate;

import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:51:59 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface ExampleBusinessObjectService {
    /**
     * validated method example
     * @param object
     * @param other
     */
    @Validate
    public void saveBusinessObject(@Validate BusinessObject object, Object other);

    /**
     * explicit validation example
     * @param object
     * @return
     */
    public ValidationResults validateBusinessObject(@Validate("BusinessObject") BusinessObject object);

    /**
     * query by example - example
     * @param example
     * @return
     */
    public List<BusinessObject> findBusinessObjects(BusinessObject example);
}
