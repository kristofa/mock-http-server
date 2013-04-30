package com.github.kristofa.test.http;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link HttpResponseProvider} that supports http request with all properties. It supports any http header you define as
 * opposed to {@link SimpleHttpResponseProvider} which only supports Content-Type.
 * 
 * @author kristof
 */
public class DefaultHttpResponseProvider implements HttpResponseProvider {

    private final boolean ignoreAdditionalHeaders;
    private final Map<HttpRequest, HttpResponse> expectedRequests = new HashMap<HttpRequest, HttpResponse>();
    private final Set<HttpRequest> receivedRequests = new HashSet<HttpRequest>();

    /**
     * Creates a new instance.
     * 
     * @param ignoreAdditionalHeaders In some cases you might want to ignore additional http headers and only want to check
     *            some headers. For example sometimes a HTTP client adds custom identification headers you might not be
     *            interested in. If this is the case and you don't necessary want a full match then specific
     *            <code>true</code>. In case you specify <code>false</code> all http headers will be matched.
     */
    public DefaultHttpResponseProvider(final boolean ignoreAdditionalHeaders) {
        this.ignoreAdditionalHeaders = ignoreAdditionalHeaders;
    }

    /**
     * Sets a new request/response.
     * 
     * @param request HttpRequest.
     * @param response Response that should be returned for given request.
     */
    public void set(final HttpRequest request, final HttpResponse response) {
        expectedRequests.put(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request) {

        final HttpResponse httpResponse = expectedRequests.get(request);
        if (ignoreAdditionalHeaders && httpResponse == null) {
            final HttpResponse response = findAndIgnoreAdditionalHeaders(request);
            if (response == null) {
                receivedRequests.add(request);
            }
            return response;
        } else {
            receivedRequests.add(request);
        }
        return httpResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() throws UnsatisfiedExpectationException {
        if (!expectedRequests.keySet().equals(receivedRequests)) {

            final Collection<HttpRequest> missing = new HashSet<HttpRequest>();

            for (final HttpRequest expectedRequest : expectedRequests.keySet()) {
                if (!receivedRequests.contains(expectedRequest)) {
                    missing.add(expectedRequest);
                }
            }

            final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();
            for (final HttpRequest receivedRequest : receivedRequests) {
                if (!expectedRequests.keySet().contains(receivedRequest)) {
                    unexpected.add(receivedRequest);
                }
            }

            throw new UnsatisfiedExpectationException(missing, unexpected);

        }

    }

    private HttpResponse findAndIgnoreAdditionalHeaders(final HttpRequest request) {
        for (final HttpRequest expectedRequest : expectedRequests.keySet()) {
            if (expectedRequest == request) {
                receivedRequests.add(expectedRequest);
                return expectedRequests.get(expectedRequest);
            }
            if (!Arrays.equals(expectedRequest.getContent(), request.getContent())) {
                continue;
            }
            if (expectedRequest.getMethod() != request.getMethod()) {
                continue;
            }
            if (expectedRequest.getPath() == null) {
                if (request.getPath() != null) {
                    continue;
                }
            } else if (!expectedRequest.getPath().equals(request.getPath())) {
                continue;
            }
            if (!expectedRequest.getQueryParameters().equals(request.getQueryParameters())) {
                continue;
            }

            if (!request.getHttpMessageHeaders().containsAll(expectedRequest.getHttpMessageHeaders())) {
                // The input request should at least contain all headers from the expected request.
                continue;
            }
            receivedRequests.add(expectedRequest);
            return expectedRequests.get(expectedRequest);

        }
        return null;
    }

}
