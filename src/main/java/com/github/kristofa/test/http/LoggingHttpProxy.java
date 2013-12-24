package com.github.kristofa.test.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kristofa.test.http.client.ApacheHttpClientImpl;
import com.github.kristofa.test.http.client.HttpClient;
import com.github.kristofa.test.http.client.HttpClientResponse;
import com.github.kristofa.test.http.client.HttpRequestException;

/**
 * Http proxy that supports logging requests/reponses. Its purpose is to be a 'man in the middle' which can be used to
 * capture request/responses that can be mocked later on for testing purposes. It forwards requests it gets to another
 * service and returns the result of that service unmodified to the requester. While doing that it also allows logging
 * request/response pairs.
 * <p>
 * Those persisted request/response pairs can be mocked by {@link MockHttpServer}.
 * <p>
 * Using the {@link LoggingHttpProxy} to persist request/responses and using them with the {@link MockHttpServer} is
 * especially useful for complex responses that are not that easy to mock by hand. It allows building a <a
 * href="http://googletesting.blogspot.be/2012/10/hermetic-servers.html">hermetic server</a>.
 * <p>
 * The {@link LoggingHttpProxy} will return following HTTP return codes when things go wrong:
 * <ul>
 * <li>570: We could not build a forward request for input request. Missing of faulty {@link ForwardHttpRequestBuilder}.
 * <li>571: Forward request failed. Forward URL invalid?
 * <li>572: Copying response of forwarding request failed.
 * <li>573: Unknown exception.
 * </ul>
 * The body of the response will contain the error message.
 * 
 * @author kristof
 */
public class LoggingHttpProxy {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingHttpProxy.class);

    private final int port;
    private final Collection<ForwardHttpRequestBuilder> requestBuilders = new HashSet<ForwardHttpRequestBuilder>();
    private final HttpRequestResponseLoggerFactory loggerFactory;
    private Connection connection;
    private ProxyImplementation proxy;

    private class ProxyImplementation implements Container {

        private static final int UNKNOWN_EXCEPTION_HTTP_CODE = 573;
        private static final int FORWARD_REQUEST_FAILED_HTTP_CODE = 571;
        private static final int COPY_RESPONSE_FAILED_ERROR_HTTP_CODE = 572;
        private static final int NO_FORWARD_REQUEST_ERROR_HTTP_CODE = 570;
        private static final String CONTENT_TYPE = "Content-Type";

        public ProxyImplementation() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final Request request, final Response response) {

            try {
                final FullHttpRequest httpRequest = FullHttpRequestBuilder.build(request);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Received request: " + httpRequest);
                }

                FullHttpRequest forwardHttpRequest = null;
                for (final ForwardHttpRequestBuilder forwardRequestBuilder : requestBuilders) {
                    forwardHttpRequest = forwardRequestBuilder.getForwardRequest(httpRequest);
                    if (forwardHttpRequest != null) {
                        break;
                    }
                }

                if (forwardHttpRequest == null) {
                    LOGGER.error("Got unexpected request: " + httpRequest);
                    errorResponse(response, NO_FORWARD_REQUEST_ERROR_HTTP_CODE, "Received unexpected request:\n"
                        + httpRequest.toString());
                } else {

                    LOGGER.debug("Logging request.");

                    final HttpRequestResponseLogger logger = loggerFactory.getHttpRequestResponseLogger();
                    logger.log(httpRequest);
                    try {
                        LOGGER.debug("Forward request.");
                        final HttpClientResponse<InputStream> forwardResponse = forward(forwardHttpRequest);
                        LOGGER.debug("Got response for forward request.");
                        try {
                            final InputStream inputStream = forwardResponse.getResponseEntity();
                            byte[] responseEntity;
                            try {
                                // This is tricky as we keep the full response in memory... reason is that we need to copy it
                                // twice.
                                // Once to return to response, another time to log.
                                responseEntity = IOUtils.toByteArray(inputStream);
                            } finally {
                                inputStream.close();
                            }
                            final HttpResponse httpResponse =
                                new HttpResponseImpl(forwardResponse.getHttpCode(), forwardResponse.getContentType(),
                                    responseEntity);
                            LOGGER.debug("Logging response");
                            logger.log(httpResponse);

                            response.setCode(forwardResponse.getHttpCode());
                            response.set(CONTENT_TYPE, forwardResponse.getContentType());
                            final OutputStream outputStream = response.getOutputStream();
                            try {
                                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseEntity);
                                IOUtils.copy(byteArrayInputStream, outputStream);
                                byteArrayInputStream.close();
                            } finally {
                                outputStream.close();
                            }

                        } catch (final IOException e) {
                            LOGGER.error("IOException when trying to copy response of forward request.", e);
                            errorResponse(response, COPY_RESPONSE_FAILED_ERROR_HTTP_CODE, "Exception when copying streams."
                                + e.getMessage());
                        } finally {
                            forwardResponse.close();
                        }

                    } catch (final HttpRequestException e) {
                        LOGGER.error("HttpRequestException when forwarding request.", e);
                        errorResponse(response, FORWARD_REQUEST_FAILED_HTTP_CODE,
                            "Exception when forwarding request." + e.getMessage());
                    }
                }
            } catch (final Exception e) {
                LOGGER.error("Exception.", e);
                errorResponse(response, UNKNOWN_EXCEPTION_HTTP_CODE, "Exception: " + e.getMessage());
            }

        }

        private HttpClientResponse<InputStream> forward(final FullHttpRequest request) throws HttpRequestException {
            final HttpClient client = new ApacheHttpClientImpl();
            return client.execute(request);
        }

        private void errorResponse(final Response response, final int httpCode, final String message) {
            response.setCode(httpCode);
            response.set(CONTENT_TYPE, "text/plain;charset=utf-8");
            PrintStream body;
            try {
                body = response.getPrintStream();
                body.print(message);
                body.close();
            } catch (final IOException e) {
                throw new IllegalStateException("Exception when building response.", e);
            }
        }

    }

    /**
     * Create a new instance.
     * 
     * @param port Port at which proxy will be running.
     * @param requestBuilders Forward request builders. Should not be <code>null</code> and at least 1 should be specified.
     * @param loggerFactory Request/Response logger factory.. Should not be <code>null</code>.
     */
    public LoggingHttpProxy(final int port, final Collection<ForwardHttpRequestBuilder> requestBuilders,
        final HttpRequestResponseLoggerFactory loggerFactory) {
        Validate.isTrue(requestBuilders != null && !requestBuilders.isEmpty(),
            "At least 1 ForwardHttpRequestBuilder should be provided.");
        Validate.notNull(loggerFactory, "HttpRequestResponseLoggerFactory should not be null.");
        this.port = port;
        this.requestBuilders.addAll(requestBuilders);
        this.loggerFactory = loggerFactory;
    }

    /**
     * Starts proxy.
     * 
     * @throws Exception In case starting fails.
     */
    public void start() throws IOException {
        // Close existing connection if it exists.
        if (connection != null) {
            connection.close();
        }

        proxy = new ProxyImplementation();
        connection = new SocketConnection(proxy);
        final SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
        LOGGER.debug("Started on port: " + port);
    }

    /**
     * Stops proxy.
     * 
     * @throws IOException In case closing connection fails.
     */
    public void stop() throws IOException {
        LOGGER.debug("Stopping and closing connection.");
        connection.close();
    }
}
