package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Defines a query parameter key/value.
 *
 * @author kristof
 */
public class QueryParameter implements Comparable<QueryParameter> {

    private final String key;
    private final String value;

    /**
     * Creates a new instance.
     * 
     * @param key Key, should not be empty.
     * @param value Value, should not be empty.
     */
    public QueryParameter(final String key, final String value) {
        Validate.notBlank(key);
        Validate.notNull(value);
        this.key = key;
        this.value = value;
    }

    /**
     * Gets key.
     * 
     * @return Key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets value for key.
     * 
     * @return Value for key.
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
        return key + "=" + value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final QueryParameter o) {
        final int compareTo = getKey().compareTo(o.getKey());
        if (compareTo != 0) {
            return compareTo;
        }
        return getValue().compareTo(o.getValue());
    }

}
