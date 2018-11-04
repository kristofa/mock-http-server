package com.github.kristofa.test.http;

import com.github.kristofa.test.http.file.FileHttpResponseProvider;

/**
 * A {@link HttpRequestMatchingFilter} contains logic to deal with expected differences between 2 requests and brings them
 * in-line so that they can potentially match.
 *
 * If we have a request that contains a http header entry that can differ from request to request, for example a user token,
 * the {@link HttpRequestMatchingFilter} can create copies of both input requests and remove the header entry of both or set
 * a common value so they can match.
 *
 * Http Request Matching Filters can be chained. Each filter working on the output of the previous one. This is implemented
 * in {@link AbstractHttpResponseProvider}.
 * 
 * @see AbstractHttpResponseProvider
 * @see FileHttpResponseProvider
 * @see AllExceptContentTypeHeaderFilter
 * @see AllExceptOriginalHeadersFilter
 * @author kristof
 */
public interface HttpRequestMatchingFilter {

    /**
     * Filter/transform input http requests and response if needed.
     * 
     * @param context Input context. Contains either original request, non matching other request and original response OR
     *            context modified by {@link HttpRequestMatchingFilter} earlier in the chain.
     * @return Same or modified context.
     */
    HttpRequestMatchingContext filter(final HttpRequestMatchingContext context);

    /**
     * {@link HttpRequestMatchingFilter Http request match filters} can be chained.
     * 
     * @param filter Set next instance in chain.
     */
    void setNext(final HttpRequestMatchingFilter filter);

    /**
     * Get next instance in chain.
     * 
     * @return Next instance in chain. Can be <code>null</code>.
     */
    HttpRequestMatchingFilter next();

}
