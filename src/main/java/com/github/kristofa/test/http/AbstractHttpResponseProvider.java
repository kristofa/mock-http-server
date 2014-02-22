package com.github.kristofa.test.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.kristofa.test.http.file.FileHttpResponseProvider;

/**
 * Abstract {@link HttpResponseProvider} that contains the basic functionality any HttpResponseProvider should have:
 * <ul>
 * <li>Exactly matching HttpRequests</li>
 * <li>In case of non exact match use submitted {@link HttpRequestMatcher http request matchers} to perform matching.</li>
 * <li>Support multiple times the same request with potentially different responses that are returned in a fixed order.
 * </ul>
 * <p/>
 * If you create your own {@link HttpResponseProvider} it is probably a good idea to extend this class.
 * 
 * @author kristof
 * @see DefaultHttpResponseProvider
 * @see FileHttpResponseProvider
 */
public abstract class AbstractHttpResponseProvider implements HttpResponseProvider {

    private final Map<HttpRequest, List<HttpResponseProxy>> requestMap = new HashMap<HttpRequest, List<HttpResponseProxy>>();
    private final Collection<HttpRequestMatcher> matchers = new ArrayList<HttpRequestMatcher>();
    private final List<HttpRequest> unexpectedRequests = new ArrayList<HttpRequest>();
    private boolean initialized = false;

    /**
     * Adds an expected HttpRequest and response proxy combination.
     * 
     * @param request Expected http request.
     * @param responseProxy Response proxy which gives us access to http response.
     */
    protected final void addExpected(final HttpRequest request, final HttpResponseProxy responseProxy) {
        List<HttpResponseProxy> list = requestMap.get(request);
        if (list == null) {
            list = new ArrayList<HttpResponseProxy>();
            requestMap.put(request, list);
        }
        list.add(responseProxy);
    }

    /**
     * Override this method if you want to lazily initialize requests/responses.
     * <p/>
     * This method will be called with the first call to {@link AbstractHttpResponseProvider#getResponse(HttpRequest)}.
     * <p/>
     * You can initialize expected requests and responses by calling
     * {@link AbstractHttpResponseProvider#addExpected(HttpRequest, HttpResponseProxy)}.
     */
    protected void lazyInitializeExpectedRequestsAndResponses() {

    }

    /**
     * Clear expected request/responses as well as already received unexpected requests.
     * <p/>
     * Allows re-use for new test without having to recreate instance.
     */
    protected final void resetState() {
        requestMap.clear();
        unexpectedRequests.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized HttpResponse getResponse(final HttpRequest request) {

        if (!initialized) {
            lazyInitializeExpectedRequestsAndResponses();
            initialized = true;
        }

        final HttpResponse responseForExactMatchingRequest = getFirstNotYetReturnedResponseFor(request);
        if (responseForExactMatchingRequest != null) {
            return responseForExactMatchingRequest;
        }

        for (final HttpRequestMatcher matcher : matchers) {
            for (final HttpRequest originalRequest : requestMap.keySet()) {
                if (matcher.match(originalRequest, request)) {
                    final HttpResponse originalResponse = getFirstNotYetReturnedResponseFor(originalRequest);
                    if (originalResponse != null) {
                        return matcher.getResponse(originalRequest, originalResponse, request);
                    }
                }
            }
        }
        unexpectedRequests.add(request);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addMatcher(final HttpRequestMatcher matcher) {
        matchers.add(matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void verify() throws UnsatisfiedExpectationException {
        final Collection<HttpRequest> missingRequests = new ArrayList<HttpRequest>();
        for (final Entry<HttpRequest, List<HttpResponseProxy>> entry : requestMap.entrySet()) {
            for (final HttpResponseProxy responseProxy : entry.getValue()) {
                if (responseProxy.alreadyRequested() == false) {
                    missingRequests.add(entry.getKey());
                }
            }
        }

        if (!unexpectedRequests.isEmpty() || !missingRequests.isEmpty()) {
            throw new UnsatisfiedExpectationException(missingRequests, unexpectedRequests);
        }

    }

    private HttpResponse getFirstNotYetReturnedResponseFor(final HttpRequest request) {
        final List<HttpResponseProxy> list = requestMap.get(request);
        if (list != null) {
            for (final HttpResponseProxy proxy : list) {
                if (!proxy.alreadyRequested()) {
                    return proxy.getResponse();
                }
            }
        }
        return null;
    }

}
