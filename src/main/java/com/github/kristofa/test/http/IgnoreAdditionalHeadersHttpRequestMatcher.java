package com.github.kristofa.test.http;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

/**
 * {@link HttpRequestMatcher} that is initialized with a {@link HttpRequest} and which will match another request if it is
 * equal or if it has more http headers than the expected request.
 * 
 * @author kristof
 */
public class IgnoreAdditionalHeadersHttpRequestMatcher implements HttpRequestMatcher {

    private final HttpRequest expectedRequest;

    public IgnoreAdditionalHeadersHttpRequestMatcher(final HttpRequest expectedRequest) {
        Validate.notNull(expectedRequest);
        this.expectedRequest = expectedRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final HttpRequest request) {
        if (request == expectedRequest) {
            return true;
        }
        if (!Arrays.equals(expectedRequest.getContent(), request.getContent())) {
            return false;
        }
        if (expectedRequest.getMethod() != request.getMethod()) {
            return false;
        }
        if (expectedRequest.getPath() == null) {
            if (request.getPath() != null) {
                return false;
            }
        } else if (!expectedRequest.getPath().equals(request.getPath())) {
            return false;
        }
        if (!expectedRequest.getQueryParameters().equals(request.getQueryParameters())) {
            return false;
        }

        if (!request.getHttpMessageHeaders().containsAll(expectedRequest.getHttpMessageHeaders())) {
            // The input request should at least contain all headers from the expected request.
            return false;
        }
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request, final HttpResponse originalResponse) {
        return originalResponse;
    }

}
