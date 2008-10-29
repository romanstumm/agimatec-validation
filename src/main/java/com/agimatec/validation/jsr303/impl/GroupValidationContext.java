package com.agimatec.validation.jsr303.impl;

import com.agimatec.validation.ValidationContext;

import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:15:08 <br/>
 * Copyright: Agimatec GmbH
 */
interface GroupValidationContext extends ValidationContext {
    List<String> getSequencedGroups();

    void setCurrentGroup(String currentGroup);

    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    void resetValidated();

    void setFixedValue(Object value);

    String[] DEFAULT_GROUPS = {"default"};
}
