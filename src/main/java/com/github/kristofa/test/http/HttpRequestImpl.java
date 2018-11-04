package com.github.kristofa.test.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HttpRequestImpl implements HttpRequest {

    private static final String NOT_SPECIFIED = "null";

    private Method method;
    private ContentMatcher contentMatcher;
    private String path;
    private final Set<QueryParameter> queryParameters = new TreeSet<QueryParameter>();
    private final Set<HttpMessageHeader> httpMessageHeaders = new TreeSet<HttpMessageHeader>();

    /**
     * Creates a new unintialized instance.
     */
    public HttpRequestImpl() {
        contentMatcher = new DefaultContentMatcher();
    }

    /**
     * Copy constructor. Will initialize http request with content of given request.
     * 
     * @param request Request to copy.
     */
    public HttpRequestImpl(final HttpRequest request) {

        if (request instanceof HttpRequestImpl) {
            final ContentMatcher otherMatcher = ((HttpRequestImpl)request).contentMatcher;
            if (otherMatcher != null) {
                contentMatcher = otherMatcher.copy();
            }
        } else {
            contentMatcher = new DefaultContentMatcher();
            try {
                contentMatcher.setContent(request.getContent());
            } catch (final UnexpectedContentException e) {
                throw new IllegalStateException(e);
            }
        }
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
        try {
            contentMatcher.setContent(content);
        } catch (final UnexpectedContentException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }

    /**
     * Sets {@link ContentMatcher}. It will be initialized with any existing data.
     * 
     * If not set a default {@link ContentMatcher} will be used which simply does an equals on byte array.
     * 
     * @param matcher New Content matcher. Should not be <code>null</code>.
     * @return This http request.
     * @throws UnexpectedContentException In case the current content is incompatible with the new ContentMatcher. In this
     *             case the new matcher will not be set. The previous {@link ContentMatcher} will remain active.
     */
    public HttpRequestImpl contentMatcher(final ContentMatcher matcher) throws UnexpectedContentException {
        Validate.notNull(matcher);
        final byte[] content = contentMatcher.getContent();
        if (content != null) {
            matcher.setContent(content);
            contentMatcher = matcher;
        } else {
            contentMatcher = matcher;
        }
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
     * Removes Query Parameter with given key and value.
     * 
     * @param key Query parameter key.
     * @param value Query parameter value.
     * @return This http request.
     */
    public HttpRequestImpl removeQueryParameter(final String key, final String value) {
        queryParameters.remove(new QueryParameter(key, value));
        return this;
    }

    /**
     * Removes all Query Parameters that have given key.
     * 
     * @param key Query parameter key.
     * @return This http request.
     */
    public HttpRequestImpl removeQueryParameters(final String key) {
        final Set<QueryParameter> toRemove = new HashSet<QueryParameter>();

        for (final QueryParameter qp : queryParameters) {
            if (qp.getKey().equals(key)) {
                toRemove.add(qp);
            }
        }
        queryParameters.removeAll(toRemove);
        return this;
    }

    /**
     * Adds a Http message header.
     * 
     * @param name header name. Should not be <code>null</code> or blank.
     * @param value header value. Should not be <code>null</code> or blank.
     * @return This http request.
     */
    public HttpRequestImpl httpMessageHeader(final String name, final String value) {
        httpMessageHeaders.add(new HttpMessageHeader(name, value));
        return this;
    }

    /**
     * Removes Http message header with given name and value.
     * 
     * @param name Http message header name.
     * @param value Http message header value.
     * @return This http request.
     */
    public HttpRequestImpl removeHttpMessageHeader(final String name, final String value) {
        httpMessageHeaders.remove(new HttpMessageHeader(name, value));
        return this;
    }

    /**
     * Removes all Http message headers with given name.
     * 
     * @param name Http message header name.
     * @return This http request.
     */
    public HttpRequestImpl removeHttpMessageHeaders(final String name) {
        final Set<HttpMessageHeader> toRemove = new HashSet<HttpMessageHeader>();

        for (final HttpMessageHeader header : httpMessageHeaders) {
            if (header.getName().equals(name)) {
                toRemove.add(header);
            }
        }

        httpMessageHeaders.removeAll(toRemove);
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
        if (contentMatcher == null) {
            return null;
        }
        final byte[] content = contentMatcher.getContent();
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
    public Set<QueryParameter> getQueryParameters(final String key) {
        Validate.notBlank(key);
        final Set<QueryParameter> qpSubset = new TreeSet<QueryParameter>();
        for (final QueryParameter qp : queryParameters) {
            if (qp.getKey().equals(key)) {
                qpSubset.add(qp);
            }
        }
        return qpSubset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMessageHeader> getHttpMessageHeaders(final String name) {
        Validate.notBlank(name);
        final Set<HttpMessageHeader> mhSubset = new TreeSet<HttpMessageHeader>();
        for (final HttpMessageHeader header : httpMessageHeaders) {
            if (header.getName().equals(name)) {
                mhSubset.add(header);
            }
        }
        return mhSubset;
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
