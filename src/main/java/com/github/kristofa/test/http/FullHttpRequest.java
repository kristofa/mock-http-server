package com.github.kristofa.test.http;

/**
 * A Full HTTP Request extends a {@link HttpRequest} with domain (host), port. We are interested in those properties when we
 * need to forward requests.
 * 
 * @see ForwardHttpRequestBuilder
 * @see LoggingHttpProxy
 * @author kristof
 */
public interface FullHttpRequest extends HttpRequest {

    /**
     * Gets domain / host for request.
     * 
     * @return Gets domain for request.
     */
    String getDomain();

    /**
     * Gets port for request.
     * 
     * @return Gets port for request.
     */
    Integer getPort();

    /**
     * Gets the url for the http request, including query parameters if defined.
     * 
     * @return Url for http request. The URL should be encoded so it can be submitted to a http server.
     */
    String getUrl();

}
