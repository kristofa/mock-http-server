package com.github.kristofa.test.http;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * {@link HttpResponseProvider} that keeps expected request/responses in memory. Its purpose is to match simple requests that
 * can be easily programmatically defined in code. It supports matching:
 * <p/>
 * <ul>
 * <li>HTTP Method (GET, PUT, POST, DELETE)</li>
 * <li>Path</li>
 * <li>Content-Type http header</li>
 * <li>Request Entity</li>
 * </ul>
 * 
 * @see MockHttpServer
 * @author kristof
 */
public class SimpleHttpResponseProvider implements HttpResponseProvider {

    private LimitedHttpRequestData latestRequest;

    private final Map<LimitedHttpRequestData, HttpResponse> expectedRequests =
        new HashMap<LimitedHttpRequestData, HttpResponse>();
    private final Set<LimitedHttpRequestData> receivedRequests = new HashSet<LimitedHttpRequestData>();

    /**
     * Provide an expected request with content.
     * 
     * @param method HTTP method.
     * @param path Path. Path can contain query parameters, eg: path?a=b&b=c
     * @param contentType Content type.
     * @param requestEntity Request entity as string.
     * @return current {@link SimpleHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleHttpResponseProvider expect(final Method method, final String path, final String contentType,
        final String requestEntity) {
        latestRequest = new LimitedHttpRequestData();
        latestRequest.setMethod(method);
        latestRequest.setPath(path);
        latestRequest.setContent(requestEntity.getBytes());
        latestRequest.setContentType(contentType);

        return this;
    }

    /**
     * Provide an expected request without content.
     * 
     * @param method HTTP method.
     * @param path Path. Path can contain query parameters, eg: path?a=b&b=c
     * @return current {@link SimpleHttpResponseProvider}. Allows chaining calls.
     */
    public SimpleHttpResponseProvider expect(final Method method, final String path) {
        latestRequest = new LimitedHttpRequestData();
        latestRequest.setMethod(method);
        latestRequest.setPath(path);
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
        expectedRequests.put(latestRequest, response);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getResponse(final HttpRequest request) {

        final LimitedHttpRequestData limitedRequest = new LimitedHttpRequestData();
        limitedRequest.setMethod(request.getMethod());
        limitedRequest.setPath(request.getPath());
        limitedRequest.setContent(request.getContent());

        for (final QueryParameter qp : request.getQueryParameters()) {
            limitedRequest.addQueryParam(qp.getKey(), qp.getValue());
        }

        for (final HttpMessageHeader header : request.getHttpMessageHeaders()) {
            if (HttpMessageHeaderField.CONTENTTYPE.getValue().equals(header.getName())) {
                limitedRequest.setContentType(header.getValue());
                break;
            }
        }

        receivedRequests.add(limitedRequest);
        return expectedRequests.get(limitedRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() throws UnsatisfiedExpectationException {

        if (!expectedRequests.keySet().equals(receivedRequests)) {

            final Collection<HttpRequest> missing = new HashSet<HttpRequest>();

            for (final LimitedHttpRequestData expectedRequest : expectedRequests.keySet()) {
                if (!receivedRequests.contains(expectedRequest)) {
                    missing.add(httpRequestFor(expectedRequest));
                }
            }

            final Collection<HttpRequest> unexpected = new HashSet<HttpRequest>();
            for (final LimitedHttpRequestData receivedRequest : receivedRequests) {
                if (!expectedRequests.keySet().contains(receivedRequest)) {
                    unexpected.add(httpRequestFor(receivedRequest));
                }
            }

            throw new UnsatisfiedExpectationException(missing, unexpected);

        }

    }

    /**
     * Reset the response provider to its original state
     */
    public void reset() {
        expectedRequests.clear();
        receivedRequests.clear();
        latestRequest = new LimitedHttpRequestData();
    }

    private HttpRequest httpRequestFor(final LimitedHttpRequestData requestData) {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(requestData.getMethod()).path(requestData.getPath()).content(requestData.getContent());
        if (requestData.getContentType() != null) {
            httpRequestImpl.httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), requestData.getContentType());
        }
        return httpRequestImpl;
    }

    private class LimitedHttpRequestData {

        private Method method;
        private String path;
        private String contentType;
        private byte[] content;
        private final Set<QueryParameter> queryParameters = new TreeSet<QueryParameter>();

        public Method getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getContent() {
            return content;
        }

        public void setMethod(final Method method) {
            this.method = method;
        }

        public void setPath(final String path) {

            this.path = extractQueryParams(path);
        }

        public void addQueryParam(final String name, final String value) {
            queryParameters.add(new QueryParameter(name, value));
        }

        public void setContentType(final String contentType) {
            this.contentType = contentType;
        }

        public void setContent(final byte[] content) {
            this.content = content;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, false);
        }

        private String extractQueryParams(final String path) {

            final int indexOfQuestionMark = path.indexOf("?");
            if (indexOfQuestionMark >= 0) {
                final String newPath = path.substring(0, indexOfQuestionMark);
                final String queryParams = path.substring(indexOfQuestionMark + 1);
                final List<NameValuePair> parameters = URLEncodedUtils.parse(queryParams, Charset.forName("UTF-8"));
                for (final NameValuePair parameter : parameters) {
                    queryParameters.add(new QueryParameter(parameter.getName(), parameter.getValue()));
                }

                return newPath;
            }
            return path;
        }

    }

}
