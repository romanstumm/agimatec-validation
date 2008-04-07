package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.model.MetaProperty;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 07.04.2008 <br/>
 * Time: 09:11:10 <br/>
 * Copyright: Agimatec GmbH
 */
final class ValidationEntry implements Comparable<ValidationEntry> {
    final MetaProperty metaProperty;
    final Validation validation;

    public ValidationEntry(MetaProperty metaProperty, Validation validation) {
        this.metaProperty = metaProperty;
        this.validation = validation;
    }

    public int compareTo(ValidationEntry o) {
        // 1. field, 2. method | property.name
        if(validation.isFieldAccess()) {
            if(!o.validation.isFieldAccess()) return -1;
        } else {
            if(o.validation.isFieldAccess()) return 1;
        }
        return metaProperty.getName().compareTo(o.metaProperty.getName());
    }
}
