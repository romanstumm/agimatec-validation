package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.Features;

/**
 * Description: Contains MetaBean feature keys of additional features used in the implementation
 * of JSR303<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 15:22:49 <br/>
 * Copyright: Agimatec GmbH 2008
 *
 * @see com.agimatec.validation.model.FeaturesCapable
 * @see com.agimatec.validation.model.Features
 */
public interface Jsr303Features {
    interface Property extends Features.Property {
        /** INFO: cached PropertyDescriptorImpl of the property */
        String PropertyDescriptor = "PropertyDescriptor";
    }

    interface Bean extends Features.Bean {
        /** INFO: List of Class for {@link javax.validation.GroupSequence#value()} */
        String GROUP_SEQ = "GroupSequence";

        /** INFO: cached sortied Array with ValidationEntries */
        String ValidationSequence = "ValidationSequence";

        /**
         * INFO: cached BeanDescriptorImpl of the bean
         */
        String BeanDescriptor = "BeanDescriptor";
    }
}
