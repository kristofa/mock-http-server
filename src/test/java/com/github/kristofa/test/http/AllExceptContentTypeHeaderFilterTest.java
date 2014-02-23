package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class AllExceptContentTypeHeaderFilterTest {

    private static final String PATH1 = "/path1/";
    private static final String PATH2 = "/path2/";
    private static final String QUERY_PARAM_NAME_1 = "qp1";
    private static final String QUERY_PARAM_VALUE_1 = "qpv1";
    private static final String QUERY_PARAM_NAME_2 = "qp2";
    private static final String QUERY_PARAM_VALUE_2 = "qpv2";
    private static final String QUERY_PARAM_NAME_3 = "qp3";
    private static final String QUERY_PARAM_VALUE_3 = "qpv2";
    private static final String HEADER_1_NAME = "hn1";
    private static final String HEADER_1_VALUE = "hv1";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String HEADER_CONTENT_TYPE_VALUE_2 = "application/xml";

    private AllExceptContentTypeHeaderFilter matcher;
    private HttpResponse mockResponse;

    @Before
    public void setup() {
        matcher = new AllExceptContentTypeHeaderFilter();
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    public void testMatchFalseDifferentMethod() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.PUT).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertFalse(context.originalRequest().equals(context.otherRequest()));
        assertEquals(httpRequestImpl, context.originalRequest());
        assertEquals(httpRequestImpl2, context.otherRequest());
        assertEquals(mockResponse, context.response());

    }

    @Test
    public void testMatchFalseDifferentPath() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH2).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertFalse(context.originalRequest().equals(context.otherRequest()));
        assertEquals(httpRequestImpl, context.originalRequest());
        assertEquals(httpRequestImpl2, context.otherRequest());
        assertEquals(mockResponse, context.response());

    }

    @Test
    public void testMatchFalseDifferentQueryParam() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_3, QUERY_PARAM_VALUE_3)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertFalse(context.originalRequest().equals(context.otherRequest()));
        assertEquals(httpRequestImpl, context.originalRequest());
        assertEquals(httpRequestImpl2, context.otherRequest());
        assertEquals(mockResponse, context.response());
    }

    @Test
    public void testMatchFalseDifferentContentTypeValue() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE_2);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertFalse(context.originalRequest().equals(context.otherRequest()));
        assertEquals(httpRequestImpl, context.originalRequest());
        assertEquals(httpRequestImpl2, context.otherRequest());
        assertEquals(mockResponse, context.response());

    }

    @Test
    public void testMatchTrueNoContentTypeDifferentHeaders() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertTrue(context.originalRequest().equals(context.otherRequest()));
        assertFalse(httpRequestImpl.equals(context.originalRequest()));
        assertEquals(httpRequestImpl2, context.otherRequest());
        assertEquals(mockResponse, context.response());

    }

    @Test
    public void testMatchTrueSameContentTypeAndDifferentHeaders() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertTrue(context.originalRequest().equals(context.otherRequest()));
        assertFalse(httpRequestImpl.equals(context.originalRequest()));
        assertFalse(httpRequestImpl2.equals(context.otherRequest()));
        assertEquals(mockResponse, context.response());
    }

    @Test
    public void testMatchFalseSameContentTypeDifferentContent() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE).content(new String("a").getBytes());
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE).content(new String("b").getBytes());

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertFalse(context.originalRequest().equals(context.otherRequest()));
        assertFalse(httpRequestImpl.equals(context.originalRequest()));
        assertFalse(httpRequestImpl2.equals(context.otherRequest()));
        assertEquals(mockResponse, context.response());
    }

    @Test
    public void testMatchTrueSameContentTypeSameContent() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE).content(new String("a").getBytes());
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2)
            .httpMessageHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE).content(new String("a").getBytes());

        final HttpRequestMatchingContext context =
            matcher.filter(new HttpRequestMatchingContextImpl(httpRequestImpl, httpRequestImpl2, mockResponse));
        assertTrue(context.originalRequest().equals(context.otherRequest()));
        assertFalse(httpRequestImpl.equals(context.originalRequest()));
        assertTrue(httpRequestImpl2.equals(context.otherRequest()));
        assertEquals(mockResponse, context.response());
    }

}
