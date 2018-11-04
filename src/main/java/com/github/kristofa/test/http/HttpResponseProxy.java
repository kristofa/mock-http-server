package com.github.kristofa.test.http;

/**
 * Http responses can be large and they are only needed when the corresponding http request matched. So we don't need and
 * don't want to keep all responses for expected http requests in memory.
 *
 * This proxy knows how to fetch and build the http response but does not necessary keep it in memory. It can fetch the
 * response lazily.
 *
 * It also keeps track if the response has already been consumed or not. In that way we can support different responses for
 * same request and we can check if we have the exact amount of expected calls for a request.
 * 
 * @author kristof
 * @see AbstractHttpResponseProvider
 */
public interface HttpResponseProxy {

    /**
     * Indicates if the HttpResponse has already been consumed.
     * 
     * @return <code>true</code> in case {@link HttpResponseProxy#consume()} has already been called before or
     *         <code>false</code> in case it has not been called yet.
     */
    boolean consumed();

    /**
     * Gets HttpResponse.
     * 
     * @return HttpResponse.
     */
    HttpResponse getResponse();

    /**
     * Consumes HttpResponse.
     * 
     * @return HttpResponse.
     */
    HttpResponse consume();

}
