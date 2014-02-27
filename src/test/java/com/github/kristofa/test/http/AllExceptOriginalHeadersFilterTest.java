package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class AllExceptOriginalHeadersFilterTest {

    private static final String PATH = "/test/";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private HttpRequestImpl request;
    private HttpResponse response;
    private AllExceptOriginalHeadersFilter matcher;

    @Before
    public void setup() {
        request = new HttpRequestImpl();
        request.method(Method.GET);
        request.httpMessageHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.path(PATH);

        response = mock(HttpResponse.class);

        matcher = new AllExceptOriginalHeadersFilter();

    }

    @Test
    public void testFilterAdditionalHeader() {
        final HttpRequestImpl request2 = new HttpRequestImpl(request);
        request2.httpMessageHeader("another", "header");
        final HttpRequestMatchingContext newContext =
            matcher.filter(new HttpRequestMatchingContextImpl(request, request2, response));
        assertEquals(request, newContext.originalRequest());
        assertEquals(newContext.originalRequest(), newContext.otherRequest());
        assertEquals(response, newContext.response());
    }

    @Test
    public void testFilterAdditionalQueryParam() {

        final HttpRequestImpl request3 = new HttpRequestImpl(request);
        request3.queryParameter("param1", "value1");
        final HttpRequestMatchingContext newContext =
            matcher.filter(new HttpRequestMatchingContextImpl(request, request3, response));
        assertEquals(request, newContext.originalRequest());
        assertFalse(newContext.originalRequest().equals(newContext.otherRequest()));
        assertEquals(response, newContext.response());

    }

    @Test
    public void testFilterMissingHeader() {

        final HttpRequestImpl request4 = new HttpRequestImpl();
        request4.method(Method.GET);
        request4.path(PATH);
        final HttpRequestMatchingContext newContext =
            matcher.filter(new HttpRequestMatchingContextImpl(request, request4, response));
        assertEquals(request, newContext.originalRequest());
        assertFalse(newContext.originalRequest().equals(newContext.otherRequest()));
        assertEquals(response, newContext.response());

    }

}
