package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.github.kristofa.test.http.MockAndProxyFacade.Builder;
import com.github.kristofa.test.http.MockAndProxyFacade.Mode;
import com.github.kristofa.test.http.file.FileHttpResponseProvider;
import com.github.kristofa.test.http.file.HttpRequestResponseFileLoggerFactory;

/**
 * Integration test that shows the usage of {@link MockAndProxyFacade}. </p> It first executes some requests after starting
 * {@link MockAndProxyFacade} in {@link Mode#LOGGING} mode. The requests will get logged to disk. Next it executes the same
 * requests but in {@link Mode#MOCKING} mode so the earlier persisted responses will be returned.
 * <p/>
 * It also assures the integration between {@link MockHttpServer} and {@link LoggingHttpProxy}.
 * 
 * @author kristof
 */
public class ITMockAndProxyFacade {

    private static final String LOGGING_FILE_NAME = "ITMockAndProxyFacade";
    private static final String LOGGING_DIRECTORY = "target/";
    private final static int SERVICE_PORT = 51234;
    private final static int MOCK_PROXY_PORT = 51235;

    private static final String MOCK_PROXY_URL = "http://localhost:" + MOCK_PROXY_PORT;

    @Test
    public void testLoggingAndMocking() throws IOException, UnsatisfiedExpectationException {

        // First log requests.
        logging();
        // Next use logged requests to repla them.
        mocking();

    }

    private void logging() throws IOException {

        /*
         * We start a separate instance of MockHttpServer that acts as our destination service.
         */
        final SimpleHttpResponseProvider responseProvider = new SimpleHttpResponseProvider();
        responseProvider.expect(Method.GET, "/service/a").respondWith(200, "text/plain", "ABCD1234");
        responseProvider.expect(Method.GET, "/service/b?c=d&e=f").respondWith(200, "text/plain", "EFG1234");
        // This is no mistake. We will call this request twice so we also need to expect it twice.
        responseProvider.expect(Method.GET, "/service/b?c=d&e=f").respondWith(200, "text/plain", "EFG1234");
        responseProvider.expect(Method.POST, "/service/a", "application/json; charset=UTF-8", "{}").respondWith(201, null,
            null);
        final MockHttpServer service = new MockHttpServer(SERVICE_PORT, responseProvider);
        service.start();

        try {

            final MockAndProxyFacade loggingFacade = buildFacade(Mode.LOGGING);
            loggingFacade.start();
            try {

                executeAndVerifyRequests();

            } finally {
                loggingFacade.stop();
            }
        } finally {
            service.stop();
        }
    }

    private void mocking() throws IOException, UnsatisfiedExpectationException {
        final MockAndProxyFacade mockingFacade = buildFacade(Mode.MOCKING);
        mockingFacade.start();
        try {
            executeAndVerifyRequests();
            mockingFacade.verify(); // Verify that we got all and only the requests we expected.
        } finally {
            mockingFacade.stop();
        }
    }

    private void executeAndVerifyRequests() throws ClientProtocolException, IOException {
        final HttpClient httpClient = new DefaultHttpClient();
        try {

            final HttpGet req1 = new HttpGet(MOCK_PROXY_URL + "/service/a");
            final HttpResponse response1 = httpClient.execute(req1);
            assertEquals(200, response1.getStatusLine().getStatusCode());
            assertEquals("ABCD1234", IOUtils.toString(response1.getEntity().getContent()));

            final HttpGet req2 = new HttpGet(MOCK_PROXY_URL + "/service/b?c=d&e=f");
            final HttpResponse response2 = httpClient.execute(req2);
            assertEquals(200, response2.getStatusLine().getStatusCode());
            assertEquals("EFG1234", IOUtils.toString(response2.getEntity().getContent()));

            final HttpPost req3 = new HttpPost(MOCK_PROXY_URL + "/service/a");
            req3.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
            final HttpResponse response3 = httpClient.execute(req3);
            assertEquals(201, response3.getStatusLine().getStatusCode());
            EntityUtils.consumeQuietly(response3.getEntity());

            // Same as request 2 but query parameters in different order.
            final HttpGet req4 = new HttpGet(MOCK_PROXY_URL + "/service/b?e=f&c=d");
            final HttpResponse response4 = httpClient.execute(req4);
            assertEquals(200, response4.getStatusLine().getStatusCode());
            assertEquals("EFG1234", IOUtils.toString(response4.getEntity().getContent()));

        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private MockAndProxyFacade buildFacade(final Mode mode) {
        final Builder builder = new Builder();
        return builder
            .mode(mode)
            .addForwardHttpRequestBuilder(new PassthroughForwardHttpRequestBuilder("localhost", SERVICE_PORT))
            .httpRequestResponseLoggerFactory(new HttpRequestResponseFileLoggerFactory(LOGGING_DIRECTORY, LOGGING_FILE_NAME))
            .port(MOCK_PROXY_PORT).httpResponseProvider(new FileHttpResponseProvider(LOGGING_DIRECTORY, LOGGING_FILE_NAME))
            .build();
    }
}
