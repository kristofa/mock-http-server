package com.github.kristofa.test.http;

/**
 * The media type is the value of Content-Type http message header and is defined of 2 or more parts. A main type, sub type
 * and zero or more optional sub types. For example, subtypes of application/json have an optional charset parameter that can
 * be included to indicate the character encoding eg: application/json; charset=UTF-8.
 * 
 * @author kristof
 */
public enum MediaType {

    APPLICATION_JSON_UTF8("application/json; charset=UTF-8");

    private final String value;

    private MediaType(final String value) {
        this.value = value;
    }

    /**
     * Get value of MediaType.
     * 
     * @return String value of MediaType.
     */
    public String getValue() {
        return value;
    }

}
