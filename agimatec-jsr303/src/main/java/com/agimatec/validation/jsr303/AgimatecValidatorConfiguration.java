package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;

import javax.validation.Configuration;

/**
 * Description: Uniquely identify Agimatec Validation in the Bean Validation bootstrap
 * strategy. Also contains agimatec validation specific configurations<br/>
 * User: roman.stumm <br/>
 * Date: 28.10.2008 <br/>
 * Time: 16:16:45 <br/>
 * Copyright: Agimatec GmbH
 */
public interface AgimatecValidatorConfiguration
        extends Configuration<AgimatecValidatorConfiguration> {
    /**
     * set the implementation class for bean validation. the implementation
     * is responsible to provide a validation context.
     */
    AgimatecValidatorConfiguration beanValidator(BeanValidator beanValidator);
}
