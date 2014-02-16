package com.github.kristofa.test.http;

/**
 * If you have Http requests that have variable content and so don't match exactly you can use a {@link HttpRequestMatcher}
 * that takes into account the variable content.
 * <p/>
 * You can set 1 or more {@link HttpRequestMatcher http request matchers} in a {@link HttpResponseProvider}.
 * 
 * @author kristof
 * @see HttpResponseProvider
 */
public interface HttpRequestMatcher {

    /**
     * Tries to match a new non exact matching httpRequest.
     * 
     * @param originalRequest Original http request which was logged or set by user.
     * @param otherRequest Another request that is not an exact match of original request.
     * @return <code>true</code> in case both requests are same taking into account variable content. <code>false</code> in
     *         case both requests don't match.
     */
    boolean match(final HttpRequest originalRequest, final HttpRequest otherRequest);

    /**
     * Will only be executed in case we find a match using {@link HttpRequestMatcher#match(HttpRequest, HttpRequest)}.
     * <p/>
     * In case the request matches we have the option to return a custom response. For example if the variable content of the
     * request is also present in the response it can be required to adapt the original response. This will not always be the
     * case.
     * 
     * @param originalRequest Original Request which we found matching with matchingRequest.
     * @param originalResponse The original response.
     * @param matchingRequest The matching request.
     * @return New response in case it needs to be changed based on the request. Or in case is does not need to be changed
     *         you should return originalResponse. You should not return <code>null</code>.
     */
    HttpResponse getResponse(final HttpRequest originalRequest, final HttpResponse originalResponse,
        final HttpRequest matchingRequest);

}
