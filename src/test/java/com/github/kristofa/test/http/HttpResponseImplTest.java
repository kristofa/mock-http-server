package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HttpResponseImplTest {

    private final static int HTTP_RESPONSE_CODE = 200;
    private final static String CONTENT_TYPE = "application/json; charset=UTF-8";
    private final static byte[] CONTENT = new String("content").getBytes();

    private HttpResponseImpl response;
    private HttpResponseImpl responseNoContentAndType;

    @Before
    public void setup() {
        response = new HttpResponseImpl(HTTP_RESPONSE_CODE, CONTENT_TYPE, CONTENT);
        responseNoContentAndType = new HttpResponseImpl(HTTP_RESPONSE_CODE, null, null);
    }

    @Test
    public void testHashCode() {
        final HttpResponseImpl equalResponse = new HttpResponseImpl(HTTP_RESPONSE_CODE, CONTENT_TYPE, CONTENT);
        assertEquals(response.hashCode(), equalResponse.hashCode());
    }

    @Test
    public void testGetHttpCode() {
        assertEquals(HTTP_RESPONSE_CODE, response.getHttpCode());
        assertEquals(HTTP_RESPONSE_CODE, responseNoContentAndType.getHttpCode());
    }

    @Test
    public void testGetContentType() {
        assertEquals(CONTENT_TYPE, response.getContentType());
        assertNull(responseNoContentAndType.getContentType());
    }

    @Test
    public void testGetContent() {
        assertEquals(CONTENT, response.getContent());
        assertNull(responseNoContentAndType.getContent());
    }

    @Test
    public void testToString() {
        assertEquals("Http code: " + HTTP_RESPONSE_CODE + ", Content Type: " + CONTENT_TYPE + ", Content: "
            + new String(CONTENT), response.toString());
        assertEquals("Http code: " + HTTP_RESPONSE_CODE + ", Content Type: null, Content: null",
            responseNoContentAndType.toString());
    }

    @Test
    public void testEqualsObject() {
        assertFalse(response.equals(null));
        assertFalse(response.equals(new String()));
        assertTrue(response.equals(response));
        assertFalse(response.equals(responseNoContentAndType));

        final HttpResponseImpl equalResponse = new HttpResponseImpl(HTTP_RESPONSE_CODE, CONTENT_TYPE, CONTENT);
        assertTrue(response.equals(equalResponse));

    }

}
