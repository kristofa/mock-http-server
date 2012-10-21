package com.harlap.test.http.client;

import org.apache.commons.lang3.Validate;

/**
 * Name/Value pair that can be used as parameter for query part of url.
 * 
 * @author kristof
 */
public class NameValuePair {

    private final String name;
    private final String value;

    /**
     * Constructor.
     * 
     * @param name Name should not be empty.
     * @param value Value should not be empty.
     */
    public NameValuePair(final String name, final String value) {
        super();
        Validate.notEmpty(name);
        Validate.notEmpty(value);
        this.name = name;
        this.value = value;
    }

    /**
     * Gets name.
     * 
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets value.
     * 
     * @return Value.
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
        result = prime * result + name.hashCode();
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
        final NameValuePair other = (NameValuePair)obj;
        if (!name.equals(other.name)) {
            return false;
        }
        if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
