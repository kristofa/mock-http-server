package com.harlap.test.http.client;

import java.io.InputStream;

import com.harlap.test.http.FullHttpRequest;

/**
 * A higher level HTTP Client interface that abstracts the low level library that is being used.
 * 
 * @author kristof
 */
public interface HttpClient {

    HttpClientResponse<InputStream> execute(final FullHttpRequest request) throws HttpRequestException;

}