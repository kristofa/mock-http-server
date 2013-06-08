package com.github.kristofa.test.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockHttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(MockHttpServer.class);

    public class ExpectationHandler implements Container {

        public ExpectationHandler() {
        }

        @Override
        public void handle(final Request req, final Response response) {

            byte[] data = null;
            try {
                if (req.getContentLength() > 0) {
                    final InputStream inputStream = req.getInputStream();
                    try {
                        data = IOUtils.toByteArray(inputStream);
                    } finally {
                        inputStream.close();
                    }
                }
            } catch (final IOException e) {
                LOGGER.error("IOException when getting request content.", e);
            }

            final HttpRequestImpl expectedRequest = new HttpRequestImpl();
            expectedRequest.method(Method.valueOf(req.getMethod()));
            expectedRequest.path(req.getPath().getPath());
            expectedRequest.content(data);

            for (final String headerField : req.getNames()) {
                for (final String headerFieldValue : req.getValues(headerField)) {
                    expectedRequest.httpMessageHeader(headerField, headerFieldValue);
                }
            }

            for (final Entry<String, String> entry : req.getQuery().entrySet()) {
                expectedRequest.queryParameter(entry.getKey(), entry.getValue());
            }

            final HttpResponse expectedResponse = responseProvider.getResponse(expectedRequest);

            if (expectedResponse != null) {
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
                response.setCode(noMatchFoundResponseCode);
                response.set("Content-Type", "text/plain;charset=utf-8");
                PrintStream body;
                try {
                    body = response.getPrintStream();
                    body.print("Received unexpected request " + req.getMethod() + ":" + req.getTarget() + " with data: "
                        + data);
                    body.close();
                } catch (final IOException e) {
                    LOGGER.error("IOException when writing response content.", e);
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

    private int noMatchFoundResponseCode = 500;

    /**
     * Creates a new instance.
     * 
     * @param port Port on which mock server should operate.
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
     * @throws Exception In case starting fails.
     */
    public void start() throws Exception {
        handler = new ExpectationHandler();
        connection = new SocketConnection(handler);
        final SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
    }

    /**
     * Closes the server.
     * 
     * @throws Exception In case closing fails.
     */
    public void stop() throws Exception {
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
     * @param code HTTP response code to return when no matching response is found.
     */
    public void setNoMatchFoundResponseCode(final int code) {
        noMatchFoundResponseCode = code;
    }

}
