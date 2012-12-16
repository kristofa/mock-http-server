package com.github.kristofa.test.http;

/**
 * Logs a {@link HttpRequest} / {@link HttpResponse} combination. Used with {@link LoggingHttpProxy}.
 * <p>
 * Each instance of {@link HttpRequestResponseLogger} is responsible for logging 1 request/response.
 * <p>
 * Request will be logged just before executing request. Response will be logged as soon as response is received. This also
 * allows to log of forwarded request.
 * 
 * @see LoggingHttpProxy
 * @author kristof
 */
public interface HttpRequestResponseLogger {

    /**
     * Logs a {@link HttpRequest}.
     * 
     * @param request Request.
     */
    void log(HttpRequest request);

    /**
     * Logs {@link HttpResponse}.
     * 
     * @param response Response.
     */
    void log(HttpResponse response);

}
