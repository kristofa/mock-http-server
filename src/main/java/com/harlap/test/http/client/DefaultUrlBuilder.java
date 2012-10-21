package com.harlap.test.http.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * Default URL encoding
 */
public class DefaultUrlBuilder implements UrlBuilder {

    private static final String UTF_8 = "UTF-8";

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildAndEncodeUrl(final String baseUrl, final NameValuePair... parameters) {
        if (parameters == null || parameters.length == 0) {
            return baseUrl;
        }
        final String newUrl = baseUrl + "?";
        final List<org.apache.http.NameValuePair> qparams = new ArrayList<org.apache.http.NameValuePair>();
        for (final NameValuePair parameter : parameters) {
            qparams.add(new BasicNameValuePair(parameter.getName(), parameter.getValue()));
        }
        return newUrl + URLEncodedUtils.format(qparams, UTF_8);
    }

}
