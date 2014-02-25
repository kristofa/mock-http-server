package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class DefaultContentMatcherTest {

    private String string;
    private String equalString;
    private String notEqualString;

    @Before
    public void setup() {
        string = new String("abcd");
        equalString = new String("abcd");
        notEqualString = new String("abcde");
    }

    @Test
    public void testHashCode() {
        final DefaultContentMatcher defaultContentProxy = new DefaultContentMatcher(string.getBytes());
        final DefaultContentMatcher defaultContentProxy2 = new DefaultContentMatcher(equalString.getBytes());

        assertEquals(defaultContentProxy.hashCode(), defaultContentProxy2.hashCode());
    }

    @Test
    public void testGetContent() {
        final byte[] bytes = string.getBytes();
        final DefaultContentMatcher defaultContentProxy = new DefaultContentMatcher(bytes);
        assertSame(bytes, defaultContentProxy.getContent());

    }

    @Test
    public void testSetContent() {
        final DefaultContentMatcher defaultContentProxy = new DefaultContentMatcher();
        assertNull(defaultContentProxy.getContent());
        defaultContentProxy.setContent(string.getBytes());
        assertTrue(Arrays.equals(string.getBytes(), defaultContentProxy.getContent()));
    }

    @Test
    public void testEqualsObject() {
        final DefaultContentMatcher proxy = new DefaultContentMatcher(string.getBytes());
        final DefaultContentMatcher equalProxy = new DefaultContentMatcher(equalString.getBytes());
        final DefaultContentMatcher notEqualProxy = new DefaultContentMatcher(notEqualString.getBytes());

        assertFalse(proxy.equals(null));
        assertFalse(proxy.equals(new String("abcd")));
        assertTrue(proxy.equals(proxy));
        assertTrue(proxy.equals(equalProxy));
        assertFalse(proxy.equals(notEqualProxy));
    }

    @Test
    public void testCopyUninitializedObject() {
        final DefaultContentMatcher defaultContentProxy = new DefaultContentMatcher();
        final ContentMatcher copy = defaultContentProxy.copy();
        assertNotSame(defaultContentProxy, copy);
        assertEquals(defaultContentProxy, copy);
    }

    @Test
    public void testCopyInitializedObject() {
        final DefaultContentMatcher defaultContentProxy = new DefaultContentMatcher();
        defaultContentProxy.setContent(string.getBytes());
        final ContentMatcher copy = defaultContentProxy.copy();
        assertNotSame(defaultContentProxy, copy);
        assertEquals(defaultContentProxy, copy);

    }

}
