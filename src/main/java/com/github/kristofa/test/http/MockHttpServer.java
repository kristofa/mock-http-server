package com.github.kristofa.test.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock Http Server which can be used to return upfront defined responses for a set of http requests.
 *
 * Mock http server will in its default configuration return code 598 in case it receives a request which was not foreseen.
 * In case of an internal exception it will return http returncode 599.
 * 
 * @author kristof
 */
public class MockHttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(MockHttpServer.class);

    public class ExpectationHandler implements Container {

        public ExpectationHandler() {
        }

        @Override
        public void handle(final Request req, final Response response) {

            try {
                final FullHttpRequest receivedFullRequest = RequestConvertor.convert(req);
                // We need to copy it because HttpResponseProvider works with HttpRequest, not with FullHttpRequest.
                // If we did not copy matching would fail.
                final HttpRequest receivedRequest = new HttpRequestImpl(receivedFullRequest);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got request: " + receivedRequest);
                }
                final HttpResponse expectedResponse = responseProvider.getResponse(receivedRequest);

                if (expectedResponse != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Got response for request: " + expectedResponse);
                    }
                    response.setCode(expectedResponse.getHttpCode());
                    if (!StringUtils.isEmpty(expectedResponse.getContentType())) {
                        response.set("Content-Type", expectedResponse.getContentType());
                    }
                    OutputStream body = null;
                    try {
                        body = response.getOutputStream();
                        if (expectedResponse.getContent() != null) {
                            body.write(expectedResponse.getContent());
                        }
                        body.close();
                    } catch (final IOException e) {
                        LOGGER.error("IOException when getting response content.", e);
                    }
                } else {
                    LOGGER.error("Did receive an unexpected request:" + receivedRequest);
                    response.setCode(noMatchFoundResponseCode);
                    response.set("Content-Type", "text/plain;charset=utf-8");
                    PrintStream body;
                    try {
                        body = response.getPrintStream();
                        body.print("Received unexpected request " + receivedRequest);
                        body.close();
                    } catch (final IOException e) {
                        LOGGER.error("IOException when writing response content.", e);
                    }
                }
            } catch (final Exception e) {
                LOGGER.error("Unexpected exception.", e);
                response.setCode(exceptionResponseCode);
                try {
                    response.getPrintStream().close();
                } catch (final IOException e2) {
                    LOGGER.error("IOException when writing response content.", e2);
                }
            }
        }

        public void verify() throws UnsatisfiedExpectationException {
            responseProvider.verify();
        }
    }

    private ExpectationHandler handler;
    private final HttpResponseProvider responseProvider;

    private final int port;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private Connection connection;
    private int connectedPort = -1;

    private int noMatchFoundResponseCode = 598;
    private int exceptionResponseCode = 599;

    /**
     * Creates a new instance.
     * 
     * @param port Port on which mock server should operate. If you provide 0 as port number a free port will be choosen for you. You can get the port through {@link MockHttpServer#getPort()}
     * @param responseProvider {@link HttpResponseProvider}. Should not be <code>null</code>.
     */
    public MockHttpServer(final int port, final HttpResponseProvider responseProvider) {
        Validate.notNull(responseProvider);
        this.port = port;
        this.responseProvider = responseProvider;
    }

    /**
     * Starts the server.
     * 
     * @return Port used by server.
     * @throws IOException In case starting fails.
     */
    public int start() throws IOException {
        handler = new ExpectationHandler();
        connection = new SocketConnection(handler);
        final SocketAddress address = new InetSocketAddress(port);
        final InetSocketAddress connectedAddress = (InetSocketAddress) connection.connect(address);
        connectedPort = connectedAddress.getPort();
        return connectedPort;
    }
    
    /**
     * Return the port used by the server.
     * 
     * @return The port in case the server is successfully started or -1 in case the server has not been started yet.
     */
    public int getPort() {
    	return connectedPort;
    }

    /**
     * Closes the server.
     * 
     * @throws IOException In case closing fails.
     */
    public void stop() throws IOException {
        connection.close();
    }

    /**
     * Verify if we got all requests as expected.
     * 
     * @throws UnsatisfiedExpectationException In case we got unexpected requests or we did not get all requests we expected.
     */
    public void verify() throws UnsatisfiedExpectationException {
        handler.verify();
    }

    /**
     * Allows you to set a custom response code to be returned when no matching response is found.
     *
     * If not set the default code is 598.
     * 
     * @param code HTTP response code to return when no matching response is found.
     */
    public void setNoMatchFoundResponseCode(final int code) {
        noMatchFoundResponseCode = code;
    }

    /**
     * Allows to set a custom response code to be returned when an unexpected exception happens.
     *
     * If not set the default code is 599.
     * 
     * @param code HTTP response code to return when an unexpected exception happens.
     */
    public void setExceptionResponseCode(final int code) {
        exceptionResponseCode = code;
    }

}
