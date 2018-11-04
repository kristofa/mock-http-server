package com.github.kristofa.test.http;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * {@link HttpResponseProvider} that keeps expected request/responses in memory. Its purpose is to match simple requests that
 * can be easily programmatically defined in code. It supports matching:
 *
 * <ul>
 * <li>HTTP Method (GET, PUT, POST, DELETE)</li>
 * <li>Path (including query parmeters)</li>
 * <li>Content-Type http header</li>
 * <li>Request Entity</li>
 * </ul>
 * It does not support other http headers than Content-Type. If other http header parameters are present in HttpRequest they
 * will be ignored.
 * 
 * @see MockHttpServer
 * @author kristof
 */
public class SimpleHttpResponseProvider extends AbstractHttpResponseProvider {

    private static final String CONTENT_TYPE_HTTP_HEADER_NAME = "Content-Type";
    private HttpRequestImpl latestRequest;

    public SimpleHttpResponseProvider() {
        addHttpRequestMatchingFilter(new AllExceptContentTypeHeaderFilter());
    }

    /**
     * Provide an expected request with content.
     * 
     * @param method HTTP method.
     * @param path Path
     * @param contentType Content type.
     * @param requestEntity Request entity as string.
     * @return current {@link SimpleHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleHttpResponseProvider expect(final Method method, final String path, final String contentType,
        final String requestEntity) {

        latestRequest = new HttpRequestImpl();
        latestRequest.method(method).content(requestEntity.getBytes())
            .httpMessageHeader(CONTENT_TYPE_HTTP_HEADER_NAME, contentType);
        extractAndSetQueryParams(latestRequest, path);
        return this;
    }

    /**
     * Provide an expected request without content.
     * 
     * @param method HTTP method.
     * @param path Path.
     * @return current {@link SimpleHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleHttpResponseProvider expect(final Method method, final String path) {
        latestRequest = new HttpRequestImpl();
        latestRequest.method(method);
        extractAndSetQueryParams(latestRequest, path);
        return this;
    }

    /**
     * Provide expected response for latest given request.
     * 
     * @param httpCode Http response code.
     * @param contentType Content type.
     * @param requestEntity Data.
     * @return current {@link SimpleHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleHttpResponseProvider respondWith(final int httpCode, final String contentType, final String requestEntity) {
        final HttpResponseImpl response =
            new HttpResponseImpl(httpCode, contentType, requestEntity == null ? null : requestEntity.getBytes());
        addExpected(latestRequest, new DefaultHttpResponseProxy(response));
        return this;
    }

    /**
     * Reset the response provider to its original state
     */
    public void reset() {
        resetState();
    }

    private void extractAndSetQueryParams(final HttpRequestImpl request, final String path) {

        final int indexOfQuestionMark = path.indexOf("?");
        if (indexOfQuestionMark >= 0) {
            final String newPath = path.substring(0, indexOfQuestionMark);
            final String queryParams = path.substring(indexOfQuestionMark + 1);
            final List<NameValuePair> parameters = URLEncodedUtils.parse(queryParams, Charset.forName("UTF-8"));
            for (final NameValuePair parameter : parameters) {
                request.queryParameter(parameter.getName(), parameter.getValue());
            }
            request.path(newPath);
        } else {
            request.path(path);
        }
    }

}
