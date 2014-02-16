package com.github.kristofa.test.http;

import java.util.Arrays;

/**
 * {@link HttpRequestMatcher} that will match requests in case the new request contains additional http header entries but
 * the other content is equal (same http method, path, query params, content).
 * 
 * @author kristof
 */
public class IgnoreAdditionalHeadersHttpRequestMatcher implements HttpRequestMatcher {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final HttpRequest originalRequest, final HttpRequest otherRequest) {

        if (!Arrays.equals(originalRequest.getContent(), otherRequest.getContent())) {
            return false;
        }
        if (originalRequest.getMethod() != otherRequest.getMethod()) {
            return false;
        }
        if (originalRequest.getPath() == null) {
            if (otherRequest.getPath() != null) {
                return false;
            }
        } else if (!originalRequest.getPath().equals(otherRequest.getPath())) {
            return false;
        }
        if (!originalRequest.getQueryParameters().equals(otherRequest.getQueryParameters())) {
            return false;
        }

        if (!otherRequest.getHttpMessageHeaders().containsAll(originalRequest.getHttpMessageHeaders())) {
            // The new request should at least contain all headers from the expected request.
            return false;
        }
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest originalRequest, final HttpResponse originalResponse,
        final HttpRequest matchingRequest) {
        return originalResponse;
    }

}
