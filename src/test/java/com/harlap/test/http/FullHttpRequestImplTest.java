package com.harlap.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FullHttpRequestImplTest {

    private final static String CONTENT_TYPE = "application/json";
    private final static String CONTENT = "content";
    private final static String DOMAIN = "localhost";
    private final static String PATH = "/test/a";
    private static final int PORT = 8080;
    private static final String QUERY_PARAM_KEY = "key1";
    private static final String QUERY_PARAM_VALUE = "value1";
    private static final String QUERY_PARAM_KEY2 = "key2";
    private static final String QUERY_PARAM_VALUE2 = "value2";

    private FullHttpRequestImpl httpRequest;

    @Before
    public void setup() {
        httpRequest = new FullHttpRequestImpl();
    }

    @Test
    public void testMethod() {
        assertNull(httpRequest.getMethod());
        assertSame(httpRequest, httpRequest.method(Method.DELETE));
        assertEquals(Method.DELETE, httpRequest.getMethod());

    }

    @Test
    public void testContentType() {
        assertNull(httpRequest.getContentType());
        assertSame(httpRequest, httpRequest.contentType(CONTENT_TYPE));
        assertEquals(CONTENT_TYPE, httpRequest.getContentType());
    }

    @Test
    public void testContent() {
        assertNull(httpRequest.getContent());
        assertSame(httpRequest, httpRequest.content(CONTENT));
        assertEquals(CONTENT, httpRequest.getContent());
    }

    @Test
    public void testDomain() {
        assertNull(httpRequest.getDomain());
        assertSame(httpRequest, httpRequest.domain(DOMAIN));
        assertEquals(DOMAIN, httpRequest.getDomain());

    }

    @Test
    public void testPort() {
        assertNull(httpRequest.getPort());
        assertSame(httpRequest, httpRequest.port(PORT));
        assertEquals(Integer.valueOf(PORT), httpRequest.getPort());
    }

    @Test
    public void testPath() {
        assertNull(httpRequest.getPath());
        assertSame(httpRequest, httpRequest.path(PATH));
        assertEquals(PATH, httpRequest.getPath());
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
    public void testGetUrl() {
        assertEquals("http:///", httpRequest.getUrl());
        httpRequest.content(CONTENT).contentType(CONTENT_TYPE).domain(DOMAIN).method(Method.GET).path(PATH).port(PORT)
            .queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);
        assertEquals("http://localhost:8080/test/a?key1=value1&key2=value2", httpRequest.getUrl());
    }

    @Test
    public void testToString() {
        assertEquals(
            "Method: null\nContent-Type: null\nPath: null\nQuery Parameters: \nContent:\nnull\nDomain: null\nPort: null",
            httpRequest.toString());
        httpRequest.content(CONTENT).contentType(CONTENT_TYPE).domain(DOMAIN).method(Method.GET).path(PATH).port(PORT)
            .queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);
        assertEquals(
            "Method: GET\nContent-Type: application/json\nPath: /test/a\nQuery Parameters: key1=value1&key2=value2\nContent:\ncontent\nDomain: localhost\nPort: 8080",
            httpRequest.toString());
    }

    @Test
    public void testCopyConstructor() throws CloneNotSupportedException {

        final FullHttpRequestImpl copyNonInitializedRequest = new FullHttpRequestImpl(httpRequest);

        assertNotSame(httpRequest, copyNonInitializedRequest);
        assertEquals(copyNonInitializedRequest, httpRequest);

        httpRequest.content(CONTENT).contentType(CONTENT_TYPE).domain(DOMAIN).method(Method.GET).path(PATH).port(PORT)
            .queryParameter(QUERY_PARAM_KEY, QUERY_PARAM_VALUE).queryParameter(QUERY_PARAM_KEY2, QUERY_PARAM_VALUE2);

        assertFalse(httpRequest.equals(copyNonInitializedRequest));

        final FullHttpRequestImpl copyInitializedRequest = new FullHttpRequestImpl(httpRequest);
        assertNotSame(httpRequest, copyInitializedRequest);
        assertEquals(copyInitializedRequest, httpRequest);

    }

}
