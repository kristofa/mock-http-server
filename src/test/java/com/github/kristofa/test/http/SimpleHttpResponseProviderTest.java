package com.github.kristofa.test.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class SimpleHttpResponseProviderTest {

    private final static String PATH = "path";
    private final static String PATH_WITH_PARAMS = "path?a=b&b=c";
    private final static String CONTENT_TYPE = "contenType";
    private final static String CONTENT = "content";

    private final static int HTTP_CODE = 200;
    private final static String DATA = "DATA";

    private SimpleHttpResponseProvider responseProvider;

    @Before
    public void setup() {
        responseProvider = new SimpleHttpResponseProvider();
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
    public void testVerifySuccess() throws UnsatisfiedExpectationException {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH);

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNotNull(response);

        responseProvider.verify();
    }

    @Test
    public void testExpectPathWithQueryParams() throws UnsatisfiedExpectationException {
        responseProvider.expect(Method.GET, PATH_WITH_PARAMS).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH).queryParameter("a", "b").queryParameter("b", "c");

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNotNull(response);
        assertEquals(HTTP_CODE, response.getHttpCode());
        assertArrayEquals(DATA.getBytes(), response.getContent());
        assertEquals(CONTENT_TYPE, response.getContentType());
        responseProvider.verify();
    }

    @Test
    public void testResetResponses() {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(Method.GET).path(PATH);

        responseProvider.reset();

        final HttpResponse response = responseProvider.getResponse(httpRequestImpl);
        assertNull(response);
    }

    @Test
    public void testVerifyMissingAndExtra() {
        responseProvider.expect(Method.GET, PATH).respondWith(HTTP_CODE, CONTENT_TYPE, DATA);

        final HttpRequestImpl unexpectedRequest = new HttpRequestImpl();
        unexpectedRequest.method(Method.GET).path(PATH).content(CONTENT.getBytes())
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE);

        final HttpResponse response = responseProvider.getResponse(unexpectedRequest);
        assertNull(response);

        try {
            responseProvider.verify();
            fail("Expected exception.");
        } catch (final UnsatisfiedExpectationException e) {
            final Collection<HttpRequest> missingHttpRequests = e.getMissingHttpRequests();
            assertEquals(1, missingHttpRequests.size());
            final HttpRequestImpl expectedMissing = new HttpRequestImpl();
            expectedMissing.method(Method.GET).path(PATH);
            assertEquals(expectedMissing, missingHttpRequests.iterator().next());

            final Collection<HttpRequest> unexpectedHttpRequests = e.getUnexpectedHttpRequests();
            assertEquals(1, unexpectedHttpRequests.size());
            assertEquals(unexpectedRequest, unexpectedHttpRequests.iterator().next());

        }
    }

}
