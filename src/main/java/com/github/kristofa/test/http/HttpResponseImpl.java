package com.github.kristofa.test.http;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HttpResponseImpl implements HttpResponse {

    private final int httpCode;
    private final String contentType;
    private final byte[] content;

    public HttpResponseImpl(final int httpCode, final String contentType, final byte[] content) {
        this.httpCode = httpCode;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Http code: " + getHttpCode() + ", Content Type: " + (getContentType() == null ? "null" : getContentType())
            + ", Content: " + (getContent() == null ? "null" : new String(getContent()));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

}
