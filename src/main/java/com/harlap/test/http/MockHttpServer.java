/**
 * Copyright 2011 <jharlap@gitub.com> Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.harlap.test.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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

            String data = null;
            try {
                if (req.getContentLength() > 0) {
                    data = req.getContent();
                }
            } catch (final IOException e) {
                LOGGER.error("IOException when getting request content.", e);
            }

            final HttpRequestImpl expectedRequeset = new HttpRequestImpl();
            expectedRequeset.method(Method.valueOf(req.getMethod())).path(req.getTarget()).content(data);
            if (req.getContentType() != null) {
                expectedRequeset.contentType(req.getContentType().toString());
            }

            final HttpResponse expectedResponse = responseProvider.getResponse(expectedRequeset);

            if (expectedResponse != null) {
                response.setCode(expectedResponse.getHttpCode());
                response.set("Content-Type", expectedResponse.getContentType());
                OutputStream body = null;
                try {
                    body = response.getOutputStream();
                    body.write(expectedResponse.getContent());
                    body.close();
                } catch (final IOException e) {
                    LOGGER.error("IOException when getting response content.", e);
                }
            } else {
                response.setCode(500);
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

        public void verify() {
            responseProvider.verify();

        }
    }

    private ExpectationHandler handler;
    private final ExpectedHttpResponseProvider responseProvider;

    private final int port;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private Connection connection;

    /**
     * Creates a new instance.
     * 
     * @param port Port on which mock server should operate.
     * @param responseProvider {@link ExpectedHttpResponseProvider}. Should not be <code>null</code>.
     */
    public MockHttpServer(final int port, final ExpectedHttpResponseProvider responseProvider) {
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
     */
    public void verify() {
        handler.verify();
    }

}
