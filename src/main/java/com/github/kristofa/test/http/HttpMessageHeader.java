package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents http message header entry.
 * 
 * @author kristof
 */
public class HttpMessageHeader implements Comparable<HttpMessageHeader> {

    private final String name;
    private final String value;

    /**
     * Create a new header entry instance.
     * 
     * @param name Header entry name. Should not be <code>null</code> or blank.
     * @param value Header entry value. Should not be <code>null</code> or blank.
     */
    public HttpMessageHeader(final String name, final String value) {
        Validate.notBlank(name, "HttpHeader name is blank.");
        Validate.notBlank(value, "HttpHeader value for name " + name + " is blank");

        this.name = name;
        this.value = value;
    }

    /**
     * Get header entry name.
     * 
     * @return Header entry name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get header entry value.
     * 
     * @return Header entry value.
     */
    public String getValue() {
        return value;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final HttpMessageHeader o) {
        final int compareTo = getName().compareTo(o.getName());
        if (compareTo != 0) {
            return compareTo;
        }
        return getValue().compareTo(o.getValue());
    }

}
