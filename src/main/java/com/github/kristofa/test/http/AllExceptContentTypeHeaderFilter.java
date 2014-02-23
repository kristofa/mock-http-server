package com.github.kristofa.test.http;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link HttpRequestMatchingFilter} that will remove all http headers except Content-Type from original and other http
 * request.
 * 
 * @author kristof
 */
public class AllExceptContentTypeHeaderFilter extends AbstractHttpRequestMatchingFilter {

    private final static String CONTENTTYPE_HEADER_NAME = "Content-Type";

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestMatchingContext filter(final HttpRequestMatchingContext context) {
        final HttpRequest originalRequest = context.originalRequest();
        final HttpRequest newOriginal = filter(originalRequest);

        final HttpRequest otherRequest = context.otherRequest();
        final HttpRequest newOther = filter(otherRequest);

        if (originalRequest != newOriginal || otherRequest != newOther) {
            return new HttpRequestMatchingContextImpl(newOriginal, newOther, context.response());
        }
        return context;
    }

    private HttpRequest filter(final HttpRequest request) {
        final Set<HttpMessageHeader> httpMessageHeaders = request.getHttpMessageHeaders();
        if (httpMessageHeaders.isEmpty()) {
            return request;
        }

        if (httpMessageHeaders.size() == 1 && request.getHttpMessageHeaders(CONTENTTYPE_HEADER_NAME).size() == 1) {
            return request;
        }

        final HttpRequestImpl copy = new HttpRequestImpl(request);
        final Set<HttpMessageHeader> headersToRemove = new HashSet<HttpMessageHeader>();

        for (final HttpMessageHeader header : copy.getHttpMessageHeaders()) {
            if (!header.getName().equals(CONTENTTYPE_HEADER_NAME)) {
                headersToRemove.add(header);
            }
        }

        for (final HttpMessageHeader headerToRemove : headersToRemove) {
            copy.removeHttpMessageHeader(headerToRemove.getName(), headerToRemove.getValue());
        }
        return copy;

    }
}
