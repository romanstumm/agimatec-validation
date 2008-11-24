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
interface Jsr303Features {
    interface Property extends Features.Property {
        /** INFO: cached ElementDescriptorImpl of the property */
        String ElementDescriptor = "ElementDescriptor";
    }

    interface Bean extends Features.Bean {
        /** INFO: Map with String->String[] for {@link javax.validation.GroupSequences} */
        String GROUP_SEQ = "GroupSequences";

        /** INFO: cached sortied Array with ValidationEntries */
        String ValidationSequence = "ValidationSequence";

        /**
         * INFO: cached BeanDescriptor
         */
        String BeanDescriptor = "BeanDescriptor";
    }
}
