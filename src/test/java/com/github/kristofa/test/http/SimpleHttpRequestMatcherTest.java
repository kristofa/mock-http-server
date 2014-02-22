package com.github.kristofa.test.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SimpleHttpRequestMatcherTest {

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

    private SimpleHttpRequestMatcher matcher;

    @Before
    public void setup() {
        matcher = new SimpleHttpRequestMatcher();
    }

    @Test
    public void testMatchFalseDifferentMethod() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.PUT).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        assertFalse(matcher.match(httpRequestImpl, httpRequestImpl2));

    }

    @Test
    public void testMatchFalseDifferentPath() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH2).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        assertFalse(matcher.match(httpRequestImpl, httpRequestImpl2));

    }

    @Test
    public void testMatchFalseDifferentQueryParam() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_3, QUERY_PARAM_VALUE_3)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        assertFalse(matcher.match(httpRequestImpl, httpRequestImpl2));

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

        assertFalse(matcher.match(httpRequestImpl, httpRequestImpl2));

    }

    @Test
    public void testMatchTrueNoContentTypeDifferentHeaders() {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2).httpMessageHeader(HEADER_1_NAME, HEADER_1_VALUE);
        final HttpRequestImpl httpRequestImpl2 = new HttpRequestImpl();
        httpRequestImpl2.method(Method.GET).path(PATH1).queryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1)
            .queryParameter(QUERY_PARAM_NAME_2, QUERY_PARAM_VALUE_2);

        assertTrue(matcher.match(httpRequestImpl, httpRequestImpl2));
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

        assertTrue(matcher.match(httpRequestImpl, httpRequestImpl2));
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

        assertFalse(matcher.match(httpRequestImpl, httpRequestImpl2));
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

        assertTrue(matcher.match(httpRequestImpl, httpRequestImpl2));
    }

    @Test
    public void testGetResponse() {
        final HttpRequestImpl request1 = new HttpRequestImpl();
        final HttpResponseImpl response = new HttpResponseImpl(200, null, null);
        final HttpRequestImpl request2 = new HttpRequestImpl();
        assertSame(response, matcher.getResponse(request1, response, request2));
    }

}
