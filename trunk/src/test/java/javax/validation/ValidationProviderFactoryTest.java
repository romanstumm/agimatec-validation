package javax.validation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ValidationProviderFactory Tester.
 *
 * @author ${USER}
 * @since <pre>04/02/2008</pre>
 * @version 1.0
 */
public class ValidationProviderFactoryTest extends TestCase {
    public ValidationProviderFactoryTest(String name) {
        super(name);
    }

    public void testGetProvider() throws Exception {
        ValidationProvider provider = ValidationProviderFactory.getProvider();
        assertNotNull(provider);
        ValidationProvider provider2 = ValidationProviderFactory.getProvider();
        assertTrue(provider2 == provider);        
    }

    public static Test suite() {
        return new TestSuite(ValidationProviderFactoryTest.class);
    }
}
