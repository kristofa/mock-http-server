package com.github.kristofa.test.http;

import java.util.Set;

/**
 * Contains the HTTP request properties required to identify or compare a HTTP request.
 * 
 * @author kristof
 */
public interface HttpRequest {

    /**
     * Gets method for request.
     * 
     * @return Method for request.
     */
    Method getMethod();

    /**
     * Get content for request.
     * 
     * @return Content for request.
     */
    byte[] getContent();

    /**
     * Gets path for request without query parameters.
     * 
     * @return Path for request.
     */
    String getPath();

    /**
     * Gets query parameters for request.
     * 
     * @return Query parameters for request.
     */
    Set<QueryParameter> getQueryParameters();

    /**
     * Gets query parameters with given key.
     * 
     * @param key Query parameter key. Should not be <code>null</code> or blank.
     * @return Query parameters with given key.
     */
    Set<QueryParameter> getQueryParameters(final String key);

    /**
     * Get http message headers.
     * 
     * @return Http message headers.
     */
    Set<HttpMessageHeader> getHttpMessageHeaders();

    /**
     * Get http message headers with given name/key.
     * 
     * @param name Name/key. Should not be <code>null</code> or blank.
     * @return Http message headers with given name/key.
     */
    Set<HttpMessageHeader> getHttpMessageHeaders(final String name);

}