package com.harlap.test.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HttpRequestImpl implements HttpRequest {

    private static final String NOT_SPECIFIED = "null";

    private Method method;
    private String contentType;
    private String content;
    private String path;
    private final Set<QueryParameter> queryParameters = new HashSet<QueryParameter>();

    public HttpRequestImpl() {
        // Default constructor.
    }

    public HttpRequestImpl(final HttpRequest request) {
        content = request.getContent();
        contentType = request.getContentType();
        method = request.getMethod();
        path = request.getPath();
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
     * Sets content type of message body for request.
     * 
     * @param contentType Content type of message body for request.
     * @return This http request.
     */
    public HttpRequestImpl contentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Sets content of message body for request.
     * 
     * @param content Message body for request.
     * @return This http request.
     */
    public HttpRequestImpl content(final String content) {
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
     * @param key Parameter key. Should not be empty or <code>null</code>.
     * @param value Parameter value. Should not be empty or <code>null</code>
     * @return This http request.
     */
    public HttpRequestImpl queryParameter(final String key, final String value) {
        queryParameters.add(new QueryParameter(key, value));
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
    public String getContentType() {
        return contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContent() {
        return content;
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
        final String contentTypeString = add("Content-Type: ", getContentType());
        final String pathString = add("Path: ", getPath());
        final String queryParamsString = add("Query Parameters: ", getQueryParamsAsString());
        final String contentString = add("Content:\n", getContent());

        final String[] array = {methodString, contentTypeString, pathString, queryParamsString, contentString};

        return StringUtils.join(array, "\n");
    }

    private String add(final String value, final Object object) {
        if (object != null) {
            return value + object;
        }
        return value + NOT_SPECIFIED;
    }

    private String getQueryParamsAsString() {

        String queryParamsAsString = "";
        boolean first = true;
        for (final QueryParameter parameter : getQueryParameters()) {
            if (first) {
                first = false;
            } else {
                queryParamsAsString += "&";
            }
            queryParamsAsString += parameter.toString();
        }
        return queryParamsAsString;
    }
}
