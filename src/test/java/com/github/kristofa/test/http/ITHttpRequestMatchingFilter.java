package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test that shows that chaining of {@link HttpRequestMatchingFilter http request matching filters} works.
 * 
 * @author kristof
 */
public class ITHttpRequestMatchingFilter {

    private final static String DATE_TIME_PARAM = "datetime";
    private final static String ID_PARAM = "id";
    private static final int PORT = 51235;
    private static final String MOCK_URL = "http://localhost:" + PORT;

    private MockHttpServer server;
    private DefaultHttpResponseProvider responseProvider;

    @Before
    public void setup() throws IOException {
        responseProvider = new DefaultHttpResponseProvider(true);
        // Set our custom filters.
        responseProvider.addHttpRequestMatchingFilter(new DateTimeHttpRequestMatchingFilter());
        responseProvider.addHttpRequestMatchingFilter(new IdHttpRequestMatchingFilter());
        server = new MockHttpServer(PORT, responseProvider);
        server.start();
    }

    @After
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void testChainedHttpRequestMatchingFilters() throws ClientProtocolException, IOException {
        final UUID origId = UUID.randomUUID();
        final HttpRequestImpl request = new HttpRequestImpl();
        request.method(Method.POST).path("/service").httpMessageHeader(DATE_TIME_PARAM, new Date().toString())
            .httpMessageHeader(ID_PARAM, origId.toString());
        final HttpResponseImpl response = new HttpResponseImpl(200, "text/plain", origId.toString().getBytes());
        responseProvider.set(request, response);

        final UUID newId = UUID.randomUUID();
        final HttpPost post = new HttpPost(MOCK_URL + "/service");
        post.addHeader(DATE_TIME_PARAM, new Date(20334).toString());
        post.addHeader(ID_PARAM, newId.toString());
        final HttpClient httpClient = new DefaultHttpClient();
        try {
            final HttpResponse receivedResponse = httpClient.execute(post);
            assertEquals(200, receivedResponse.getStatusLine().getStatusCode());
            assertEquals("Expected new id to be returned as response.", newId.toString(),
                IOUtils.toString(receivedResponse.getEntity().getContent()));
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testShouldNotMatch() throws ClientProtocolException, IOException {
        final UUID origId = UUID.randomUUID();
        final HttpRequestImpl request = new HttpRequestImpl();
        request.method(Method.POST).path("/service").httpMessageHeader(DATE_TIME_PARAM, new Date().toString())
            .httpMessageHeader(ID_PARAM, origId.toString()).httpMessageHeader("otherHeader", "otherValue");
        final HttpResponseImpl response = new HttpResponseImpl(200, "text/plain", origId.toString().getBytes());
        responseProvider.set(request, response);

        final UUID newId = UUID.randomUUID();
        final HttpPost post = new HttpPost(MOCK_URL + "/service");
        post.addHeader(DATE_TIME_PARAM, new Date(20334).toString());
        post.addHeader(ID_PARAM, newId.toString());
        final HttpClient httpClient = new DefaultHttpClient();
        try {
            final HttpResponse receivedResponse = httpClient.execute(post);
            assertEquals(598, receivedResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(receivedResponse.getEntity());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Filter that removes date/time header entry from requests.
     * 
     * @author kristof
     */
    public class DateTimeHttpRequestMatchingFilter extends AbstractHttpRequestMatchingFilter {

        @Override
        public HttpRequestMatchingContext filter(final HttpRequestMatchingContext context) {
            final HttpRequest copyOriginal = removeDateTime(context.originalRequest());
            final HttpRequest copyOther = removeDateTime(context.otherRequest());

            if (copyOriginal != context.originalRequest() && copyOther != context.otherRequest()) {
                return new HttpRequestMatchingContextImpl(copyOriginal, copyOther, context.response());
            }
            return context;

        }

        private HttpRequest removeDateTime(final HttpRequest request) {
            final Set<HttpMessageHeader> headers = request.getHttpMessageHeaders(DATE_TIME_PARAM);
            if (!headers.isEmpty()) {
                final HttpRequestImpl copy = new HttpRequestImpl(request);
                copy.removeHttpMessageHeaders(DATE_TIME_PARAM);
                return copy;
            }
            return request;
        }
    }

    /**
     * Filter that removes an id header from the request but also updates the response to return the id header value that was
     * received as input.
     * 
     * @author kristof
     */
    public class IdHttpRequestMatchingFilter extends AbstractHttpRequestMatchingFilter {

        @Override
        public HttpRequestMatchingContext filter(final HttpRequestMatchingContext context) {

            String otherId;

            final Set<HttpMessageHeader> httpMessageHeaders = context.originalRequest().getHttpMessageHeaders(ID_PARAM);
            if (!httpMessageHeaders.isEmpty()) {
                final Set<HttpMessageHeader> otherHeaders = context.otherRequest().getHttpMessageHeaders(ID_PARAM);
                if (!otherHeaders.isEmpty()) {
                    otherId = otherHeaders.iterator().next().getValue();

                    final HttpRequestImpl copyOrig = new HttpRequestImpl(context.originalRequest());
                    copyOrig.removeHttpMessageHeaders(ID_PARAM);
                    final HttpRequestImpl copyOther = new HttpRequestImpl(context.otherRequest());
                    copyOther.removeHttpMessageHeaders(ID_PARAM);

                    final HttpResponseImpl newResponse =
                        new HttpResponseImpl(context.response().getHttpCode(), context.response().getContentType(),
                            new String(otherId.toString()).getBytes());
                    return new HttpRequestMatchingContextImpl(copyOrig, copyOther, newResponse);

                }
            }
            return context;

        }

    }

}
