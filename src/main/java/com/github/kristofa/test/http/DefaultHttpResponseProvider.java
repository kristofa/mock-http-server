package com.github.kristofa.test.http;

/**
 * A {@link HttpResponseProvider} that supports http request with all properties. It supports any http header you define as
 * opposed to {@link SimpleHttpResponseProvider} which only supports Content-Type.
 * 
 * @author kristof
 */
public class DefaultHttpResponseProvider extends AbstractHttpResponseProvider {

    /**
     * Creates a new instance.
     * 
     * @param ignoreAdditionalHeaders In some cases you might want to ignore additional http headers and only want to check
     *            some headers. For example sometimes a HTTP client adds custom identification headers you might not be
     *            interested in. If this is the case and you don't necessary want a full match then specific
     *            <code>true</code>. In case you specify <code>false</code> all http headers will be matched.
     */
    public DefaultHttpResponseProvider(final boolean ignoreAdditionalHeaders) {
        super();
        if (ignoreAdditionalHeaders) {
            addHttpRequestMatchingFilter(new AllExceptOriginalHeadersFilter());
        }
    }

    /**
     * Sets a new request/response.
     * 
     * @param request HttpRequest.
     * @param response Response that should be returned for given request.
     */
    public void set(final HttpRequest request, final HttpResponse response) {

        addExpected(request, new DefaultHttpResponseProxy(response));
    }
    
    /**
     * Reset the response provider to its original state
     */
    public void reset() {
        resetState();
    }

}
