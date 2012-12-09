package com.github.kristofa.test.http;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * HttpResponse implementation.
 * 
 * @author kristof
 */
public class HttpResponseImpl implements HttpResponse {

    private final int httpCode;
    private final String contentType;
    private final byte[] content;

    /**
     * Creates a new instance.
     * 
     * @param httpCode http response code.
     * @param contentType Content type, can be <code>null</code>.
     * @param content Content, can be <code>null</code>.
     */
    public HttpResponseImpl(final int httpCode, final String contentType, final byte[] content) {
        this.httpCode = httpCode;
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHttpCode() {
        return httpCode;
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
    public byte[] getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Http code: " + getHttpCode() + ", Content Type: " + (getContentType() == null ? "null" : getContentType())
            + ", Content: " + (getContent() == null ? "null" : new String(getContent()));
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

}
