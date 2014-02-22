package com.github.kristofa.test.http;

import java.util.Arrays;
import java.util.Set;

/**
 * {@link HttpRequestMatcher} that matches requests if following is equal:
 * <ul>
 * <li>HTTP Method (GET, PUT, POST, DELETE)</li>
 * <li>Path</li>
 * <li>Query parameters</li>
 * <li>Content-Type http header</li>
 * <li>Request Entity</li>
 * </ul>
 * So it only matches Content-Type http header and will ignore any other headers.
 * 
 * @author kristof
 */
public class SimpleHttpRequestMatcher implements HttpRequestMatcher {

    private final static String CONTENTTYPE_HEADER_NAME = "Content-Type";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final HttpRequest originalRequest, final HttpRequest otherRequest) {
        if (!originalRequest.getMethod().equals(otherRequest.getMethod())) {
            return false;
        }
        if (!originalRequest.getPath().equals(otherRequest.getPath())) {
            return false;
        }
        if (!originalRequest.getQueryParameters().equals(otherRequest.getQueryParameters())) {
            return false;
        }
        final Set<HttpMessageHeader> orig = originalRequest.getHttpMessageHeaders(CONTENTTYPE_HEADER_NAME);
        final Set<HttpMessageHeader> other = otherRequest.getHttpMessageHeaders(CONTENTTYPE_HEADER_NAME);
        if (!orig.equals(other)) {
            return false;
        }

        if (!Arrays.equals(originalRequest.getContent(), otherRequest.getContent())) {
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
