package com.github.kristofa.test.http;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Simple {@link ContentMatcher} around byte[] which will do an equals on the byte[] as is.
 * 
 * @see HttpRequestImpl
 * @author kristof
 */
class DefaultContentMatcher extends ContentMatcher {

    private byte[] content;

    DefaultContentMatcher() {

    }

    DefaultContentMatcher(final byte[] content) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContent(final byte[] content) {
        this.content = content;
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
    public boolean equals(final Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj, false);
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
    public ContentMatcher copy() {
        final DefaultContentMatcher proxy = new DefaultContentMatcher();
        if (content != null) {
            proxy.setContent(Arrays.copyOf(content, content.length));
        }
        return proxy;

    }

}
