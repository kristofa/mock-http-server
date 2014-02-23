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

public class DefaultContentDecoratorTest {

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
        final DefaultContentDecorator defaultContentProxy = new DefaultContentDecorator(string.getBytes());
        final DefaultContentDecorator defaultContentProxy2 = new DefaultContentDecorator(equalString.getBytes());

        assertEquals(defaultContentProxy.hashCode(), defaultContentProxy2.hashCode());
    }

    @Test
    public void testGetContent() {
        final byte[] bytes = string.getBytes();
        final DefaultContentDecorator defaultContentProxy = new DefaultContentDecorator(bytes);
        assertSame(bytes, defaultContentProxy.getContent());

    }

    @Test
    public void testSetContent() {
        final DefaultContentDecorator defaultContentProxy = new DefaultContentDecorator();
        assertNull(defaultContentProxy.getContent());
        defaultContentProxy.setContent(string.getBytes());
        assertTrue(Arrays.equals(string.getBytes(), defaultContentProxy.getContent()));
    }

    @Test
    public void testEqualsObject() {
        final DefaultContentDecorator proxy = new DefaultContentDecorator(string.getBytes());
        final DefaultContentDecorator equalProxy = new DefaultContentDecorator(equalString.getBytes());
        final DefaultContentDecorator notEqualProxy = new DefaultContentDecorator(notEqualString.getBytes());

        assertFalse(proxy.equals(null));
        assertFalse(proxy.equals(new String("abcd")));
        assertTrue(proxy.equals(proxy));
        assertTrue(proxy.equals(equalProxy));
        assertFalse(proxy.equals(notEqualProxy));
    }

    @Test
    public void testCopyUninitializedObject() {
        final DefaultContentDecorator defaultContentProxy = new DefaultContentDecorator();
        final ContentDecorator copy = defaultContentProxy.copy();
        assertNotSame(defaultContentProxy, copy);
        assertEquals(defaultContentProxy, copy);
    }

    @Test
    public void testCopyInitializedObject() {
        final DefaultContentDecorator defaultContentProxy = new DefaultContentDecorator();
        defaultContentProxy.setContent(string.getBytes());
        final ContentDecorator copy = defaultContentProxy.copy();
        assertNotSame(defaultContentProxy, copy);
        assertEquals(defaultContentProxy, copy);

    }

}
