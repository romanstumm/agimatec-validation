package com.agimatec.utility.validation;

import com.agimatec.utility.validation.xml.XMLMetaBeanRegistry;

/**
 * Description: API class to hold a singleton of a {@link MetaBeanManager}
 * that implements the finder and registry interfaces for MetaBeans<br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 16:20:03 <br/>
 *
 * @see com.agimatec.utility.validation.model.MetaBean
 * @see com.agimatec.utility.validation.MetaBeanManager
 */
public class MetaBeanManagerFactory {
    private static MetaBeanManager manager = new MetaBeanManager();

    public static MetaBeanFinder getFinder() {
        return manager;
    }

    public static XMLMetaBeanRegistry getRegistry()
    {
        return manager;
    }

    public static MetaBeanEnricher getEnricher() {
        return manager;
    }

    public static void setManager(MetaBeanManager finder) {
        manager = finder;
    }
}
