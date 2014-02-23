package com.github.kristofa.test.http;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Simple wrapper around byte[].
 * 
 * @see HttpRequestImpl
 * @author kristof
 */
class DefaultContentDecorator extends ContentDecorator {

    private byte[] content;

    DefaultContentDecorator() {

    }

    DefaultContentDecorator(final byte[] content) {
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
    public ContentDecorator copy() {
        final DefaultContentDecorator proxy = new DefaultContentDecorator();
        if (content != null) {
            proxy.setContent(Arrays.copyOf(content, content.length));
        }
        return proxy;

    }

}
