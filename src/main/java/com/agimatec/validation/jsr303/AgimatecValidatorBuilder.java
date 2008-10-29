package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;

import javax.validation.ValidatorBuilder;

/**
 * Description: Uniquely identify Agimatec Validation in the Bean Validation bootstrap
 * strategy. Also contains agimatec validation specific configurations<br/>
 * User: roman.stumm <br/>
 * Date: 28.10.2008 <br/>
 * Time: 16:16:45 <br/>
 * Copyright: Agimatec GmbH
 */
public interface AgimatecValidatorBuilder extends ValidatorBuilder<AgimatecValidatorBuilder> {
    /**
     * set the implementation class for bean validation. the implementation
     * is responsible to provide a validation context.
     * @param beanValidator
     * @return
     */
    AgimatecValidatorBuilder beanValidator(BeanValidator beanValidator);
}
