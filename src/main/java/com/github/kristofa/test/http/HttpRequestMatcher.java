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
     * Tries to match given httpRequest.
     * 
     * @param request http request.
     * @return <code>true</code> in case we match given request. <code>false</code> in case we can't match given request.
     */
    boolean match(final HttpRequest request);

    /**
     * In case the request matches we have the option to return a custom response.
     * <p/>
     * For example if the variable content of the request is also present in the response it can be required to adapt the
     * response but this is not always the case.
     * 
     * @param request Request that matched.
     * @param originalResponse The original response
     * @return Custom response in case it needs to be changed based on the request or the original response otherwise.
     */
    HttpResponse getResponse(final HttpRequest request, final HttpResponse originalResponse);

}
