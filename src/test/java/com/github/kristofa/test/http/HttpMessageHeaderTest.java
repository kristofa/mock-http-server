package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HttpMessageHeaderTest {

    private final static String NAME = "name";
    private final static String VALUE = "value";

    private HttpMessageHeader header;

    @Before
    public void setup() {
        header = new HttpMessageHeader(NAME, VALUE);
    }

    @Test
    public void testGetName() {
        assertEquals(NAME, header.getName());
    }

    @Test
    public void testGetValue() {
        assertEquals(VALUE, header.getValue());
    }

    @Test
    public void testToString() {
        assertEquals(NAME + ": " + VALUE, header.toString());
    }

    @Test
    public void testHashCode() {
        final HttpMessageHeader equalHeader = new HttpMessageHeader(NAME, VALUE);
        assertEquals("Equal objects should have equal hashcode.", header.hashCode(), equalHeader.hashCode());
    }

    @Test
    public void testEquals() {
        assertFalse(header.equals(null));
        assertFalse(header.equals(new String()));
        assertTrue(header.equals(header));

        final HttpMessageHeader nonEqualHeader = new HttpMessageHeader(NAME + "a", VALUE);
        final HttpMessageHeader nonEqualHeader2 = new HttpMessageHeader(NAME, VALUE + "a");
        final HttpMessageHeader equalHeader = new HttpMessageHeader(NAME, VALUE);

        assertFalse(header.equals(nonEqualHeader));
        assertFalse(header.equals(nonEqualHeader2));
        assertTrue(header.equals(equalHeader));

    }

    @Test
    public void testCompareTo() {
        final HttpMessageHeader messageHeader = new HttpMessageHeader("a", "b");
        final HttpMessageHeader messageHeader2 = new HttpMessageHeader("a", "c");
        final HttpMessageHeader messageHeader3 = new HttpMessageHeader("b", "a");

        assertEquals(0, messageHeader.compareTo(messageHeader));
        assertTrue(messageHeader.compareTo(messageHeader2) < 0);
        assertTrue(messageHeader2.compareTo(messageHeader) > 0);
        assertTrue(messageHeader.compareTo(messageHeader3) < 0);
        assertTrue(messageHeader3.compareTo(messageHeader) > 0);

    }

}
