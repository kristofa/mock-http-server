package com.github.kristofa.test.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A {@link HttpResponseProvider} that supports http request with all properties. It supports any http header you define as
 * opposed to {@link SimpleHttpResponseProvider} which only supports Content-Type.
 * 
 * @author kristof
 */
public class DefaultHttpResponseProvider implements HttpResponseProvider {

    private final boolean ignoreAdditionalHeaders;
    private final List<Pair<Pair<HttpRequestMatcher, HttpRequest>, HttpResponse>> requestsAndResponses =
        new ArrayList<Pair<Pair<HttpRequestMatcher, HttpRequest>, HttpResponse>>();
    private final Set<HttpRequest> receivedRequests = new HashSet<HttpRequest>();
    private final Set<HttpRequest> expectedRequests = new HashSet<HttpRequest>();
    private final Collection<HttpRequestMatcher> customMatchers = new ArrayList<HttpRequestMatcher>();

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
        expectedRequests.add(request);
        HttpRequestMatcher matcher = null;

        for (final HttpRequestMatcher customMatcher : customMatchers) {
            if (customMatcher.match(request)) {
                matcher = customMatcher;
                break;
            }
        }

        if (matcher == null) {
            if (ignoreAdditionalHeaders) {
                matcher = new IgnoreAdditionalHeadersHttpRequestMatcher(request);
            } else {
                matcher = new DefaultHttpRequestMatcher(request);
            }

        }
        requestsAndResponses.add(Pair.of(Pair.of(matcher, request), response));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request) {

        for (final Pair<Pair<HttpRequestMatcher, HttpRequest>, HttpResponse> pair : requestsAndResponses) {
            if (pair.getLeft().getLeft().match(request)) {
                receivedRequests.add(pair.getLeft().getRight());
                return pair.getLeft().getLeft().getResponse(request, pair.getRight());
            }
        }
        receivedRequests.add(request);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() throws UnsatisfiedExpectationException {
        if (!expectedRequests.equals(receivedRequests)) {

            final Collection<HttpRequest> missing = new HashSet<HttpRequest>();

            for (final HttpRequest expectedRequest : expectedRequests) {
                if (!receivedRequests.contains(expectedRequest)) {
                    missing.add(expectedRequest);
                }
            }

            final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();
            for (final HttpRequest receivedRequest : receivedRequests) {
                if (!expectedRequests.contains(receivedRequest)) {
                    unexpected.add(receivedRequest);
                }
            }

            throw new UnsatisfiedExpectationException(missing, unexpected);

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMatcher(final HttpRequestMatcher matcher) {
        customMatchers.add(matcher);
    }

}
