package com.harlap.test.http.client;

import java.io.InputStream;

/**
 * A higher level HTTP Client interface that abstracts the low level library that is being used.
 * <p>
 * We only added GET, PUT, POST as we only needed support for these request types for now.
 * 
 * @author kristof
 */
public interface HttpClient {

    /**
     * Invokes a GET call to given URL and returns the response as InputStream.
     * 
     * @param url Url to which to execute request.
     * @param contentType For example application/json , text/plain.
     * @param queryParameters Optional query parameters that are added to URL. Key in map is parameter name, value in map is
     *            parameter value.
     * @return {@link ApacheHttpClientResponseImpl}.
     * @throws GetException In case there is an exception when invoking request.
     */
    HttpClientResponse<InputStream> get(final String url, final String contentType, final NameValuePair... queryParameters)
        throws GetException;

    /**
     * Invokes PUT call to given URL and returns response as InputStream.
     * 
     * @param url url to which to PUT.
     * @param contentType Content type for PUT. Example of valid content types: application/json, text/plain.
     * @param entity Entity object that will be submitted along with HTTP request. Is optional. Can be <code>null</code>.
     * @param queryParameters Optional query parameters that are added to URL. Key in map is parameter name, value in map is
     *            parameter value.
     * @return Response object.
     * @throws PutException In case something goes wrong with PUT process.
     */
    HttpClientResponse<InputStream> put(final String url, final String contentType, final String entity,
        final NameValuePair... queryParameters) throws PutException;

    /**
     * Invokes a Http POST call .
     * 
     * @param url Url to which to POST.
     * @param contentType Content type for POST. Example of valid content types: application/json, text/plain.
     * @param entity Entity object that will be submitted along with HTTP request. Is optional. Can be <code>null</code>.
     * @param queryParameters Optional query parameters that are added to URL. Key in map is parameter name, value in map is
     *            parameter value.
     * @return Response object.
     * @throws PostException In case something goes wrong with POST process.
     */
    HttpClientResponse<InputStream> post(final String url, final String contentType, final String entity,
        final NameValuePair... queryParameters) throws PostException;

}