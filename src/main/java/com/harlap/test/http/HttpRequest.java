package com.harlap.test.http;

import java.util.Set;

public interface HttpRequest {

    /**
     * Gets method for request.
     * 
     * @return Method for request.
     */
    Method getMethod();

    /**
     * Gets content type for request.
     * 
     * @return Content type for request.
     */
    String getContentType();

    /**
     * Get content for request.
     * 
     * @return Content for request.
     */
    String getContent();

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

}