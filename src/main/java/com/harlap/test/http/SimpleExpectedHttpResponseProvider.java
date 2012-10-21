package com.harlap.test.http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link ExpectedHttpResponseProvider} that keeps expected request/responses in memory.
 * <p>
 * Simple to use for not too complex request/responses.
 * 
 * @author kristof
 */
public class SimpleExpectedHttpResponseProvider implements ExpectedHttpResponseProvider {

    private HttpRequestImpl latestRequest;

    private final Map<HttpRequest, HttpResponse> expectedRequests = new HashMap<HttpRequest, HttpResponse>();
    private final Set<HttpRequest> receivedRequests = new HashSet<HttpRequest>();

    /**
     * Provide an expected request with content.
     * 
     * @param method HTTP method.
     * @param path Path.
     * @param contentType Content type.
     * @param data Data, content.
     * @return current {@link SimpleExpectedHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleExpectedHttpResponseProvider expect(final Method method, final String path, final String contentType,
        final String data) {
        latestRequest = new HttpRequestImpl();
        latestRequest.method(method).path(path).content(data).contentType(contentType);
        return this;
    }

    /**
     * Provide an expected request without content.
     * 
     * @param method HTTP method.
     * @param path Path.
     * @return current {@link SimpleExpectedHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleExpectedHttpResponseProvider expect(final Method method, final String path) {
        latestRequest = new HttpRequestImpl();
        latestRequest.method(method).path(path);
        return this;
    }

    /**
     * Provide expected response for latest given request.
     * 
     * @param httpCode Http response code.
     * @param contentType Content type.
     * @param data Data.
     * @return current {@link SimpleExpectedHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleExpectedHttpResponseProvider respondWith(final int httpCode, final String contentType, final String data) {
        final HttpResponseImpl response = new HttpResponseImpl(httpCode, contentType, data == null ? null : data.getBytes());
        expectedRequests.put(latestRequest, response);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request) {
        receivedRequests.add(request);
        return expectedRequests.get(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() throws UnsatisfiedExpectationException {

        if (!expectedRequests.keySet().equals(receivedRequests)) {
            String missingExpectedRequestsString = "Missing expected requests: ";

            for (final HttpRequest expectedRequest : expectedRequests.keySet()) {
                if (!receivedRequests.contains(expectedRequest)) {
                    missingExpectedRequestsString += expectedRequest.toString();
                }
            }

            String unexpectedReceivedRequestsString = "Unexpected received requests: ";
            for (final HttpRequest receivedRequest : receivedRequests) {
                if (!expectedRequests.keySet().contains(receivedRequest)) {
                    unexpectedReceivedRequestsString += receivedRequest.toString();
                }
            }

            throw new UnsatisfiedExpectationException(missingExpectedRequestsString + "\n"
                + unexpectedReceivedRequestsString);

        }

    }

}
