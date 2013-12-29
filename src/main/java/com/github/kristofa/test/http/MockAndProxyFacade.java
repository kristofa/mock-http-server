package com.github.kristofa.test.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

/**
 * Facade that lets you easily configure and use either the {@link LoggingHttpProxy} for using real services and logging
 * requests/responses or the {@link MockHttpServer} for running your code against mocked http requests/responses. </p> You
 * can use {@link MockHttpServer} or {@link LoggingHttpProxy} by themselves but if you use both it is most likely that this
 * class will make things easier for you.
 * 
 * @author kristof
 */
public class MockAndProxyFacade {

    /**
     * Specifies the mode in which {@link MockAndProxyFacade} should be operating.
     * 
     * @author kristof
     */
    public static enum Mode {
        MOCKING, LOGGING
    };

    public static class Builder {

        private static final int MINPORT = 1;
        private static final int MAXPORT = 65535;

        private int port;
        private HttpResponseProvider responseProvider;
        private final Collection<ForwardHttpRequestBuilder> requestBuilders = new ArrayList<ForwardHttpRequestBuilder>();
        private HttpRequestResponseLoggerFactory loggerFactory;
        private Mode mode;

        /**
         * Sets the port that will be used for either {@link MockHttpServer} or {@link LoggingHttpProxy}.
         * 
         * @param port Port.
         * @return Builder.
         */
        public Builder port(final int port) {
            Validate.inclusiveBetween(MINPORT, MAXPORT, port);
            this.port = port;
            return this;
        }

        /**
         * Sets the {@link HttpResponseProvider} that will be used for {@link MockHttpServer}.
         * 
         * @param responseProvider {@link HttpResponseProvider}. Should not be <code>null</code>.
         * @return Builder.
         */
        public Builder httpResponseProvider(final HttpResponseProvider responseProvider) {
            Validate.notNull(responseProvider);
            this.responseProvider = responseProvider;
            return this;
        }

        /**
         * Adds a {@link ForwardHttpRequestBuilder} that will be used with {@link LoggingHttpProxy}.
         * 
         * @param requestBuilder {@link ForwardHttpRequestBuilder}. Should not be <code>null</code>.
         * @return Builder.
         */
        public Builder addForwardHttpRequestBuilder(final ForwardHttpRequestBuilder requestBuilder) {
            Validate.notNull(requestBuilder);
            requestBuilders.add(requestBuilder);
            return this;
        }

        /**
         * Sets the {@link HttpRequestResponseLoggerFactory} that will be used with {@link LoggingHttpProxy}.
         * 
         * @param loggerFactory {@link HttpRequestResponseLoggerFactory}. Should not be <code>null</code>.
         * @return Builder.
         */
        public Builder httpRequestResponseLoggerFactory(final HttpRequestResponseLoggerFactory loggerFactory) {
            Validate.notNull(loggerFactory);
            this.loggerFactory = loggerFactory;
            return this;
        }

        /**
         * Sets the Mode in which the {@link MockAndProxyFacade} should operate.
         * 
         * @param mode Operation mode. Should not be <code>null</code>.
         * @return Builder.
         */
        public Builder mode(final Mode mode) {
            Validate.notNull(mode);
            this.mode = mode;
            return this;

        }

        public MockAndProxyFacade build() {
            return new MockAndProxyFacade(this);
        }
    }

    private final Mode mode;
    private MockHttpServer mockServer;
    private LoggingHttpProxy proxy;

    private MockAndProxyFacade(final Builder builder) {
        Validate.notNull(builder.mode, "You should have set mode in builder!");
        mode = builder.mode;
        Validate.isTrue(builder.port > 0, "You should have set port in builder!");
        if (Mode.MOCKING.equals(mode)) {
            Validate.notNull(builder.responseProvider,
                "You should have set a HttpResponseProvider in builder when using Mocking mode!");
            mockServer = new MockHttpServer(builder.port, builder.responseProvider);
        } else {
            // Logging
            Validate.notNull(builder.requestBuilders,
                "You should have added a ForwardHttpRequestBuilder when using Logging mode!");
            Validate.notNull(builder.loggerFactory,
                "You should have set a HttpRequestResponseLoggerFactory when using Logging mode!");
            proxy = new LoggingHttpProxy(builder.port, builder.requestBuilders, builder.loggerFactory);
        }
    }

    /**
     * Starts either {@link MockHttpServer} or {@link LoggingHttpProxy} depending on operation mode.
     * 
     * @throws IOException In case starting fails.
     */
    public void start() throws IOException {
        if (Mode.MOCKING.equals(mode)) {
            mockServer.start();
        } else {
            proxy.start();
        }
    }

    /**
     * Stops either {@link MockHttpServer} or {@link LoggingHttpProxy} depending on operation mode.
     * 
     * @throws IOException In case stopping fails.
     */
    public void stop() throws IOException {
        if (Mode.MOCKING.equals(mode)) {
            mockServer.stop();
        } else {
            proxy.stop();
        }
    }

    /**
     * In case we are in Mocking operation mode we will verify if all expected requests have been invoked. </p> In case we
     * are in Logging operation mode nothing will be checked.
     * 
     * @throws UnsatisfiedExpectationException In case we got unexpected requests and/or not all expected requests were
     *             invoked.
     */
    public void verify() throws UnsatisfiedExpectationException {
        if (Mode.MOCKING.equals(mode)) {
            mockServer.verify();
        }
    }
}
