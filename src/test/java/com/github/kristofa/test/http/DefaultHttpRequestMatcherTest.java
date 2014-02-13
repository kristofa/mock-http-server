package com.github.kristofa.test.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class DefaultHttpRequestMatcherTest {

    private HttpRequest mockRequest;
    private DefaultHttpRequestMatcher matcher;

    @Before
    public void setup() {
        mockRequest = mock(HttpRequest.class);
        matcher = new DefaultHttpRequestMatcher(mockRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultHttpRequestMatcher() {
        new DefaultHttpRequestMatcher(null);
    }

    @Test
    public void testMatch() {
        assertTrue(matcher.match(mockRequest));
        final HttpRequest mockRequest2 = mock(HttpRequest.class);
        assertFalse(matcher.match(mockRequest2));
    }

    @Test
    public void testGetResponse() {
        final HttpResponse mockResponse = mock(HttpResponse.class);
        assertSame(mockResponse, matcher.getResponse(mockRequest, mockResponse));
    }

}
