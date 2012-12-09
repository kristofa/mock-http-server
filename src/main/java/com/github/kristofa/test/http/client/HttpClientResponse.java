package com.github.kristofa.test.http.client;

/**
 * Http Response as being returned by a {@link HttpClient}. Provides the user with the HTTP response code, response entity
 * and optional error message.
 * <p>
 * IMPORTANT: Call {@link HttpClientResponse#close()} method when done with process response. This will clean-up resources.
 * 
 * @author kristof
 * @param <T> Type for response entity.
 */
public interface HttpClientResponse<T> {

    /**
     * Indicates if result of http request was successful. Response was successful in case there is no error message.
     * 
     * @return <code>true</code> in case request was successful, false in case request failed.
     */
    boolean success();

    /**
     * Gets the HTTP return code.
     * 
     * @return the httpCode
     */
    int getHttpCode();

    /**
     * In case request failed this will return the error message.
     * 
     * @return the error message. Will be <code>null</code> in case request was successful.
     */
    String getErrorMessage();

    /**
     * Gets response object as a result of executing http request.
     * 
     * @return the response Response as a result of executing http request.
     */
    T getResponseEntity();

    /**
     * Closes all resources related to this request/response.
     */
    void close();

    /**
     * Get Content-Type.
     * 
     * @return Content-Type.
     */
    String getContentType();

}