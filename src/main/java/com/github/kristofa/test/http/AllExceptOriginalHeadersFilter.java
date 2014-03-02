package com.github.kristofa.test.http;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link HttpRequestMatchingFilter} that will remove http headers in 'other request' with given key in case headers with
 * same key are not present in original request.
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

        final Set<String> headersToRemove = new HashSet<String>();
        for (final HttpMessageHeader header : otherRequest.getHttpMessageHeaders()) {
            if (originalRequest.getHttpMessageHeaders(header.getName()).isEmpty()) {
                headersToRemove.add(header.getName());
            }
        }

        if (!headersToRemove.isEmpty()) {
            final HttpRequestImpl otherRequestCopy = new HttpRequestImpl(otherRequest);
            for (final String header : headersToRemove) {
                otherRequestCopy.removeHttpMessageHeaders(header);
            }

            return new HttpRequestMatchingContextImpl(originalRequest, otherRequestCopy, context.response());

        }
        return context;

    }

}
