package com.github.kristofa.test.http;

/**
 * Responsible for creating {@link HttpRequestResponseLogger} instances.
 * 
 * @see LoggingHttpProxy
 * @author kristof
 */
public interface HttpRequestResponseLoggerFactory {

    /**
     * Creates a new {@link HttpRequestResponseLogger}. Each request should return a new instance.
     * 
     * @return a New {@link HttpRequestResponseLogger}.
     */
    HttpRequestResponseLogger getHttpRequestResponseLogger();

}
