package com.github.kristofa.test.http;

/**
 * Logs a {@link HttpRequest} / {@link HttpResponse} combination.
 * <p>
 * Used with {@link LoggingHttpProxy}.
 * 
 * @see LoggingHttpProxy
 * @author kristof
 */
public interface HttpRequestResponseLogger {

    /**
     * Logs a {@link HttpRequest} / {@link HttpResponse}.
     * 
     * @param request Request.
     * @param response Response.
     */
    void log(HttpRequest request, HttpResponse response);

}
