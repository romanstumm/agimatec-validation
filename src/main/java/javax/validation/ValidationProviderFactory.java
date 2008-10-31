package javax.validation;

import sun.misc.Service;

import java.util.Iterator;

/**
 * --
 * This class is NOT part of the bean_validation spec and might not be supported
 * as soon as the specification contains detailed information about "Bootstrapping".
 * --
 *
 * Description: Simple Factory using JDK Service discovery mechanism
 * to discover the ValidationProvider.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 09:44:12 <br/>
 *
 */
public class ValidationProviderFactory {
    private static ValidationProvider provider;

    public static ValidationProvider getProvider() {
        if (provider == null) provider = discover();
        return provider;
    }

    public static void setProvider(ValidationProvider provider) {
        ValidationProviderFactory.provider = provider;
    }

    /**
     * @see sun.misc.Service#providers(Class) the algorithm used here
     * @see "org.apache.commons.discovery.tools.DiscoverClass" as an alternative approach
     * @return provider discovered by sun.misc.Service
     */
    private static ValidationProvider discover() {
        final Iterator<ValidationProvider> ps = Service.providers(ValidationProvider.class);
        if (ps.hasNext()) {
            return ps.next();
        }
        return null;
    }

    /** Validator object factory method - */
    public static Validator createValidator(Class aBeanClass) {
        return getProvider().createValidator(aBeanClass);
    }
}
