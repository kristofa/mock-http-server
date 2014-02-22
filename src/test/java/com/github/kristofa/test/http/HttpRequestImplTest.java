package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class HttpRequestImplTest {

    private final static byte[] CONTENT = "content".getBytes();
    private final static String PATH = "/test/a";
    private static final String QUERY_PARAM_KEY = "key1";
    private static final String QUERY_PARAM_VALUE = "value1";
    private static final String QUERY_PARAM_KEY2 = "key2";
    private static final String QUERY_PARAM_VALUE2 = "value2";
    private static final String QUERY_PARAM_VALUE3 = "value3";
    private static final String HTTP_MESSAGE_HEADER_NAME = "Content-Type";
    private static final String HTTP_MESSAGE_HEADER_VALUE = "application/json";
    private static final String HTTP_MESSAGE_HEADER_NAME2 = "header2";
    private static final String HTTP_MESSAGE_HEADER_VALUE2 = "value2";

    private HttpRequestImpl httpRequest;

    @Before
    public void setup() {
        httpRequest = new HttpRequestImpl();
    }

    @Test
    public void testMethod() {
        assertNull(httpRequest.getMethod());
        assertSame(httpRequest, httpRequest.method(Method.DELETE));
        assertEquals(Method.DELETE, httpRequest.getMethod());

    }

    @Test
    public void testContent() {
        assertNull(httpRequest.getContent());
        assertSame(httpRequest, httpRequest.content(CONTENT));
        assertTrue(Arrays.equals(CONTENT, httpRequest.getContent()));
    }

    @Test
    public void testPath() {
        assertNull(httpRequest.getPath());
        assertSame(httpRequest, httpRequest.path(PATH));
        assertEquals(PATH, httpRequest.getPath());
    }

    @Test
    public void testHttpMessageHeader() {
        assertTrue(httpRequest.getHttpMessageHeaders().isEmpty());
        assertSame(httpRequest, httpRequest.httpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE)
            .httpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2));

        final Set<HttpMessageHeader> expectedHeaders = new HashSet<HttpMessageHeader>();
        expectedHeaders.add(new HttpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE));
        expectedHeaders.add(new HttpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2));
        assertEquals(expectedHeaders, httpRequest.getHttpMessageHeaders());
    }

    @Test
    public void testGetHttpMessageHeadersForName() {
        assertTrue(httpRequest.getHttpMessageHeaders(HTTP_MESSAGE_HEADER_NAME).isEmpty());
        httpRequest.httpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE).httpMessageHeader(
            HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2);
        final Set<HttpMessageHeader> httpMessageHeaders = httpRequest.getHttpMessageHeaders(HTTP_MESSAGE_HEADER_NAME);
        assertEquals(1, httpMessageHeaders.size());
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE)));
    }

    @Test
    public void testRemoveHttpMessageHeader() {
        assertTrue(httpRequest.getHttpMessageHeaders().isEmpty());
        assertSame(httpRequest, httpRequest.httpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE)
            .httpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2));

        assertSame(httpRequest, httpRequest.removeHttpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2));

        final Set<HttpMessageHeader> expectedHeaders = new HashSet<HttpMessageHeader>();
        expectedHeaders.add(new HttpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE));
        assertEquals(expectedHeaders, httpRequest.getHttpMessageHeaders());
    }

    @Test
    public void testRemoveHttpMessageHeaders() {
        assertTrue(httpRequest.getHttpMessageHeaders().isEmpty());
        assertSame(
            httpRequest,
            httpRequest.httpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE)
                .httpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2)
                .httpMessageHeader(HTTP_MESSAGE_HEADER_NAME, HTTP_MESSAGE_HEADER_VALUE2));

        assertSame(httpRequest, httpRequest.removeHttpMessageHeaders(HTTP_MESSAGE_HEADER_NAME));

        final Set<HttpMessageHeader> expectedHeaders = new HashSet<HttpMessageHeader>();
        expectedHeaders.add(new HttpMessageHeader(HTTP_MESSAGE_HEADER_NAME2, HTTP_MESSAGE_HEADER_VALUE2));
        assertEquals(expectedHeaders, httpRequest.getHttpMessageHeaders());

    }

    @Test
    public void testQueryParameter() {
        assertTrue(httpRequest.getQueryParameters().isEmpty());
        assertSame(
            httpRequest,
            httpRequest.queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2,
                QUERY_PARAM_VALUE2));

        final Set<QueryParameter> expectedParameters = new HashSet<QueryParameter>();
        expectedParameters.add(new QueryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE));
        expectedParameters.add(new QueryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2));
        assertEquals(expectedParameters, httpRequest.getQueryParameters());
    }

    @Test
    public void testGetQueryParametersForKey() {
        assertTrue(httpRequest.getQueryParameters(QUERY_PARAM_KEY).isEmpty());
        httpRequest.queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);
        final Set<QueryParameter> queryParams = httpRequest.getQueryParameters(QUERY_PARAM_KEY);
        assertEquals(1, queryParams.size());
        assertTrue(queryParams.contains(new QueryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE)));
    }

    @Test
    public void testRemoveQueryParameter() {
        assertTrue(httpRequest.getQueryParameters().isEmpty());
        assertSame(
            httpRequest,
            httpRequest.queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2,
                QUERY_PARAM_VALUE2));

        assertSame(httpRequest, httpRequest.removeQueryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE));

        final Set<QueryParameter> expectedParameters = new HashSet<QueryParameter>();
        expectedParameters.add(new QueryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2));
        assertEquals(expectedParameters, httpRequest.getQueryParameters());
    }

    @Test
    public void testRemoveQueryParameters() {
        assertTrue(httpRequest.getQueryParameters().isEmpty());
        assertSame(
            httpRequest,
            httpRequest.queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE)
                .queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2).queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE3));

        assertEquals(3, httpRequest.getQueryParameters().size());

        assertSame(httpRequest, httpRequest.removeQueryParameters(QUERY_PARAM_KEY));

        final Set<QueryParameter> expectedParameters = new HashSet<QueryParameter>();
        expectedParameters.add(new QueryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2));
        assertEquals(expectedParameters, httpRequest.getQueryParameters());
    }

    @Test
    public void testToString() {
        assertEquals("Method: null\nMessage Header: []\nPath: null\nQuery Parameters: []\nContent:\nnull",
            httpRequest.toString());
        httpRequest.content(CONTENT)
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), MediaType.APPLICATION_JSON_UTF8.getValue())
            .method(Method.GET).path(PATH).queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE)
            .queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);
        assertEquals(
            "Method: GET\nMessage Header: [Content-Type: application/json; charset=UTF-8]\nPath: /test/a\nQuery Parameters: [key1=value1, key2=value2]\nContent:\ncontent",
            httpRequest.toString());
    }

    @Test
    public void testCopyConstructor() {

        final HttpRequestImpl copyNonInitializedRequest = new HttpRequestImpl(httpRequest);

        assertNotSame(httpRequest, copyNonInitializedRequest);
        assertEquals(copyNonInitializedRequest, httpRequest);

        httpRequest.content(CONTENT)
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), MediaType.APPLICATION_JSON_UTF8.getValue())
            .method(Method.GET).path(PATH).queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE)
            .queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);

        assertFalse(httpRequest.equals(copyNonInitializedRequest));

        final HttpRequestImpl copyInitializedRequest = new HttpRequestImpl(httpRequest);
        assertNotSame(httpRequest, copyInitializedRequest);
        assertEquals(copyInitializedRequest, httpRequest);

    }
}
