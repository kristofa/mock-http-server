package com.harlap.test.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HttpRequestImpl implements HttpRequest {

    private static final String NOT_SPECIFIED = "null";

    private Method method;
    private byte[] content;
    private String path;
    private final Set<QueryParameter> queryParameters = new HashSet<QueryParameter>();
    private final Set<HttpMessageHeader> httpMessageHeaders = new HashSet<HttpMessageHeader>();

    public HttpRequestImpl() {
        // Default constructor.
    }

    public HttpRequestImpl(final HttpRequest request) {
        content = request.getContent();
        method = request.getMethod();
        path = request.getPath();

        for (final HttpMessageHeader header : request.getHttpMessageHeaders()) {
            httpMessageHeaders.add(new HttpMessageHeader(header.getName(), header.getValue()));
        }

        for (final QueryParameter parameter : request.getQueryParameters()) {
            queryParameters.add(new QueryParameter(parameter.getKey(), parameter.getValue()));
        }
    }

    /**
     * Sets method for request.
     * 
     * @param method Method for request.
     * @return This http request.
     */
    public HttpRequestImpl method(final Method method) {
        this.method = method;
        return this;
    }

    /**
     * Sets content of message body for request.
     * 
     * @param content Message body for request.
     * @return This http request.
     */
    public HttpRequestImpl content(final byte[] content) {
        this.content = content;
        return this;
    }

    /**
     * Sets path for request.
     * 
     * @param path Sets path for request.
     * @return This http request.
     */
    public HttpRequestImpl path(final String path) {
        this.path = path;
        return this;
    }

    /**
     * Adds a query parameter for request.
     * 
     * @param key Parameter key. Should not be empty or <code>null</code> or blank.
     * @param value Parameter value. Should not be empty or <code>null</code> or blank.
     * @return This http request.
     */
    public HttpRequestImpl queryParameter(final String key, final String value) {
        queryParameters.add(new QueryParameter(key, value));
        return this;
    }

    /**
     * Adds a Http message header.
     * 
     * @param name header name. Should not be <code>null</code> or blank.
     * @param value header value. Should not be <code>null</code> or blank.
     * @return The http request.
     */
    public HttpRequestImpl httpMessageHeader(final String name, final String value) {
        httpMessageHeaders.add(new HttpMessageHeader(name, value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getMethod() {
        return method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent() {
        if (content == null) {
            return null;
        }
        return Arrays.copyOf(content, content.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QueryParameter> getQueryParameters() {
        return Collections.unmodifiableSet(queryParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMessageHeader> getHttpMessageHeaders() {
        return Collections.unmodifiableSet(httpMessageHeaders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

    @Override
    public String toString() {

        final String methodString = add("Method: ", getMethod());
        final String messageHeaderString = add("Message Header: ", getHttpMessageHeaders());
        final String pathString = add("Path: ", getPath());
        final String queryParamsString = add("Query Parameters: ", getQueryParameters());
        String contentString = null;
        if (getContent() == null) {
            contentString = add("Content:\n", getContent());
        } else {
            contentString = add("Content:\n", new String(getContent()));
        }

        final String[] array = {methodString, messageHeaderString, pathString, queryParamsString, contentString};

        return StringUtils.join(array, "\n");
    }

    private String add(final String value, final Object object) {
        if (object != null) {
            return value + object;
        }
        return value + NOT_SPECIFIED;
    }

}
