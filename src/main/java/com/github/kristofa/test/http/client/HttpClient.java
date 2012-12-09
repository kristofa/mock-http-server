package com.github.kristofa.test.http.client;

import java.io.InputStream;

import com.github.kristofa.test.http.FullHttpRequest;

/**
 * A higher level HTTP Client interface that abstracts the low level library that is being used.
 * 
 * @author kristof
 */
public interface HttpClient {

    /**
     * Executes a HTTP request and returns response.
     * 
     * @param request HTTP request.
     * @return Response.
     * @throws HttpRequestException In case request failed.
     */
    HttpClientResponse<InputStream> execute(final FullHttpRequest request) throws HttpRequestException;

}