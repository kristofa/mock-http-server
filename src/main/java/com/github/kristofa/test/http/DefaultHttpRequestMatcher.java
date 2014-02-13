package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;

/**
 * {@link HttpRequestMatcher} that expects an exact match for a given request.
 * 
 * @author kristof
 */
public class DefaultHttpRequestMatcher implements HttpRequestMatcher {

    private final HttpRequest request;

    public DefaultHttpRequestMatcher(final HttpRequest request) {
        Validate.notNull(request);
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final HttpRequest request) {
        return this.request.equals(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request, final HttpResponse response) {
        return response;
    }

}
