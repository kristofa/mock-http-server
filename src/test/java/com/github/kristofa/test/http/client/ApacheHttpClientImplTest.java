package com.github.kristofa.test.http.client;

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
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.test.http.FullHttpRequest;
import com.github.kristofa.test.http.HttpMessageHeader;
import com.github.kristofa.test.http.HttpMessageHeaderField;
import com.github.kristofa.test.http.Method;
import com.github.kristofa.test.http.client.ApacheHttpClientImpl;
import com.github.kristofa.test.http.client.GetException;
import com.github.kristofa.test.http.client.HttpClientResponse;
import com.github.kristofa.test.http.client.HttpRequestException;
import com.github.kristofa.test.http.client.PostException;
import com.github.kristofa.test.http.client.PutException;

public class ApacheHttpClientImplTest {

    private static final String RESPONSE_AS_STRING = "ResponseAsString";
    private final static String URL = "http://localhost:8080/myservice";
    private final static String CONTENT_TYPE = "application/json";
    private final static String ENTITY = "{}";

    private org.apache.http.client.HttpClient mockHttpClient;
    private ClientConnectionManager mockConnectionManager;
    private FullHttpRequest mockRequest;
    private ApacheHttpClientImpl serviceInvoker;

    @Before
    public void setUp() throws Exception {
        mockHttpClient = mock(org.apache.http.client.HttpClient.class);
        mockConnectionManager = mock(ClientConnectionManager.class);
        when(mockHttpClient.getConnectionManager()).thenReturn(mockConnectionManager);

        mockRequest = mock(FullHttpRequest.class);
        when(mockRequest.getUrl()).thenReturn(URL);
        final Set<HttpMessageHeader> headers = new HashSet<HttpMessageHeader>();
        headers.add(new HttpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE));
        when(mockRequest.getHttpMessageHeaders()).thenReturn(headers);

        serviceInvoker = new ApacheHttpClientImpl() {

            /* package */@Override
            org.apache.http.client.HttpClient getClient() {
                return mockHttpClient;
            }

        };
    }

    @Test
    public void testGetSuccess() throws ClientProtocolException, IOException, HttpRequestException {
        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        when(mockRequest.getMethod()).thenReturn(Method.GET);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.execute(mockRequest);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testGetThrowsIOException() throws ClientProtocolException, IOException, HttpRequestException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpGet.class))).thenThrow(ioException);

        try {
            when(mockRequest.getMethod()).thenReturn(Method.GET);
            serviceInvoker.execute(mockRequest);
            fail("Expected exception.");
        } catch (final GetException e) {
            assertEquals(ioException, e.getCause());

        }

        verify(mockConnectionManager).shutdown();
        verifyNoMoreInteractions(mockConnectionManager);

    }

    @Test
    public void testPutSuccesWithNoEntity() throws ClientProtocolException, IOException, HttpRequestException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPut.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        when(mockRequest.getMethod()).thenReturn(Method.PUT);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.execute(mockRequest);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostSucces() throws ClientProtocolException, IOException, HttpRequestException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        when(mockRequest.getMethod()).thenReturn(Method.POST);
        when(mockRequest.getContent()).thenReturn(ENTITY.getBytes());
        final HttpClientResponse<InputStream> responseObject = serviceInvoker.execute(mockRequest);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        assertTrue(responseObject.success());

        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPutThrowsIOException() throws ClientProtocolException, IOException, HttpRequestException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpPut.class))).thenThrow(ioException);

        try {
            when(mockRequest.getMethod()).thenReturn(Method.PUT);
            when(mockRequest.getContent()).thenReturn(ENTITY.getBytes());
            serviceInvoker.execute(mockRequest);
            fail("Expected exception.");
        } catch (final PutException e) {
            assertEquals(ioException, e.getCause());
        }
        verify(mockConnectionManager).shutdown(); // In case of an exception we should shutdown the connection manager.
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostNonOkStatusCode() throws ClientProtocolException, IOException, HttpRequestException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_SERVICE_UNAVAILABLE);
        final ByteArrayInputStream responseStream = new ByteArrayInputStream(new String(RESPONSE_AS_STRING).getBytes());
        when(mockHttpEntity.getContent()).thenReturn(responseStream);

        when(mockRequest.getMethod()).thenReturn(Method.POST);
        when(mockRequest.getContent()).thenReturn(ENTITY.getBytes());
        final HttpClientResponse<InputStream> responseObject = serviceInvoker.execute(mockRequest);
        assertNotNull(responseObject);
        assertFalse(responseObject.success());
        assertEquals("Got HTTP return code " + HttpStatus.SC_SERVICE_UNAVAILABLE, responseObject.getErrorMessage());
        assertEquals(responseStream, responseObject.getResponseEntity());
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testPostThrowsIOException() throws ClientProtocolException, IOException, HttpRequestException {

        final IOException ioException = new IOException();
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(ioException);

        try {
            when(mockRequest.getMethod()).thenReturn(Method.POST);
            when(mockRequest.getContent()).thenReturn(ENTITY.getBytes());
            serviceInvoker.execute(mockRequest);
            fail("Expected exception.");
        } catch (final PostException e) {
            assertEquals(ioException, e.getCause());
        }
        verify(mockConnectionManager).shutdown(); // In case of an exception we should shutdown the connection manager.
        verifyNoMoreInteractions(mockConnectionManager);
    }

    @Test
    public void testDeleteSuccess() throws ClientProtocolException, IOException, HttpRequestException {
        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        final HttpEntity mockHttpEntity = mock(HttpEntity.class);
        final StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpClient.execute(any(HttpDelete.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        when(mockRequest.getMethod()).thenReturn(Method.DELETE);

        final HttpClientResponse<InputStream> responseObject = serviceInvoker.execute(mockRequest);
        assertNotNull(responseObject);
        assertNull(responseObject.getErrorMessage());
        assertNull(responseObject.getResponseEntity());
        assertTrue(responseObject.success());

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
