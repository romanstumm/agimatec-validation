package com.agimatec.validation.jsr303.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.Path;
import java.util.Iterator;

/**
 * ErrorBuilderImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10/01/2009</pre>
 */
public class PathImplTest extends TestCase {
    public PathImplTest(String name) {
        super(name);
    }

    public void testParsing() {
        String property = "order[3].deliveryAddress.addressline[1]";
        Path path = PathImpl.fromString(property);
        Iterator<Path.Node> propIter = path.iterator();

        assertTrue(propIter.hasNext());
        Path.Node elem = propIter.next();
        assertEquals("order", elem.getName());
        assertTrue(elem.isInIterable());
        assertEquals(new Integer(3), elem.getIndex());

        assertTrue(propIter.hasNext());
        elem = propIter.next();
        assertEquals("deliveryAddress", elem.getName());
        assertFalse(elem.isInIterable());
        assertEquals(null, elem.getIndex());

        assertTrue(propIter.hasNext());
        elem = propIter.next();
        assertEquals("addressline", elem.getName());
        assertTrue(elem.isInIterable());
        assertEquals(new Integer(1), elem.getIndex());

        assertFalse(propIter.hasNext());
    }

    public void testParseMapBasedProperty() {
        String property = "order[foo].deliveryAddress";
        Path path = PathImpl.fromString(property);
        Iterator<Path.Node> propIter = path.iterator();

        assertTrue(propIter.hasNext());
        Path.Node elem = propIter.next();
        assertEquals("order", elem.getName());
        assertTrue(elem.isInIterable());
        assertEquals("foo", elem.getKey());

        assertTrue(propIter.hasNext());
        elem = propIter.next();
        assertEquals("deliveryAddress", elem.getName());
        assertFalse(elem.isInIterable());
        assertEquals(null, elem.getIndex());

        assertFalse(propIter.hasNext());
    }

    public void testNull() {
        assertEquals(PathImpl.fromString(null), PathImpl.create(null));
        Path path = PathImpl.create(null);
        assertFalse(path.iterator().hasNext());
    }

    public void testUnbalancedBraces() {
        try {
            PathImpl.fromString("foo[.bar");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testIndexInMiddleOfProperty() {
        try {
            PathImpl.fromString("f[1]oo.bar");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testTrailingPathSeperator() {
        try {
            PathImpl.fromString("foo.bar.");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testLeadingPathSeperator() {
        try {
            PathImpl.fromString(".foo.bar");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testEmptyString() {
        Path path = PathImpl.fromString("");
        assertFalse(path.iterator().hasNext());
    }

    public static Test suite() {
        return new TestSuite(PathImplTest.class);
    }
}
