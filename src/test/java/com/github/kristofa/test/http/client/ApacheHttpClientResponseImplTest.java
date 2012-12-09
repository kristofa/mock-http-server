package com.github.kristofa.test.http.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.test.http.client.ApacheHttpClientResponseImpl;
import com.github.kristofa.test.http.client.HttpClientResponse;

public class HttpClientResponseTest {

    private final static int HTTP_RESPONSE_CODE = 201;
    private final static int HTTP_RESPONSE_CODE_2 = 200;
    private final static Integer RESPONSE_ENTITY = new Integer(1000);
    private final static Integer RESPONSE_ENTITY_2 = new Integer(1001);
    private final static String ERROR_MESSAGE = "Error message.";
    private final static String ERROR_MESSAGE_2 = "Error message 2.";

    private HttpClient mockClient;

    @Before
    public void setUp() throws Exception {
        mockClient = mock(HttpClient.class);
    }

    @Test
    public void testHttpServiceInvokerResponseIntHttpClient() {
        final HttpClientResponse<Integer> response =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        assertEquals(HTTP_RESPONSE_CODE, response.getHttpCode());
        assertNull(response.getErrorMessage());
        assertNull(response.getResponseEntity());
        assertTrue(response.success());
    }

    @Test(expected = NullPointerException.class)
    public void testHttpServiceInvokerResponseIntNullHttpClient() {
        new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, null);

    }

    @Test
    public void testSetErrorMessage() {
        final ApacheHttpClientResponseImpl<Integer> response =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        assertNull(response.getErrorMessage());
        assertTrue(response.success());
        response.setErrorMessage(ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, response.getErrorMessage());
        assertFalse(response.success());
    }

    @Test
    public void testSetResponse() {
        final ApacheHttpClientResponseImpl<Integer> response =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        assertNull(response.getResponseEntity());
        response.setResponseEntity(RESPONSE_ENTITY);
        assertEquals(RESPONSE_ENTITY, response.getResponseEntity());
    }

    @Test
    public void testEquals() {
        final ApacheHttpClientResponseImpl<Integer> response =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        response.setResponseEntity(RESPONSE_ENTITY);
        assertTrue(response.equals(response));
        assertFalse(response.equals(new String()));
        assertFalse(response.equals(null));

        final ApacheHttpClientResponseImpl<Integer> responseWithErrorMessage =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        responseWithErrorMessage.setErrorMessage(ERROR_MESSAGE);
        assertFalse(response.equals(responseWithErrorMessage));
        final ApacheHttpClientResponseImpl<Integer> responseWithErrorMessage2 =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        responseWithErrorMessage2.setErrorMessage(ERROR_MESSAGE_2);
        assertFalse(responseWithErrorMessage.equals(responseWithErrorMessage2));
        assertFalse("Without and with response entity object.", responseWithErrorMessage.equals(response));

        final ApacheHttpClientResponseImpl<Integer> responseDifferentHttpResponseCode =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE_2, mockClient);
        responseDifferentHttpResponseCode.setResponseEntity(RESPONSE_ENTITY);
        assertFalse(response.equals(responseDifferentHttpResponseCode));

        final ApacheHttpClientResponseImpl<Integer> response2 =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        response2.setResponseEntity(RESPONSE_ENTITY_2);
        assertFalse(response.equals(response2));

        final ApacheHttpClientResponseImpl<Integer> equalResponse =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        equalResponse.setResponseEntity(RESPONSE_ENTITY);
        assertTrue(response.equals(equalResponse));

        final ApacheHttpClientResponseImpl<Integer> equalResponseWithErrorMessage =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        equalResponseWithErrorMessage.setErrorMessage(ERROR_MESSAGE);
        assertTrue(responseWithErrorMessage.equals(equalResponseWithErrorMessage));
    }

    @Test
    public void testHashCode() {
        final ApacheHttpClientResponseImpl<Integer> response =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        response.setResponseEntity(RESPONSE_ENTITY);
        final ApacheHttpClientResponseImpl<Integer> equalResponse =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        equalResponse.setResponseEntity(RESPONSE_ENTITY);
        assertEquals(response.hashCode(), equalResponse.hashCode());

        final ApacheHttpClientResponseImpl<Integer> responseWithErrorMessage =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        responseWithErrorMessage.setErrorMessage(ERROR_MESSAGE);
        final ApacheHttpClientResponseImpl<Integer> equalResponseWithErrorMessage =
            new ApacheHttpClientResponseImpl<Integer>(HTTP_RESPONSE_CODE, mockClient);
        equalResponseWithErrorMessage.setErrorMessage(ERROR_MESSAGE);
        assertEquals(responseWithErrorMessage.hashCode(), equalResponseWithErrorMessage.hashCode());

    }

}
