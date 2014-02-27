package com.github.kristofa.test.http;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link HttpRequestMatchingFilter} that will remove http headers in 'other request' in case they are not present in
 * original request.
 * 
 * @author kristof
 */
public class AllExceptOriginalHeadersFilter extends AbstractHttpRequestMatchingFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestMatchingContext filter(final HttpRequestMatchingContext context) {
        final HttpRequest originalRequest = context.originalRequest();
        final HttpRequest otherRequest = context.otherRequest();

        final Set<HttpMessageHeader> originalHttpMessageHeaders = originalRequest.getHttpMessageHeaders();

        final Set<HttpMessageHeader> headersToRemove = new HashSet<HttpMessageHeader>();
        for (final HttpMessageHeader header : otherRequest.getHttpMessageHeaders()) {
            if (!originalHttpMessageHeaders.contains(header)) {
                headersToRemove.add(header);
            }
        }

        if (!headersToRemove.isEmpty()) {
            final HttpRequestImpl otherRequestCopy = new HttpRequestImpl(otherRequest);
            for (final HttpMessageHeader header : headersToRemove) {
                otherRequestCopy.removeHttpMessageHeader(header.getName(), header.getValue());
            }

            return new HttpRequestMatchingContextImpl(originalRequest, otherRequestCopy, context.response());

        }
        return context;

    }

}
