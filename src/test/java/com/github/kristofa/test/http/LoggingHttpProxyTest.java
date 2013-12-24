package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class LoggingHttpProxyTest {

    private final static int PROXY_PORT = 51234;
    private final String PROXY_URL = "http://localhost:" + PROXY_PORT;
    private final static int PORT = 51233;

    private LoggingHttpProxy proxy;
    private MockHttpServer server;
    private HttpClient client;
    private HttpRequestResponseLoggerFactory mockLoggerFactory;
    private HttpRequestResponseLogger mockLogger;
    private SimpleHttpResponseProvider responseProvider;

    @Before
    public void setup() throws Exception {

        final ForwardHttpRequestBuilder forwardHttpRequestBuilder = new ForwardHttpRequestBuilder() {

            @Override
            public FullHttpRequest getForwardRequest(final FullHttpRequest request) {
                final FullHttpRequestImpl forwardRequest = new FullHttpRequestImpl(request);
                forwardRequest.port(PORT);
                forwardRequest.domain("localhost");
                return forwardRequest;
            }
        };

        mockLoggerFactory = mock(HttpRequestResponseLoggerFactory.class);
        mockLogger = mock(HttpRequestResponseLogger.class);
        when(mockLoggerFactory.getHttpRequestResponseLogger()).thenReturn(mockLogger);

        proxy = new LoggingHttpProxy(PROXY_PORT, Arrays.asList(forwardHttpRequestBuilder), mockLoggerFactory);
        proxy.start();

        responseProvider = new SimpleHttpResponseProvider();
        server = new MockHttpServer(PORT, responseProvider);
        server.start();

        client = new DefaultHttpClient();
    }

    @After
    public void tearDown() throws Exception {
        proxy.stop();
        server.stop();
        client.getConnectionManager().shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullForwardRequestBuilder() {
        new LoggingHttpProxy(PROXY_PORT, null, mockLoggerFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noForwardRequestBuilder() {

        final Collection<ForwardHttpRequestBuilder> emptyCollection = Collections.emptyList();
        new LoggingHttpProxy(PROXY_PORT, emptyCollection, mockLoggerFactory);
    }

    @Test(expected = NullPointerException.class)
    public void nullRequestResponseLogger() {

        final ForwardHttpRequestBuilder mockRequestBuilder = mock(ForwardHttpRequestBuilder.class);
        new LoggingHttpProxy(PROXY_PORT, Arrays.asList(mockRequestBuilder), null);
    }

    @Test
    public void successfulForwardRequestTest() throws ClientProtocolException, IOException {

        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

        final HttpGet req = new HttpGet(PROXY_URL + "/");
        final HttpResponse response = client.execute(req);
        final String responseBody = IOUtils.toString(response.getEntity().getContent());
        final int statusCode = response.getStatusLine().getStatusCode();

        // Then the response is "OK"
        assertEquals("OK", responseBody);
        // And the status code is 200
        assertEquals(200, statusCode);

        final FullHttpRequestImpl expectedRequest = new FullHttpRequestImpl();
        expectedRequest.method(Method.GET);
        expectedRequest.path("/");
        expectedRequest.httpMessageHeader("Connection", "Keep-Alive");
        expectedRequest.httpMessageHeader("Host", "localhost:51234");
        expectedRequest.httpMessageHeader("User-Agent", "Apache-HttpClient/4.2.5 (java 1.5)");
        expectedRequest.port(-1);

        final HttpResponseImpl expectedResponse = new HttpResponseImpl(200, "text/plain", "OK".getBytes());

        final InOrder inOrder = inOrder(mockLoggerFactory, mockLogger);
        inOrder.verify(mockLoggerFactory).getHttpRequestResponseLogger();
        inOrder.verify(mockLogger).log(expectedRequest);
        inOrder.verify(mockLogger).log(expectedResponse);
        verifyNoMoreInteractions(mockLogger, mockLoggerFactory, mockLogger);

    }

}
