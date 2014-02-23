package com.github.kristofa.test.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class DefaultHttpResponseProxyTest {

    private DefaultHttpResponseProxy responseProxy;
    private HttpResponse mockResponse;

    @Before
    public void setup() {
        mockResponse = mock(HttpResponse.class);
        responseProxy = new DefaultHttpResponseProxy(mockResponse);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultHttpResponseProxy() {
        new DefaultHttpResponseProxy(null);
    }

    @Test
    public void testConsumed() {
        assertFalse(responseProxy.consumed());
    }

    @Test
    public void testGetResponse() {
        assertSame(mockResponse, responseProxy.getResponse());
        assertFalse(responseProxy.consumed());
    }

    @Test
    public void testConsume() {
        assertSame(mockResponse, responseProxy.consume());
        assertTrue(responseProxy.consumed());
    }

}
