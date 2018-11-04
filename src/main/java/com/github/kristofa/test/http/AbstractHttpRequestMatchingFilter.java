package com.github.kristofa.test.http;

/**
 * Abstract {@link HttpRequestMatchingFilter} implementation which is able to deal with setting and returning next
 * {@link HttpRequestMatchingFilter} in chain.
 *
 * Filter logic needs to be implemented in extending classes.
 * 
 * @author kristof
 */
public abstract class AbstractHttpRequestMatchingFilter implements HttpRequestMatchingFilter {

    private HttpRequestMatchingFilter nextFilter;

    @Override
    public final void setNext(final HttpRequestMatchingFilter filter) {
        nextFilter = filter;

    }

    @Override
    public final HttpRequestMatchingFilter next() {
        return nextFilter;
    }

}
