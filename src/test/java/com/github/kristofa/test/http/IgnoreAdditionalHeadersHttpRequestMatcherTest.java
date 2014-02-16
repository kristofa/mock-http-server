package com.github.kristofa.test.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class IgnoreAdditionalHeadersHttpRequestMatcherTest {

    private static final String PATH = "/test/";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private HttpRequestImpl request;
    private IgnoreAdditionalHeadersHttpRequestMatcher matcher;

    @Before
    public void setup() {
        request = new HttpRequestImpl();
        request.method(Method.GET);
        request.httpMessageHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.path(PATH);

        matcher = new IgnoreAdditionalHeadersHttpRequestMatcher();

    }

    @Test
    public void testMatch() {

        final HttpRequestImpl request2 = new HttpRequestImpl(request);
        request2.httpMessageHeader("another", "header");
        assertTrue(matcher.match(request, request2));

        final HttpRequestImpl request3 = new HttpRequestImpl(request);
        request3.queryParameter("param1", "value1");
        assertFalse(matcher.match(request, request3));

        final HttpRequestImpl request4 = new HttpRequestImpl();
        request4.method(Method.GET);
        request4.path(PATH);
        assertFalse(matcher.match(request, request4));

    }

    @Test
    public void testGetResponse() {
        final HttpResponse mockResponse = mock(HttpResponse.class);
        assertSame(mockResponse, matcher.getResponse(request, mockResponse, request));
    }

}
