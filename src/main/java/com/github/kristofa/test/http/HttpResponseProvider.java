package com.github.kristofa.test.http;

/**
 * Provides responses for given {@link HttpRequest http requests}. Abstracts where the responses come from.
 * <p>
 * Used with {@link MockHttpServer}.
 * 
 * @see MockHttpServer
 * @author kristof
 */
public interface HttpResponseProvider {

    /**
     * Gets expected response for given request.
     * 
     * @param request HttpRequest.
     * @return Response or <code>null</code> in case we don't know given request.
     */
    HttpResponse getResponse(final HttpRequest request);

    /**
     * Adds a {@link HttpRequestMatcher}. A matcher can be added if you want to take into account variable content in some of
     * the requests. By default HttpResponseProviders will only exactly match requests. You can however provide custom
     * matchers which take into account the variable content and still match.
     * 
     * @param matcher {@link HttpRequestMatcher}
     */
    void addMatcher(final HttpRequestMatcher matcher);

    /**
     * Should be executed when all requests have been submitted. Checks if all expected requests have been requested.
     * 
     * @throws UnsatisfiedExpectationException In case expectation were not as expected. See
     *             {@link UnsatisfiedExpectationException#getMissingHttpRequests()} and
     *             {@link UnsatisfiedExpectationException#getUnexpectedHttpRequests()} to get missing and unexpected
     *             requests.
     */
    void verify() throws UnsatisfiedExpectationException;

}
