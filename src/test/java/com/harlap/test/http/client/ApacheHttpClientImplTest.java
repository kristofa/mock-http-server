package com.harlap.test.http.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

public class ApacheHttpClientImplTest {

    private static final String RESPONSE_AS_STRING = "ResponseAsString";
    private final static String URL = "http://localhost:8080/myservice";
    private final static String CONTENT_TYPE = "application/json";
    private final static String ENTITY = "{}";
    private final static String PARAMETER_NAME_1 = "Name 1";
    private final static String PARAMETER_VALUE_1 = "Value 1";
    private final static String PARAMETER_NAME_2 = "Name 2";
    private final static String PARAMETER_VALUE_2 = "Value 2";

    private org.apache.http.client.HttpClient mockHttpClient;
    private ClientConnectionManager mockConnectionManager;
    private ApacheHttpClientImpl serviceInvoker;

    @Before
    public void setUp() throws Exception {
        mockHttpClient = mock(org.apache.http.client.HttpClient.class);
        mockConnectionManager = mock(ClientConnectionManager.class);
        when(mockHttpClient.getConnectionManager()).thenReturn(mockConnectionManager);

        serviceInvoker = new ApacheHttpClientImpl() {

            /* package */@Override
            org.apache.http.client.HttpClient getClient() {
                return mockHttpClient;
            }

        };
    }

    @Test
    public void testGetSuccess() throws ClientProtocolException, IOException, GetException {
        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.get(URL, CONTENT_TYPE);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testGetSuccessWithParameters() throws ClientProtocolException, IOException, GetException {
        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        final HttpClientResponse<InputStream> responseObject =
            serviceInvoker.get(URL, CONTENT_TYPE, new NameValuePair(PARAMETER_NAME_1, PARAMETER_VALUE_1), new NameValuePair(
                PARAMETER_NAME_2, PARAMETER_VALUE_2));
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testGetThrowsIOException() throws ClientProtocolException, IOException, GetException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpGet.class))).thenThrow(ioException);

        try {
            serviceInvoker.get(URL, CONTENT_TYPE);
            fail("Expected exception.");
        } catch (final GetException e) {
            assertEquals(ioException, e.getCause());

        }

        verify(mockConnectionManager).shutdown();
        verifyNoMoreInteractions(mockConnectionManager);

    }

    @Test
    public void testPutSuccesWithNoEntity() throws ClientProtocolException, IOException, PutException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPut.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.put(URL, CONTENT_TYPE, null);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostSucces() throws PostException, ClientProtocolException, IOException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.post(URL, CONTENT_TYPE, ENTITY);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPutThrowsIOException() throws PutException, ClientProtocolException, IOException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpPut.class))).thenThrow(ioException);

        try {
            serviceInvoker.put(URL, CONTENT_TYPE, ENTITY);
            fail("Expected exception.");
        } catch (final PutException e) {
            assertEquals(ioException, e.getCause());
        }
        verify(mockConnectionManager).shutdown(); // In case of an exception we should shutdown the connection manager.
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostNonOkStatusCode() throws PostException, ClientProtocolException, IOException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_SERVICE_UNAVAILABLE);
        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.post(URL, CONTENT_TYPE, ENTITY);
        assertNotNull(responseObject);
        assertFalse(responseObject.success());
        assertEquals("Got HTTP return code " + HttpStatus.SC_SERVICE_UNAVAILABLE, responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostThrowsIOException() throws PostException, ClientProtocolException, IOException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(ioException);

        try {
            serviceInvoker.post(URL, CONTENT_TYPE, ENTITY);
            fail("Expected exception.");
        } catch (final PostException e) {
            assertEquals(ioException, e.getCause());
        }
        verify(mockConnectionManager).shutdown(); // In case of an exception we should shutdown the connection manager.
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testGetClient() {
        final ApacheHttpClientImpl httpServiceInvoker = new ApacheHttpClientImpl();
        final org.apache.http.client.HttpClient client = httpServiceInvoker.getClient();
        assertNotNull(client);
        client.getConnectionManager().shutdown();
    }

}
