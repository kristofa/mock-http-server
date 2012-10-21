package com.harlap.test.http.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

/**
 * {@link HttpClient} implementation that uses <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HTTP
 * client</a> as 'lower level' library. For all requests 'Content-Encoding' header is added with value UTF-8.
 * 
 * @author kristof
 */
public class ApacheHttpClientImpl implements HttpClient {

    private final UrlBuilder urlBuilder;

    public ApacheHttpClientImpl() {
        this(new DefaultUrlBuilder());
    }

    public ApacheHttpClientImpl(final UrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClientResponse<InputStream> get(final String url, final String contentType,
        final NameValuePair... queryParameters) throws GetException {

        final String urlWithParameters = urlBuilder.buildAndEncodeUrl(url, queryParameters);

        final HttpGet httpGet = new HttpGet(urlWithParameters);
        if (contentType != null) {
            httpGet.addHeader(HTTP.CONTENT_TYPE, contentType);
        }

        try {
            return execute(httpGet, contentType);
        } catch (final IOException e1) {
            throw new GetException(e1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClientResponse<InputStream> put(final String url, final String contentType, final String entity,
        final NameValuePair... queryParameters) throws PutException {

        final String urlWithParameters = urlBuilder.buildAndEncodeUrl(url, queryParameters);

        final HttpPut httpPut = new HttpPut(urlWithParameters);
        if (contentType != null) {
            httpPut.addHeader(HTTP.CONTENT_TYPE, contentType);
        }

        try {
            return execute(httpPut, contentType, entity);
        } catch (final IOException e) {
            throw new PutException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClientResponse<InputStream> post(final String url, final String contentType, final String entity,
        final NameValuePair... queryParameters) throws PostException {

        final String urlWithParameters = urlBuilder.buildAndEncodeUrl(url, queryParameters);

        final HttpPost httpPost = new HttpPost(urlWithParameters);
        if (contentType != null) {
            httpPost.addHeader(HTTP.CONTENT_TYPE, contentType);
        }

        try {
            return execute(httpPost, contentType, entity);
        } catch (final IOException e) {
            throw new PostException(e);
        }
    }

    /**
     * Gets a new HTTPClient instance. Introduced to facilitate testing.
     * 
     * @return A new HTTPClient instance.
     */
    /* package */org.apache.http.client.HttpClient getClient() {
        return new DefaultHttpClient();
    }

    private HttpClientResponse<InputStream> execute(final HttpEntityEnclosingRequestBase request, final String contentType,
        final String entity) throws IOException {

        if (!StringUtils.isBlank(entity)) {
            final StringEntity stringEntity = new StringEntity(entity, contentType, HTTP.UTF_8);
            request.setEntity(stringEntity);
        }
        return execute(request, contentType);
    }

    private HttpClientResponse<InputStream> execute(final HttpRequestBase request, final String contentType)
        throws IOException {
        final org.apache.http.client.HttpClient client = getClient();
        try {
            final HttpResponse httpResponse = client.execute(request);
            return buildResponse(client, httpResponse);
        } catch (final IOException e) {
            client.getConnectionManager().shutdown(); // In case of exception we should close connection manager here.
            throw e;
        }
    }

    private HttpClientResponse<InputStream> buildResponse(final org.apache.http.client.HttpClient client,
        final HttpResponse response) throws IOException {
        final int status = response.getStatusLine().getStatusCode();
        final ApacheHttpClientResponseImpl<InputStream> httpResponse =
            new ApacheHttpClientResponseImpl<InputStream>(status, client);
        httpResponse.setResponseEntity(response.getEntity().getContent());
        if (response.getEntity().getContentType() != null) {
            httpResponse.setContentType(response.getEntity().getContentType().getValue());
        }
        if (status < 200 || status > 299) {
            // In this case response will be the error message.
            httpResponse.setErrorMessage("Got HTTP return code " + status);
        }
        return httpResponse;
    }
}
