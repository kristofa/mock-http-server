package com.harlap.test.http;

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
     * Get http message headers.
     * 
     * @return Http message headers.
     */
    Set<HttpMessageHeader> getHttpMessageHeaders();

}