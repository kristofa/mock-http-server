package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;

/**
 * Defines a query parameter key/value.
 * <p>
 * In following url: http://localhost:8081/persons?name=Smith&gender=female there are 2 query parameters: name=Smith and
 * gender=female.
 * 
 * @author kristof
 */
public class QueryParameter {

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
        Validate.notBlank(value);
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
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryParameter other = (QueryParameter)obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return key + "=" + value;
    }

}
