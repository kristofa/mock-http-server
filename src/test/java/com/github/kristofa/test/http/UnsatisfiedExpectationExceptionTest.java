package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

public class UnsatisfiedExpectationExceptionTest {

    private final static String PATH = "path";
    private final static String CONTENT_TYPE = "contenType";
    private final static String CONTENT = "content";

    @Test(expected = NullPointerException.class)
    public void testUnsatisfiedExpectationExceptionNulObjects() {
        new UnsatisfiedExpectationException(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsatisfiedExpectationExceptionBothCollectionsEmpty() {

        final Collection<HttpRequest> missing = new HashSet<HttpRequest>();
        final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();

        new UnsatisfiedExpectationException(missing, unexpected);
    }

    @Test
    public void testMissingCollectionEmpty() {

        final Collection<HttpRequest> missing = new HashSet<HttpRequest>();
        final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();
        final HttpRequest mockRequest = mock(HttpRequest.class);
        unexpected.add(mockRequest);

        final UnsatisfiedExpectationException e = new UnsatisfiedExpectationException(missing, unexpected);
        assertTrue(e.getMissingHttpRequests().isEmpty());
        assertEquals(1, e.getUnexpectedHttpRequests().size());
        assertEquals(mockRequest, e.getUnexpectedHttpRequests().iterator().next());
    }

    @Test
    public void testUnexpectedCollectionEmpty() {

        final Collection<HttpRequest> missing = new HashSet<HttpRequest>();
        final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();
        final HttpRequest mockRequest = mock(HttpRequest.class);
        missing.add(mockRequest);

        final UnsatisfiedExpectationException e = new UnsatisfiedExpectationException(missing, unexpected);
        assertEquals(1, e.getMissingHttpRequests().size());
        assertEquals(mockRequest, e.getMissingHttpRequests().iterator().next());
        assertTrue(e.getUnexpectedHttpRequests().isEmpty());
    }

    @Test
    public void testToString() {

        final HttpRequestImpl expected = new HttpRequestImpl();
        expected.method(Method.GET).path(PATH);

        final HttpRequestImpl unexpected = new HttpRequestImpl();
        unexpected.method(Method.GET).path(PATH).content(CONTENT.getBytes())
            .httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE);

        final UnsatisfiedExpectationException e =
            new UnsatisfiedExpectationException(Arrays.<HttpRequest>asList(expected), Arrays.<HttpRequest>asList(unexpected));

        final String expectedToStringMessage =
            "Missing expected requests: [Method: GET\n" + "Message Header: []\n" + "Path: path\n" + "Query Parameters: []\n"
                + "Content:\n" + "null]\n" + "Unexpected received requests: [Method: GET\n"
                + "Message Header: [Content-Type: contenType]\n" + "Path: path\n" + "Query Parameters: []\n" + "Content:\n"
                + "content]";

        assertEquals(expectedToStringMessage, e.toString());

    }

}
