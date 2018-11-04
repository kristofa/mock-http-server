package com.github.kristofa.test.http;

/**
 * Context for matching a http request.
 *
 * If contains a request and another request and a response. Both requests did originally not equal.
 *
 * {@link HttpRequestMatchingContext Http Request matching contexts} can change through out the chain of
 * {@link HttpRequestMatchingFilter http request matching filters} that are executed.
 * 
 * @see HttpRequestMatchingFilter
 * @see AbstractHttpResponseProvider
 * @author kristof
 */
public interface HttpRequestMatchingContext {

    /**
     * A request that was expected.
     * 
     * @return a request that we expected to receive.
     */
    HttpRequest originalRequest();

    /**
     * Another request that we received and that initially did not match original request.
     * 
     * @return another request.
     */
    HttpRequest otherRequest();

    /**
     * Initial response which can have been modified in the meanwhile.
     * 
     * @return response.
     */
    HttpResponse response();

}
