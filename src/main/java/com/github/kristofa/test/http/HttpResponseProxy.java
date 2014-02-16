package com.github.kristofa.test.http;

/**
 * Http responses can be large and they are only needed when the corresponding http request matched. So we don't need and
 * don't want to keep all responses for expected http requests in memory.
 * <p/>
 * This proxy knows how to fetch and build the http response but does not necessary keep it in memory. It can fetch the
 * response lazily.
 * <p/>
 * It also keeps track if the response has already been requested before In that way we can avoid that same response is
 * returned multiple times and we can support different responses for same request.
 * 
 * @author kristof
 * @see AbstractHttpResponseProvider
 */
public interface HttpResponseProxy {

    /**
     * Indicates if the HttpResponse has already been requested before.
     * 
     * @return <code>true</code> in case {@link HttpResponseProxy#getResponse()} has already been called before or
     *         <code>false</code> in case it has not been called yet.
     */
    boolean alreadyRequested();

    /**
     * Gets HttpResponse.
     * 
     * @return HttpResponse.
     */
    HttpResponse getResponse();

}
