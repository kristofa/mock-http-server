package com.harlap.test.http.client;

/**
 * Class holding URL based methods.
 */
public interface UrlBuilder {

    /**
     * Converts a raw URL to an encoded version.
     * 
     * @param baseUrl base URL
     * @param parameters open array of {@link NameValuePair}s.
     * @return encoded URL
     */
    String buildAndEncodeUrl(final String baseUrl, final NameValuePair... parameters);

}
