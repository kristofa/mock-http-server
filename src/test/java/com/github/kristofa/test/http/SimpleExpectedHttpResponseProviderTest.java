package com.github.kristofa.test.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class SimpleExpectedHttpResponseProviderTest {

    private final static String PATH = "path";
    private final static String CONTENT_TYPE = "contenType";
    private final static String CONTENT = "content";

    private final static int HTTP_CODE = 200;
    private final static String DATA = "DATA";

    private SimpleExpectedHttpResponseProvider responseProvider;

    @Before
    public void setup() {
        responseProvider = new SimpleExpectedHttpResponseProvider();
    }

    @Test
    public void testExpectMethodStringStringString() {
        responseProvider.expect(Method.GET, PATH, CONTENT_TYPE, CONTENT).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH).content(CONTENT.getBytes())
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNotNull(response);
        assertEquals(HTTP_CODE, response.getHttpCode());
        assertArrayEquals(DATA.getBytes(), response.getContent());
        assertEquals(CONTENT_TYPE, response.getContentType());
    }

    @Test
    public void testExpectMethodString() {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNotNull(response);
        assertEquals(HTTP_CODE, response.getHttpCode());
        assertArrayEquals(DATA.getBytes(), response.getContent());
        assertEquals(CONTENT_TYPE, response.getContentType());
    }

    @Test
    public void testExpectNotFound() {
        responseProvider.expect(Method.GET, PATH, CONTENT_TYPE, CONTENT).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNull(response);
    }

    @Test
    public void testVerifySucces() {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNotNull(response);

        responseProvider.verify();
    }

    @Test
    public void testVerifyMissingAndExtra() {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH).content(CONTENT.getBytes())
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNull(response);

        try {
            responseProvider.verify();
            fail("Expected exception.");
        } catch (final UnsatisfiedExpectationException e) {

            final String expectedExceptionMessage =
                "Missing expected requests: Method: GET\n" + "Message Header: []\n" + "Path: path\n"
                    + "Query Parameters: []\n" + "Content:\n" + "null\n" + "Unexpected received requests: Method: GET\n"
                    + "Message Header: [Content-Type: contenType]\n" + "Path: path\n" + "Query Parameters: []\n"
                    + "Content:\n" + "content";

            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

}
