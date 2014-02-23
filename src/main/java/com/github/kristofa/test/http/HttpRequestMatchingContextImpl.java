package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;

/**
 * {@link HttpRequestMatchingContext} implementation.
 * 
 * @author kristof
 */
public class HttpRequestMatchingContextImpl implements HttpRequestMatchingContext {

    private final HttpRequest originalRequest;
    private final HttpRequest otherRequest;
    private final HttpResponse response;

    /**
     * Create a new instance.
     * 
     * @param originalRequest Should not be <code>null</code>.
     * @param otherRequest Should not be <code>null</code>.
     * @param response Should not be <code>null</code>.
     */
    public HttpRequestMatchingContextImpl(final HttpRequest originalRequest, final HttpRequest otherRequest,
        final HttpResponse response) {
        Validate.notNull(originalRequest);
        Validate.notNull(otherRequest);
        Validate.notNull(response);
        this.originalRequest = originalRequest;
        this.otherRequest = otherRequest;
        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest originalRequest() {
        return originalRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest otherRequest() {
        return otherRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse response() {
        return response;
    }

}
