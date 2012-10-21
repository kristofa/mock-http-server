package com.harlap.test.http;

/**
 * Logs a {@link HttpRequest} / {@link HttpResponse} combination.
 * 
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
